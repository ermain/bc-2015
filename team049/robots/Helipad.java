package team049.robots;

import team049.Const;
import battlecode.common.*;

public class Helipad extends OurRobot{
	public Helipad(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		int fate = rand.nextInt(10000);
		
		if (mRc.isCoreReady() && mRc.getTeamOre() >= 125) {
			trySpawn(Const.directions[rand.nextInt(8)],RobotType.DRONE);
		}
	}
}
