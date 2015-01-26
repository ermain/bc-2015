package team049;

import battlecode.common.*;
import java.util.*;

import team049.robots.*;

public class RobotPlayer {
	

	public static RobotController rc;

	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	static Random rand;
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
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
		else if (rc.getType() == RobotType.MINERFACTORY) {
			robot = new MinerFactory(rc);
		}
		else if (rc.getType() == RobotType.MINER) {
			robot = new Miner(rc);
		}
		else if (rc.getType() == RobotType.TANKFACTORY) {
			robot = new TankFactory(rc);
		}
		else if (rc.getType() == RobotType.TANK) {
			robot = new Tank(rc);
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
	            rc.setIndicatorString(0, "This is an indicator string.");
	            rc.setIndicatorString(1, "I am a " + rc.getType());
	            
				robot.doTurn();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			rc.yield();
		}

	}

}
