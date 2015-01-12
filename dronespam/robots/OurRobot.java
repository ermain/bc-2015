package dronespam.robots;
import java.util.Random;

import dronespam.Const;
import battlecode.common.*;

public abstract class OurRobot {
	public static RobotController mRc;
	public Team myTeam;
	public static Team enemyTeam;
	public static int myRange;
	public Random rand;
	public static RobotInfo[] myRobots;
	public MapLocation[] enemyTowers;
	public MapLocation[] myTowers;
	public MapLocation enemyHQLoc;
	public MapLocation myHQLoc;
	
	// Put generic functions that all robots will have to use in this file.
	
	protected OurRobot(RobotController rc) {
		mRc = rc;
		rand = new Random(rc.getID());
		myRange = rc.getType().attackRadiusSquared;
		enemyHQLoc = rc.senseEnemyHQLocation();
		myHQLoc = rc.senseHQLocation();
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
	}
	public void gameStartCompute() throws GameActionException {
	}
	public void doTurn() throws GameActionException {
	}
	// This method will attempt to move in Direction d (or as close to it as possible)
	static boolean tryMove(Direction d) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = Const.directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 5 && !mRc.canMove(Const.directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5) {
			mRc.move(Const.directions[(dirint+offsets[offsetIndex]+8)%8]);
			return true;
		}
		return false;
	}
	
	static void tryBuild(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = Const.directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !mRc.canMove(Const.directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			mRc.build(Const.directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	// This method will attempt to spawn in the given direction (or as close to it as possible)
	static void trySpawn(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = Const.directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !mRc.canSpawn(Const.directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			mRc.spawn(Const.directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	
	public static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = mRc.senseNearbyRobots(myRange, enemyTeam);
		MapLocation target = null;
		for (RobotInfo r : enemies) {
			if (target == null) {
				target = r.location;
			} else if (r.type == RobotType.HQ) {
				target = r.location;
				break;
			} else if (r.type == RobotType.TOWER) {
				target = r.location;
			}
		}
		if (target != null) {
			mRc.attackLocation(target);
		}
	}
}
