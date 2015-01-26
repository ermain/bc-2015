package team049.robots;
import team049.Const;
import battlecode.common.*;

public class Miner extends OurRobot {
	public Miner(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	
	final int minOre = 10; // Minimum ore on a spot for miners to mine at max efficiency
	
	public void doTurn() throws GameActionException {
		MapLocation myLoc = mRc.getLocation();
		if (mRc.isWeaponReady()) {
			attackSomething();
		}
		if (mRc.isCoreReady()) {
			if (mRc.senseOre(myLoc) >= 10) {
				mRc.mine();
			} else {
				moveTowardsOre(mRc);
			}
		}
	}
}
