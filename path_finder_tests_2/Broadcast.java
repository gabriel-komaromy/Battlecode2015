package path_finder_tests_2;

import battlecode.common.*;

public class Broadcast {
    public static void bc(RobotController rc, int channel, int message) throws GameActionException {
        rc.broadcast(channel, message);
    }

    public static void incChannel(RobotController rc, int channel, int inc) throws GameActionException {
        bc(rc, channel, read(rc, channel) + inc);
    }

    public static void incChannel(RobotController rc, int channel) throws GameActionException {
        incChannel(rc, channel, 1);
    }

    public static int read(RobotController rc, int channel) throws GameActionException {
        return rc.readBroadcast(channel);
    }

    public static void bc_location(RobotController rc, int channel, MapLocation location) throws GameActionException {
        bc_coords(rc, channel, location.x, location.y);
    }

    public static void bc_coords(RobotController rc, int channel, int x, int y) throws GameActionException {
        bc(rc, channel, x);
        bc(rc, channel + 1, y);
    }

    public static MapLocation readLocation(RobotController rc, int channel) throws GameActionException {
        return new MapLocation(read(rc, channel), read(rc, channel + 1));
    }
}