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
		int fate = rand.nextInt(3);
		MapLocation myLoc = mRc.getLocation();
		if (mRc.isWeaponReady()) {
			attackSomething();
		}
		if (mRc.isCoreReady()) {
			Direction dirToHQ = myLoc.directionTo(myHQLoc);
			int distToHQ = myLoc.distanceSquaredTo(myHQLoc);
			if (mRc.getTeamOre() >= 500 && distToHQ <= 36 && mRc.readBroadcast(Const.numFactoryPos) == 0){
				// Build one miner factory ASAP
				tryBuild(dirToHQ,RobotType.MINERFACTORY);	
			}
			else if (mRc.getTeamOre() >= 300 && distToHQ <= 36 && mRc.readBroadcast(Const.numFactoryPos) > 0) {
				// Build Helipad whenever we have spare ore and have already built a miner factory
				tryBuild(dirToHQ,RobotType.HELIPAD);	
			} else if (mRc.getTeamOre() >= 100 && mRc.readBroadcast(Const.numDepotPos) == 0 && mRc.readBroadcast(Const.numFactoryPos) > 0) {
				// Build one supply depot after we have at least one miner factory
				tryBuild(Const.directions[rand.nextInt(8)], RobotType.SUPPLYDEPOT);
			} else if (fate == 1) {
				mRc.mine();
			} else if (fate == 2) {
				tryMove(Const.directions[rand.nextInt(8)]);
			} else if (myLoc.distanceSquaredTo(myHQLoc) <= 10) {
				tryMoveAway(dirToHQ);
			} else {
				tryMove(dirToHQ);
			}
			
		}
	}
}
