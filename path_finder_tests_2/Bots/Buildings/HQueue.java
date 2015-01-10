package path_finder_tests_2;

import battlecode.common.*;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

import java.lang.*;
import java.lang.Exception;
import java.util.*;

public class HQueue extends Bot {
    public static int max_bvrs = 5;

    public static int getMax_bvrs() {
        return max_bvrs;
    }

    public static void setMax_bvrs(int max_bvrs) {
        HQueue.max_bvrs = max_bvrs;
    }

    public static void loop(RobotController cnt) throws Exception{
        Bot.init(cnt);
        Broadcast.bc_coords(rc, Channels.rally, (theirHQ.x + ourHQ.x) / 2, (theirHQ.y + ourHQ.y) / 2);
        init_coords();

        while (true) {
            try {
                Bot.attackWeakest(RobotType.HQ);
                Bot.spawnUnit(RobotType.BEAVER, max_bvrs);
                Bot.yield_actions(rc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void init_coords()  throws GameActionException{
        /*if (theirHQ.x < ourHQ.x) {

            }
        } */
        int their_lowest_x = 17000;
        int their_lowest_y = 17000;
        int their_highest_x = -17000;
        int their_highest_y = -17000;
        for (Maplocation loc : theirTowers) {
            if (loc.x < their_lowest_x) {
                their_lowest_x = loc.x;
            }
            if (loc.x > their_highest_x) {
                their_highest_x = loc.x;
            }
            if (loc.y < their_lowest_y) {
                their_lowest_y = loc.y;
            }
            if (loc.y > their_highest_y) {
                their_highest_y = loc.y;
            }
        }
        if (theirHQ.x < their_lowest_x) {
            their_lowest_x = theirHQ.x;
        }
        if (theirHQ.x > their_highest_x) {
            their_highest_x = theirHQ.x;
        }
        if (theirHQ.y < their_lowest_y) {
            their_lowest_y = theirHQ.y;
        }
        if (theirHQ.y > their_highest_y) {
            their_highest_y = theirHQ.y;
        }

        int our_lowest_x = 17000;
        int our_lowest_y = 17000;
        int our_highest_x = -17000;
        int our_highest_y = -17000;
        MapLocation low_x_loc = ourHQ;
        MapLocation high_x_loc = ourHQ;
        MapLocation low_y_loc = ourHQ;
        MapLocation high_y_loc = ourHQ;

        for (Maplocation loc : ourTowers) {
            if (loc.x < our_lowest_x) {
                our_lowest_x = loc.x;
                low_x_loc = loc;
            }
            if (loc.x > our_highest_x) {
                our_highest_x = loc.x;
                high_x_loc = loc;
            }
            if (loc.y < our_lowest_y) {
                our_lowest_y = loc.y;
                low_y_loc = loc;
            }
            if (loc.y > our_highest_y) {
                our_highest_y = loc.y;
                high_y_loc = loc;
            }
        }
        if (ourHQ.x < our_lowest_x) {
            our_lowest_x = ourHQ.x;
            low_x_loc = ourHQ;
        }
        if (ourHQ.x > our_highest_x) {
            our_highest_x = ourHQ.x;
            high_x_loc = ourHQ;
        }
        if (ourHQ.y < our_lowest_y) {
            our_lowest_y = ourHQ.y;
            low_y_loc = ourHQ;
        }
        if (ourHQ.y > our_highest_y) {
            our_highest_y = ourHQ.y;
            high_y_loc = ourHQ;
        }
        int lowest_x;
        int highest_x;
        int lowest_y;
        int highest_y;
        int offset = 5;
        boolean x_finalized = true;
        if (their_highest_x < our_highest_x) {
            if (rc.senseTerrainTile(high_x_loc.add(Direction.EAST, 5)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 5;
                x_finalized = false;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 4)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 4;
                offset = 4;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 3)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 3;
                offset = 3;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 2)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 2;
                offset = 2;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 1)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 1;
                offset = 1;
            } else {
                highest_x = high_x_loc.x;
                offset = 0;
            }

            lowest_x = their_lowest_x - offset;

        } else {
            if (rc.senseTerrainTile(low_x_loc.add(Direction.WEST, 5)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 5;
                x_finalized = false;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 4)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 4;
                offset = 4;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 3)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 3;
                offset = 3;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 2)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 2;
                offset = 2;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 1)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 1;
                offset = 1;
            } else {
                lowest_x = low_x_loc.x;
                offset = 0;
            }

            highest_x = their_highest_x + offset;
        }


        if (their_highest_y > our_highest_y) {
            if (rc.senseTerrainTile(high_x_loc.add(Direction.EAST, 5)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 5;
                x_finalized = false;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 4)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 4;
                offset = 4;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 3)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 3;
                offset = 3;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 2)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 2;
                offset = 2;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.EAST, 1)) == TerrainTile.NORMAL) {
                highest_x = high_x_loc.x + 1;
                offset = 1;
            } else {
                highest_x = high_x_loc.x;
                offset = 0;
            }

            lowest_x = their_lowest_x - offset;

        } else {
            if (rc.senseTerrainTile(low_x_loc.add(Direction.WEST, 5)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 5;
                x_finalized = false;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 4)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 4;
                offset = 4;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 3)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 3;
                offset = 3;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 2)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 2;
                offset = 2;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 1)) == TerrainTile.NORMAL) {
                lowest_x = low_x_loc.x - 1;
                offset = 1;
            } else {
                lowest_x = low_x_loc.x;
                offset = 0;
            }

            highest_x = their_highest_x + offset;
        }
    }
}