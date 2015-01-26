package dronespam.robots;

import dronespam.Const;
import battlecode.common.*;

public class Helipad extends OurRobot{
	public Helipad(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		int fate = rand.nextInt(10000);
		
        // get information broadcasted by the HQ
		int numBeavers = mRc.readBroadcast(Const.numBeaverPos);
		int numDrones = mRc.readBroadcast(Const.numDronePos);
		
		if (mRc.isCoreReady() && mRc.getTeamOre() >= 125 && fate < Math.pow(1.2,15-numDrones+numBeavers)*10000) {
			trySpawn(Const.directions[rand.nextInt(8)],RobotType.DRONE);
		}
	}
}
