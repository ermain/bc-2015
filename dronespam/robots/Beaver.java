package dronespam.robots;
import dronespam.Const;
import battlecode.common.*;

public class Beaver extends OurRobot {
	public Beaver(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		MapLocation myLoc = mRc.getLocation();
		if (mRc.isWeaponReady()) {
			attackSomething();
		}
		if (mRc.isCoreReady()) {
			if (mRc.getTeamOre() >= 300 && myLoc.distanceSquaredTo(myHQLoc) <= 36) {
				tryBuild(myLoc.directionTo(myHQLoc),RobotType.HELIPAD);	
			} else if (mRc.getTeamOre() >= 100 && mRc.readBroadcast(2) == 0) {
				tryBuild(Const.directions[rand.nextInt(8)], RobotType.SUPPLYDEPOT);
			} else if (mRc.senseOre(myLoc) >= 40) {
				mRc.mine();
			} else {
				moveTowardsOre(mRc);
			}
		}
	}
	
	void moveTowardsOre(RobotController rc) throws GameActionException {
		MapLocation myLoc = rc.getLocation();
		MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(myLoc, rc.getType().sensorRadiusSquared);
		double maxOre = rc.senseOre(myLoc) + .01; // stay and mine if higher than surrounding
		int numMax = 1;
		MapLocation deposit = null;
		for (MapLocation l : locs) {
			if (rc.canSenseLocation(l) && rc.senseRobotAtLocation(l) != null) continue;
			double ore = rc.senseOre(l);
			if (ore > maxOre) {
				maxOre = ore;
				deposit = l;
				numMax = 1;
			} else if (ore == maxOre) {
				numMax++;
				if (rand.nextInt(numMax) == 1) { // pick a max deposit uniformly at random
					deposit = l;
				}
			}
		}
		if (deposit != null) {
			if (!tryMove(myLoc.directionTo(deposit))) {
				rc.mine();
			}
		} else {
			rc.mine();
		}
	}
}
