package team049;

import battlecode.common.Direction;

public class Const {
	
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

	/* Strategy */
	public static final boolean tankStrat = true;
	public static final boolean droneStrat = false;
	/* End Strategy */
	
	/* Array Locs */
	public static final int numBeaverPos = 0;
	public static final int numDronePos = 1;
	public static final int numDepotPos = 2;
	public static final int numHelipadPos = 3;
	public static final int numMinerFactoryPos = 4;
	public static final int numMinerPos = 5;
	public static final int numBarracksPos = 6;
	public static final int numTankFactoryPos = 7;

	public static final int targetLocXPos = 1000;
	public static final int targetLocYPos = 1001;
	public static final int targetMassPos = 1002;
	public static final int attackingPos = 1003;
	public static final int attackTime = 1004;
	/* End Array Locs */
	
	/* Tuning Parameters */
	public static final int maxBeavers = 2; // num beavers to build before switching to miners
	public static final int maxMiners = 20;
	public static final int massDroneMaxThreshold = 15; // num drones needed to initiate attack
	public static final int massDroneHQThreshold = 30; // num drones needed to initiate attack on HQ
	public static final int massDroneMinThreshold = 3; // num drones remaining to initiate retreat\
	public static final int massTankMaxThreshold = 8; // num tanks needed to initiate attack
	public static final int massTankHQThreshold = 15; // num tanks needed to initiate attack on HQ
	public static final int massTankMinThreshold = 3; // num tanks remaining to initiate retreat
	public static final int distThreshold = 36; // distance threshold to be considered 'at' the target
	public static final int droneSupplyThreshold = 2000; // supply level drones will get refilled to
	public static final int tankSupplyThreshold = 4000; // supply level drones will get refilled to
	public static final int otherSupplyThreshold = 1000; // supply level drones will get refilled to
	public static final int minDroneSupplyThreshold = 200; // supply level at which drones return to HQ for a refill
	public static final int minTankSupplyThreshold = 200; // supply level at which drones return to HQ for a refill
	public static final int minBeaverSupplyThreshold = 10;
	public static final int minTimeToRetreat = 75;
	public static final int droneAttackZone = 2;
	public static final int tankAttackZone = 8;
	public static final int towersToLeave = 0;
	/* End Tuning Parameters */
	
	/* Specs Parameters */
	public static final int sightRange = 24;
	/* End Specs Parameters */
	
	public static int directionToInt(Direction d) {
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
