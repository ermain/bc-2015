package dronespam.robots;
import battlecode.common.*;
import dronespam.Const;

public class Drone extends OurRobot{
	
	int attacking = 1;
	int retreatTimer = 0;
    Direction lastDirection = null;

	public Drone(RobotController rc) {
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
			if (attacking == 0 && mRc.getSupplyLevel() < Const.minSupplyThreshold) {
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
				}
			}
			Direction d = myLoc.directionTo(target);
			tryMove(d);
		}
	}
}
