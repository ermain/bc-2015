package team049.robots;

import team049.Const;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TankFactory extends OurRobot {
	public TankFactory(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		if (mRc.isCoreReady() && mRc.getTeamOre() >= 250) {
			trySpawn(Const.directions[rand.nextInt(8)],RobotType.TANK);
		}
	}
}
