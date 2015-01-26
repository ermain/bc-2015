package team049.robots;

import battlecode.common.*;

public class Tower extends OurRobot{

	public Tower(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		try {					
			if (mRc.isWeaponReady()) {
				attackSomething();
			}
		} catch (Exception e) {
			System.out.println("Tower Exception");
            e.printStackTrace();
		}
	}
}
