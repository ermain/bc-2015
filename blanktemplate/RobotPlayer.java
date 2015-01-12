package blanktemplate;

import battlecode.common.*;
import blanktemplate.robots.*;

public class RobotPlayer {
	public static RobotController rc;
	public static void run(RobotController duckcontroller) throws GameActionException {
		RobotPlayer.rc = duckcontroller;
		OurRobot robot;
		if (rc.getType() == RobotType.HQ) {
			robot = new HQ(rc);
		}
		else if (rc.getType() == RobotType.BEAVER) {
			robot = new Beaver(rc);
		}
		else if (rc.getType() == RobotType.DRONE) {
			robot = new Drone(rc);
		}
		else if (rc.getType() == RobotType.HELIPAD) {
			robot = new Helipad(rc);
		}
		else if (rc.getType() == RobotType.TOWER) {
			robot = new Tower(rc);
		}
		else {
			robot = new Unimplemented(rc);
		}
		try {
			robot.gameStartCompute();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		rc.yield();
		while (true) {
			try {
				robot.doTurn();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			rc.yield();
		}

	}
}