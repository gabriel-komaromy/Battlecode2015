package team190;

import battlecode.common.*;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

import java.lang.*;
import java.lang.Exception;
import java.util.*;
import java.lang.Math.*;

public class HQueue extends Bot {
    private static int max_bvrs = 1;

    public static void loop(RobotController cnt) throws Exception{
        try {
            Bot.init(cnt);
            if (Bot.spawnUnit(RobotType.BEAVER, max_bvrs, rc.getLocation().directionTo(theirHQ).opposite())) {
                ;
            } else {
                Bot.spawnUnit(RobotType.BEAVER, max_bvrs, Bot.getRandomDirection());
            }

            Broadcast.bc_coords(rc, 53, (theirHQ.x + ourHQ.x * 3) / 4, (theirHQ.y + ourHQ.y * 3) / 4);
            Broadcast.bc_coords(rc, 56, theirHQ.x, theirHQ.y);
            Broadcast.bc(rc, 55, 1);
            Broadcast.bc(rc, Channels.getChannelMax(RobotType.MINER), 3);
            //Broadcast.bc(rc, Channels.getChannelMax(RobotType.SOLDIER), 0);
            //Broadcast.bc(rc, Channels.getChannelMax(RobotType.BASHER), 0);
            Broadcast.bc(rc, Channels.getChannelMax(RobotType.TANK), 50);
            //Broadcast.bc(rc, Channels.getChannelMax(RobotType.LAUNCHER), 50);
            //Broadcast.bc(rc, Channels.getChannelMax(RobotType.DRONE), 5);
            //Broadcast.bc(rc, Channels.getChannelMax(RobotType.HELIPAD), 1);
            Broadcast.bc(rc, Channels.getChannelMax(RobotType.MINERFACTORY), 1);
            init_coords();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                int rd = Clock.getRoundNum();
                if (rd < 120 && rd > 100 && Broadcast.read(rc, Channels.getChannelMax(RobotType.BARRACKS)) == 0) {
                    Broadcast.bc(rc, Channels.getChannelMax(RobotType.TANKFACTORY), 5);
                    Broadcast.bc(rc, Channels.getChannelMax(RobotType.BARRACKS), 1);
                    max_bvrs = 2;
                    Broadcast.bc(rc, Channels.getChannelMax(RobotType.MINER), 5);
                    //Broadcast.bc(rc, Channels.getChannelMax(RobotType.HELIPAD), 10);
                    //Broadcast.bc(rc, Channels.getChannelMax(RobotType.DRONE), 500);

                }

                if (rd < 100 && rd > 50 && Broadcast.read(rc, Channels.getChannelMax(RobotType.MINERFACTORY)) == 0) {
                    //Broadcast.bc(rc, Channels.getChannelMax(RobotType.MINERFACTORY), 1);
                    //Broadcast.bc(rc, Channels.getChannelMax(RobotType.AEROSPACELAB), 5);
                }

                if (rd < 220 && rd > 200 && (Broadcast.read(rc, Channels.getChannelMax(RobotType.SUPPLYDEPOT)) == 0)) {
                    Broadcast.bc(rc, Channels.getChannelMax(RobotType.SUPPLYDEPOT), 1);
                    Broadcast.bc(rc, Channels.getChannelMax(RobotType.MINER), 30);
                }

                if (rd < 700 && rd > 650 && (Broadcast.read(rc, Channels.getChannelMax(RobotType.SUPPLYDEPOT)) == 1)) {
                    Broadcast.bc(rc, Channels.getChannelMax(RobotType.SUPPLYDEPOT), 5);
                }
                /*if (rd > 1500 && rd < 1600) {
                    Broadcast.bc_coords(rc, 53, theirHQ.x, theirHQ.y);
                    Broadcast.bc(rc, 55, 1);
                }*/
                Bot.attackWeakest(RobotType.HQ);
                Bot.spawnUnit(RobotType.BEAVER, max_bvrs);
                Bot.yield_actions(rc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void init_coords()  throws GameActionException{
        int their_lowest_x = 17000;
        int their_lowest_y = 17000;
        int their_highest_x = -17000;
        int their_highest_y = -17000;
        for (MapLocation loc : theirTowers) {
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

        for (MapLocation loc : ourTowers) {
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
        boolean y_finalized = true;
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
            if (rc.senseTerrainTile(low_y_loc.add(Direction.NORTH, 5)) == TerrainTile.NORMAL) {
                lowest_y = low_y_loc.y - 5;
                y_finalized = false;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.NORTH, 4)) == TerrainTile.NORMAL) {
                lowest_y = low_y_loc.y - 4;
                offset = 4;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.NORTH, 3)) == TerrainTile.NORMAL) {
                lowest_y = low_y_loc.y - 3;
                offset = 3;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.NORTH, 2)) == TerrainTile.NORMAL) {
                lowest_y = low_y_loc.y - 2;
                offset = 2;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.NORTH, 1)) == TerrainTile.NORMAL) {
                lowest_y = low_y_loc.y - 1;
                offset = 1;
            } else {
                lowest_y = low_y_loc.y;
                offset = 0;
            }

            highest_y = their_highest_y + offset;

        } else {
            if (rc.senseTerrainTile(high_y_loc.add(Direction.SOUTH, 5)) == TerrainTile.NORMAL) {
                highest_y = high_y_loc.y + 5;
                x_finalized = false;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 4)) == TerrainTile.NORMAL) {
                highest_y = high_y_loc.y + 4;
                offset = 4;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 3)) == TerrainTile.NORMAL) {
                highest_y = high_y_loc.y + 3;
                offset = 3;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 2)) == TerrainTile.NORMAL) {
                highest_y = high_y_loc.y + 2;
                offset = 2;
            } else if (rc.senseTerrainTile(ourHQ.add(Direction.WEST, 1)) == TerrainTile.NORMAL) {
                highest_y = high_y_loc.y + 1;
                offset = 1;
            } else {
                highest_y = high_y_loc.y;
                offset = 0;
            }

            lowest_y = their_lowest_y - offset;
        }

        if (x_finalized) {
            Broadcast.bc(rc, 44, lowest_x);
            Broadcast.bc(rc, 45, highest_x);
            Broadcast.bc(rc, 50, highest_x - lowest_x);
        } else {
            Broadcast.bc(rc, 40, lowest_x);
            Broadcast.bc(rc, 41, highest_x);
            Broadcast.bc(rc, 48, highest_x - lowest_x);
        }

        if (y_finalized) {
            Broadcast.bc(rc, 46, lowest_y);
            Broadcast.bc(rc, 47, highest_y);
            Broadcast.bc(rc, 51, highest_y - lowest_y);
        } else {
            Broadcast.bc(rc, 42, lowest_y);
            Broadcast.bc(rc, 43, highest_y);
            Broadcast.bc(rc, 49, highest_y - lowest_y);
        }

        if (x_finalized && y_finalized) {
            Broadcast.bc(rc, 52, 1);
        }
    }
}
