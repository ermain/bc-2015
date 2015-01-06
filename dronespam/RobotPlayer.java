package dronespam;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static final int targetLocXPos = 1000;
	static final int targetLocYPos = 1001;
	static final int targetMassPos = 1002;
	static final int attackingPos = 1003;
	static final int attackTime = 1004;
	static final int massMaxThreshold = 15;
	static final int massMinThreshold = 3;
	static final int distThreshold = 36;
	static RobotController rc;
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	static Random rand;
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	public static void run(RobotController tomatojuice) {
		rc = tomatojuice;
        rand = new Random(rc.getID());

		myRange = rc.getType().attackRadiusSquared;
		MapLocation enemyHQLoc = rc.senseEnemyHQLocation();
		MapLocation myHQLoc = rc.senseHQLocation();
        Direction lastDirection = null;
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		RobotInfo[] myRobots;
		MapLocation[] enemyTowers;
		MapLocation[] myTowers;
		boolean swarmReachedTarget = false;
		
		if (rc.getType() == RobotType.HQ) {// game start compute goes here 
			try {
				rc.broadcast(attackingPos, 1);
				swarmReachedTarget = true;
			} catch (Exception e) {
                System.out.println("Unexpected exception");
                e.printStackTrace();
			}
		}
		while(true) {
            try {
                rc.setIndicatorString(0, "This is an indicator string.");
                rc.setIndicatorString(1, "I am a " + rc.getType());
            } catch (Exception e) {
                System.out.println("Unexpected exception");
                e.printStackTrace();
            }

			if (rc.getType() == RobotType.HQ) {
				try {
					int fate = rand.nextInt(10000);
					myRobots = rc.senseNearbyRobots(999999, myTeam);
					enemyTowers = rc.senseEnemyTowerLocations();
					myTowers = rc.senseTowerLocations();
					
					MapLocation curTarget = new MapLocation(rc.readBroadcast(targetLocXPos),rc.readBroadcast(targetLocYPos));
					int attacking = rc.readBroadcast(attackingPos);
					
					int numDrones = 0;
					int numDronesAtTarget = 0;
					int numBeavers = 0;
					int numHelipads = 0;
					for (RobotInfo r : myRobots) {
						RobotType type = r.type;
						if (type == RobotType.DRONE) {
							numDrones++;
							if (r.location.distanceSquaredTo(curTarget) < distThreshold) {
								numDronesAtTarget++;					
							}
						} else if (type == RobotType.BEAVER) {
							numBeavers++;
						} else if (type == RobotType.HELIPAD) {
							numHelipads++;
						}
					}
					rc.broadcast(0, numBeavers);
					rc.broadcast(1, numDrones);
					rc.broadcast(100, numHelipads);
					
					if (numDronesAtTarget > massMinThreshold) {
						swarmReachedTarget = true;
					}
					if (determineTarget(enemyHQLoc, myHQLoc, enemyTowers, myTowers, numDrones, numDronesAtTarget, attacking, swarmReachedTarget)) {
						swarmReachedTarget = false;
					}
					
					if (rc.isWeaponReady()) {
						attackSomething();
					}

					if (rc.isCoreReady() && rc.getTeamOre() >= 100 && fate < Math.pow(1.2,12-numBeavers)*10000) {
						trySpawn(directions[rand.nextInt(8)], RobotType.BEAVER);
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}
			
            if (rc.getType() == RobotType.TOWER) {
                try {					
					if (rc.isWeaponReady()) {
						attackSomething();
					}
				} catch (Exception e) {
					System.out.println("Tower Exception");
                    e.printStackTrace();
				}
            }
			
            if (rc.getType() == RobotType.DRONE) {
                try {
                    if (rc.isWeaponReady()) {
						attackSomething();
					}
					if (rc.isCoreReady()) {
						int targetX = rc.readBroadcast(targetLocXPos);
						int targetY = rc.readBroadcast(targetLocYPos);
						MapLocation target = new MapLocation(targetX, targetY);
						MapLocation myLoc = rc.getLocation();
						Direction d = myLoc.directionTo(target);
						tryMove(d);
					}
                } catch (Exception e) {
					System.out.println("Drone Exception");
					e.printStackTrace();
                }
            }
			
			if (rc.getType() == RobotType.BEAVER) {
				try {
					if (rc.isWeaponReady()) {
						attackSomething();
					}
					if (rc.isCoreReady()) {
						int fate = rand.nextInt(1000);
						if (fate < 8 && rc.getTeamOre() >= 300) {
							tryBuild(directions[rand.nextInt(8)],RobotType.HELIPAD);
						} else if (fate < 600) {
							rc.mine();
						} else if (fate < 900) {
							tryMove(directions[rand.nextInt(8)]);
						} else {
							tryMove(rc.senseHQLocation().directionTo(rc.getLocation()));
						}
					}
				} catch (Exception e) {
					System.out.println("Beaver Exception");
                    e.printStackTrace();
				}
			}

            if (rc.getType() == RobotType.HELIPAD) {
				try {
					int fate = rand.nextInt(10000);
					
                    // get information broadcasted by the HQ
					int numBeavers = rc.readBroadcast(0);
					int numDrones = rc.readBroadcast(1);
					
					if (rc.isCoreReady() && rc.getTeamOre() >= 125 && fate < Math.pow(1.2,15-numDrones+numBeavers)*10000) {
						trySpawn(directions[rand.nextInt(8)],RobotType.DRONE);
					}
				} catch (Exception e) {
					System.out.println("Helipad Exception");
                    e.printStackTrace();
				}
			}
			
			rc.yield();
		}
	}
	
    // This method will attack an enemy in sight, if there is one
	static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if (enemies.length > 0) {
			rc.attackLocation(enemies[0].location);
		}
	}
	
    // This method will attempt to move in Direction d (or as close to it as possible)
	static void tryMove(Direction d) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5) {
			rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
		}
	}
	
    // This method will attempt to spawn in the given direction (or as close to it as possible)
	static void trySpawn(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	
    // This method will attempt to build in the given direction (or as close to it as possible)
	static void tryBuild(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	
	static boolean determineTarget(MapLocation enemyLoc, MapLocation myLoc, MapLocation [] enemyTowers, MapLocation [] myTowers, 
			int numDrones, int numDronesAtTarget, int attacking, boolean swarmReachedTarget) throws GameActionException, NullPointerException {
		int minDist = 999999;
		MapLocation closestTower = null;
		MapLocation [] towers = null;
		MapLocation loc = null;
		
		if (numDronesAtTarget > massMaxThreshold) { // initiate attack
			loc = myLoc;
			towers = enemyTowers;
			attacking = 1;
		} else if (attacking == 1 && numDronesAtTarget < massMinThreshold && swarmReachedTarget) { // initiate retreat
			loc = enemyLoc;
			towers = myTowers;
			attacking = 0;
		} else { // status quo
			return false;
		}
			
		for (MapLocation l : towers) {
			int dist = l.distanceSquaredTo(myLoc);
			if (dist < minDist) {
				minDist = dist;
				closestTower = l;
			}
		}
		rc.broadcast(targetLocXPos, closestTower.x);
		rc.broadcast(targetLocYPos, closestTower.y);
		rc.broadcast(attackingPos, attacking);
		return true;
	}
	
	static int directionToInt(Direction d) {
		switch(d) {
			case NORTH:
				return 0;
			case NORTH_EAST:
				return 1;
			case EAST:
				return 2;
			case SOUTH_EAST:
				return 3;
			case SOUTH:
				return 4;
			case SOUTH_WEST:
				return 5;
			case WEST:
				return 6;
			case NORTH_WEST:
				return 7;
			default:
				return -1;
		}
	}
}
