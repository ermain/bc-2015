package dronespam;

import battlecode.common.Direction;

public class Const {
	
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

	/* Array Locs */
	public static final int targetLocXPos = 1000;
	public static final int targetLocYPos = 1001;
	public static final int targetMassPos = 1002;
	public static final int attackingPos = 1003;
	public static final int attackTime = 1004;
	/* End Array Locs */
	
	/* Tuning Parameters */
	public static final int massMaxThreshold = 15; // num drones needed to initiate attack
	public static final int massHQThreshold = 30; // num drones needed to initiate attack on HQ
	public static final int massMinThreshold = 3; // num drones remaining to initiate retreat
	public static final int distThreshold = 36; // distance threshold to be considered 'at' the target
	public static final int supplyThreshold = 2000; // supply level drones will get refilled to
	public static final int minSupplyThreshold = 500; // supply level at which drones return to HQ for a refill
	public static final int minTimeToRetreat = 75;
	/* End Tuning Parameters */
	
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
