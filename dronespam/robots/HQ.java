package dronespam.robots;
import java.util.Random;

import dronespam.Const;
import battlecode.common.*;

public class HQ extends OurRobot {
			
	int attacking = 1;
	int retreatTimer = 0;
    Direction lastDirection = null;
	
	public HQ(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
		try {
			mRc.broadcast(Const.attackingPos, 1);
		} catch (Exception e) {
            System.out.println("Unexpected exception");
            e.printStackTrace();
		}
	}
	public void doTurn() throws GameActionException {
		int fate = rand.nextInt(10000);
		myRobots = mRc.senseNearbyRobots(999999, myTeam);
		enemyTowers = mRc.senseEnemyTowerLocations();
		myTowers = mRc.senseTowerLocations();
		
		MapLocation curTarget = new MapLocation(mRc.readBroadcast(Const.targetLocXPos), mRc.readBroadcast(Const.targetLocYPos));
		int prevAttacking = attacking;
		attacking = mRc.readBroadcast(Const.attackingPos);
		if (prevAttacking == 0 && attacking == 1) {
			//attack recently initiated; set retreat timer
			retreatTimer = Const.minTimeToRetreat;
		} else {
			retreatTimer--;
		}
		
		
		int numDrones = 0;
		int numDronesAtTarget = 0;
		int numBeavers = 0;
		int numHelipads = 0;
		int numDepots = 0;
		int numFactories = 0;
		int numMiners = 0;
		for (RobotInfo r : myRobots) {
			RobotType type = r.type;
			if (type == RobotType.DRONE) {
				numDrones++;
				if (r.location.distanceSquaredTo(curTarget) < Const.distThreshold) {
					numDronesAtTarget++;					
				}
			} else if (type == RobotType.BEAVER) {
				numBeavers++;
			} else if (type == RobotType.HELIPAD) {
				numHelipads++;
			} else if (type == RobotType.SUPPLYDEPOT) {
				numDepots++;
			} else if (type == RobotType.MINERFACTORY) {
				numFactories++;
			} else if (type == RobotType.MINER) {
				numMiners++;
			}
		}
		mRc.broadcast(Const.numBeaverPos, numBeavers);
		mRc.broadcast(Const.numDronePos, numDrones);
		mRc.broadcast(Const.numDepotPos, numDepots);
		mRc.broadcast(Const.numHelipadPos, numHelipads);
		mRc.broadcast(Const.numFactoryPos, numFactories);
		mRc.broadcast(Const.numMinerPos, numMiners);

		

		determineTarget(enemyHQLoc, myHQLoc, enemyTowers, myTowers, numDrones, numDronesAtTarget, attacking, retreatTimer);

		handleSupply(mRc);
		
		if (mRc.isWeaponReady()) {
			attackSomething();
		}

		if (mRc.isCoreReady() && mRc.getTeamOre() >= 100 && numBeavers < Const.maxBeavers) {
			trySpawn(Const.directions[rand.nextInt(8)], RobotType.BEAVER);
		}
	}
	
	
	void handleSupply(RobotController rc)  throws GameActionException {
		RobotInfo[] myRobots = rc.senseNearbyRobots(15); // 15 is the supply transfer distance
		for (RobotInfo r : myRobots) {
			double supply = r.supplyLevel;
			double mySupply = rc.getSupplyLevel();
			
			if (r.team != myTeam) continue;
	
			if (r.type == RobotType.DRONE && supply < Const.droneSupplyThreshold && mySupply >= Const.droneSupplyThreshold - supply) {
				rc.transferSupplies((int)(Const.droneSupplyThreshold - supply), r.location);
			} else if (supply < Const.otherSupplyThreshold && mySupply >= Const.otherSupplyThreshold - supply) {
				rc.transferSupplies((int)(Const.otherSupplyThreshold - supply), r.location);
			}
		}
	}
	
	static boolean determineTarget(MapLocation enemyLoc, MapLocation myLoc, MapLocation [] enemyTowers, MapLocation [] myTowers, 
			int numDrones, int numDronesAtTarget, int attacking, int retreatTimer) throws GameActionException, NullPointerException {
		int minDist = 999999;
		MapLocation [] towers = null;
		MapLocation loc = null;
		
		int oldTargetX = mRc.readBroadcast(Const.targetLocXPos);
		int oldTargetY = mRc.readBroadcast(Const.targetLocYPos);
		MapLocation oldTarget = new MapLocation(oldTargetX, oldTargetY);
		MapLocation target = null;
		
		boolean callRetreat = (attacking == 1) && (numDronesAtTarget < Const.massMinThreshold) && (retreatTimer <= 0);
		
		if (callRetreat) { // initiate retreat
			loc = enemyLoc;
			towers = myTowers;
			attacking = 0;
		} else if (enemyTowers.length <= 3) {
			if (numDronesAtTarget > Const.massHQThreshold) { // attack their HQ
				mRc.broadcast(Const.targetLocXPos, enemyLoc.x);
				mRc.broadcast(Const.targetLocYPos, enemyLoc.y);
				mRc.broadcast(Const.attackingPos, 1);
				return true;
			} else if (attacking == 1 && oldTarget.compareTo(enemyLoc) != 0) { // call an automatic retreat before final push
				loc = enemyLoc;
				towers = myTowers;
				attacking = 0;
			} else {
				return false;
			}
		} else if (attacking == 1 || numDronesAtTarget > Const.massMaxThreshold) { // initiate attack
			loc = myLoc;
			towers = enemyTowers;
			attacking = 1;
		} else { // status quo
			return false;
		}
			
		for (MapLocation l : towers) {
			int dist = l.distanceSquaredTo(loc);
			if (dist < minDist) {
				minDist = dist;
				target = l;
			}
		}
		mRc.broadcast(Const.targetLocXPos, target.x);
		mRc.broadcast(Const.targetLocYPos, target.y);
		mRc.broadcast(Const.attackingPos, attacking);
		return true;
	}
}
