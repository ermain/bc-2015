package dronespam;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	/* Array Locs */
	static final int targetLocXPos = 1000;
	static final int targetLocYPos = 1001;
	static final int targetMassPos = 1002;
	static final int attackingPos = 1003;
	static final int attackTime = 1004;
	/* End Array Locs */
	
	/* Tuning Parameters */
	static final int massMaxThreshold = 15; // num drones needed to initiate attack
	static final int massHQThreshold = 30; // num drones needed to initiate attack on HQ
	static final int massMinThreshold = 3; // num drones remaining to initiate retreat
	static final int distThreshold = 36; // distance threshold to be considered 'at' the target
	static final int supplyThreshold = 2000; // supply level drones will get refilled to
	static final int minSupplyThreshold = 500; // supply level at which drones return to HQ for a refill
	static final int minTimeToRetreat = 75;
	/* End Tuning Parameters */
	
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
		int attacking = 1;
		int retreatTimer = 0;
		
		if (rc.getType() == RobotType.HQ) {// game start compute goes here 
			try {
				rc.broadcast(attackingPos, 1);
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
					int prevAttacking = attacking;
					attacking = rc.readBroadcast(attackingPos);
					if (prevAttacking == 0 && attacking == 1) {
						//attack recently initiated; set retreat timer
						retreatTimer = minTimeToRetreat;
					} else {
						retreatTimer--;
					}
					
					
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

					determineTarget(enemyHQLoc, myHQLoc, enemyTowers, myTowers, numDrones, numDronesAtTarget, attacking, retreatTimer);

					
					handleSupply(rc);
					
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
						MapLocation target;
						attacking = rc.readBroadcast(attackingPos);
						if (attacking == 0 && rc.getSupplyLevel() < minSupplyThreshold) {
							target = myHQLoc;
						} else {
							int targetX = rc.readBroadcast(targetLocXPos);
							int targetY = rc.readBroadcast(targetLocYPos);
							target = new MapLocation(targetX, targetY);
						}
						
						MapLocation myLoc = rc.getLocation();
						if (attacking == 0 && myLoc.distanceSquaredTo(target) <= 5) {
							int fate = rand.nextInt(5);
							if (fate != 1) { // conserve supply by moving with 1/5 probability
								rc.yield();
								continue;
							}
						}
						
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
	
    // This method will attack an enemy in sight, if there is one, prioritizing HQ > TOWER > other
	static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
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
			rc.attackLocation(target);
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
			int numDrones, int numDronesAtTarget, int attacking, int retreatTimer) throws GameActionException, NullPointerException {
		int minDist = 999999;
		MapLocation [] towers = null;
		MapLocation loc = null;
		MapLocation target = null;
		
		boolean callRetreat = (attacking == 1) && (numDronesAtTarget < massMinThreshold) && (retreatTimer <= 0);
		
		if (callRetreat) { // initiate retreat
			loc = enemyLoc;
			towers = myTowers;
			attacking = 0;
		} else if (enemyTowers.length <= 3) {
			if (numDronesAtTarget > massHQThreshold) { // attack their HQ
				rc.broadcast(targetLocXPos, enemyLoc.x);
				rc.broadcast(targetLocYPos, enemyLoc.y);
				rc.broadcast(attackingPos, 1);
				return true;
			} else {
				return false;
			}
		} else if (attacking == 1 || numDronesAtTarget > massMaxThreshold) { // initiate attack
			loc = myLoc;
			towers = enemyTowers;
			attacking = 1;
		} else { // status quo
			return false;
		}
			
		for (MapLocation l : towers) {
			int dist = l.distanceSquaredTo(loc);
			if (dist < minDist) {
				minDist = dist;
				target = l;
			}
		}
		rc.broadcast(targetLocXPos, target.x);
		rc.broadcast(targetLocYPos, target.y);
		rc.broadcast(attackingPos, attacking);
		return true;
	}
	
	static void handleSupply(RobotController rc)  throws GameActionException {
		RobotInfo[] myRobots = rc.senseNearbyRobots(15); // 15 is the supply transfer distance
		for (RobotInfo r : myRobots) {
			double supply = r.supplyLevel;
			if (r.type == RobotType.DRONE && supply < supplyThreshold && rc.getSupplyLevel() >= supplyThreshold - supply) {
				rc.transferSupplies((int)(supplyThreshold - supply), r.location);
			}
		}
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
