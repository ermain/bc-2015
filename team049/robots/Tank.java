package team049.robots;

import team049.Const;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Tank extends OurRobot {
	int attacking = 1;
	int retreatTimer = 0;
    Direction lastDirection = null;

	public Tank(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		if (mRc.isWeaponReady()) {
			attackSomething();
		}
		if (mRc.isCoreReady()) {
			MapLocation target;
			attacking = mRc.readBroadcast(Const.attackingPos);
			if (mRc.getSupplyLevel() == 0 || (attacking == 0 && mRc.getSupplyLevel() < Const.minTankSupplyThreshold)) {
				target = myHQLoc;
			} else {
				int targetX = mRc.readBroadcast(Const.targetLocXPos);
				int targetY = mRc.readBroadcast(Const.targetLocYPos);
				target = new MapLocation(targetX, targetY);
			}
			
			MapLocation myLoc = mRc.getLocation();
			if (attacking == 0 && myLoc.distanceSquaredTo(target) <= 5) {
				int fate = rand.nextInt(5);
				if (fate != 1) { // conserve supply by moving with 1/5 probability
					mRc.yield();
					return;
				}
			}
			
			RobotInfo[] enemyRobots = mRc.senseNearbyRobots(Const.sightRange, enemyTeam);
			MapLocation closest = null;
			RobotInfo closestRobot = null;
			for (RobotInfo r : enemyRobots) {
				//if (r.type == RobotType.TOWER || r.type == RobotType.HQ) continue; // need to surround towers/HQ
				if (closest == null) {
					closest = r.location;
					closestRobot = r;
				} else if (myLoc.distanceSquaredTo(closest) > myLoc.distanceSquaredTo(r.location)) {
					closest = r.location;
					closestRobot = r;
				}
			}
			
			if (closest == null) {
				Direction d = myLoc.directionTo(target);
				tryMove(d);
			} else if (closestRobot.type == RobotType.TOWER || closestRobot.type == RobotType.HQ) {
				Direction d = myLoc.directionTo(closest);
				tryMove(d);
			} else if (myLoc.distanceSquaredTo(closest) > Const.tankAttackZone) {
				Direction d = myLoc.directionTo(closest);
				tryMove(d);
			} else {
				mRc.yield();
			}
		}
	}
}
