package dronespam.robots;

import dronespam.Const;
import battlecode.common.*;


public class MinerFactory extends OurRobot {
	public MinerFactory(RobotController rc) {
		super(rc);
	}
	
	public void gameStartCompute() throws GameActionException {
	
	}
	public void doTurn() throws GameActionException {
		int numMiners = mRc.readBroadcast(Const.numMinerPos);
		if (mRc.isCoreReady() && mRc.getTeamOre() >= 60 && numMiners < Const.maxMiners) {
			trySpawn(Const.directions[rand.nextInt(8)],RobotType.MINER);
		}
	}
}
