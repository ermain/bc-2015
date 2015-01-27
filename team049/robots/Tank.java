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
		state = State.DEFENDING;
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		if (mRc.isWeaponReady()) {
			attackSomething();
		}
		if (mRc.isCoreReady()) {
			MapLocation target;
			MapLocation myLoc = mRc.getLocation();

			attacking = mRc.readBroadcast(Const.attackingPos);
			double supply = mRc.getSupplyLevel();
			int targetX = mRc.readBroadcast(Const.targetLocXPos);
			int targetY = mRc.readBroadcast(Const.targetLocYPos);
			target = new MapLocation(targetX, targetY);
			int distToTarget = myLoc.distanceSquaredTo(target);

			/*if (state == State.DEFENDING && supply < Const.minTankSupplyThreshold) {
				state = State.RESUPPLYING;
			} else */if (state == State.DEFENDING && attacking == 1) {
				state = State.EN_ROUTE;
			} else if (attacking == 0) {
				state = State.DEFENDING;
			} else if (state == State.EN_ROUTE && attacking == 1 && distToTarget <= Const.tankAttackZone) {
				state = State.ATTACKING;
			} else if (state == State.RESUPPLYING && supply > Const.minTankSupplyThreshold) {
				if (attacking == 1) {
					state = State.EN_ROUTE;
				} else {
					state = State.DEFENDING;
				}
			}
			
			mRc.setIndicatorString(0,state.toString());
			
			
			if (state == State.RESUPPLYING) {
				Direction d = myLoc.directionTo(myHQLoc);
				tryMove(d);
				return;
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
				if (state == State.DEFENDING && myLoc.distanceSquaredTo(target) <= 10) {
					int fate = rand.nextInt(5);
					if (fate != 1) { // conserve supply by moving with 1/5 probability
						mRc.yield();
						return;
					}
				}
				Direction d = myLoc.directionTo(target);
				tryMove(d);
			} else if (closestRobot.type == RobotType.TOWER || closestRobot.type == RobotType.HQ) {
				if (state == State.ATTACKING || state == State.EN_ROUTE) {
					Direction d = myLoc.directionTo(closest);
					tryMove(d);
				} else {
					Direction d = myLoc.directionTo(target);
					tryMove(d);
				}
			} else if (myLoc.distanceSquaredTo(closest) > Const.tankAttackZone) {
				Direction d = myLoc.directionTo(closest);
				tryMove(d);
			} else {
				mRc.yield();
			}
			
		}
	}
}
