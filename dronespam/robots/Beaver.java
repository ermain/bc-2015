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
		if (mRc.isWeaponReady()) {
			attackSomething();
		}
		if (mRc.isCoreReady()) {
			int fate = rand.nextInt(1000);
			if (fate < 8 && mRc.getTeamOre() >= 300) {
				tryBuild(Const.directions[rand.nextInt(8)],RobotType.HELIPAD);
			} else if (fate < 600) {
				mRc.mine();
			} else if (fate < 900) {
				tryMove(Const.directions[rand.nextInt(8)]);
			} else {
				tryMove(mRc.senseHQLocation().directionTo(mRc.getLocation()));
			}
		}
	}
}
