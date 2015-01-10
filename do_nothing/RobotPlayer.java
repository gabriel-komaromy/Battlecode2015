package do_nothing;

import battlecode.common.*;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import java.lang.Exception;
import java.util.*;

public class RobotPlayer {
    static RobotController rc;
    static Random rand;
    static Direction facing;
    static Team us;
    static Team them;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public static void run(RobotController cnt) {
        rc = cnt;
        rand = new Random(rc.getID());
        facing = getRandomDirection();
        us = rc.getTeam();
        them = us.opponent();
        while (true) {
            try {
                switch (rc.getType()) {
                    case HQ:
                        break;
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            rc.yield();
        }
    }

    private static void spawnUnit(RobotType type) throws Exception {
        Direction randomDir = getRandomDirection();
        if (rc.isCoreReady() && rc.canSpawn(randomDir, type)) {
            rc.spawn(randomDir, type);
        }
    }

    private static Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

}