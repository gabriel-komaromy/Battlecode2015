package team190;


import battlecode.common.*;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

import java.lang.*;
import java.util.*;
import java.lang.Math.*;

public class Bvr extends Bot {
    private static int max_minrfct;
    private static int max_barcks;
    private static int max_aerspclab;
    private static int max_hlpd;
    private static int max_hndwshstn;
    private static int max_supdep;
    private static int max_tnkfct;
    private static int max_techinst;
    private static int max_trngfld;


    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            Nav.init(cnt);
            max_minrfct = Broadcast.read(rc, Channels.getChannelMax(RobotType.MINERFACTORY));
            max_barcks = Broadcast.read(rc, Channels.getChannelMax(RobotType.BARRACKS));
            max_aerspclab = Broadcast.read(rc, Channels.getChannelMax(RobotType.AEROSPACELAB));
            max_hlpd = Broadcast.read(rc, Channels.getChannelMax(RobotType.HELIPAD));
            max_hndwshstn = Broadcast.read(rc, Channels.getChannelMax(RobotType.HANDWASHSTATION));
            max_supdep = Broadcast.read(rc, Channels.getChannelMax(RobotType.SUPPLYDEPOT));
            max_tnkfct = Broadcast.read(rc, Channels.getChannelMax(RobotType.TANKFACTORY));
            max_techinst = Broadcast.read(rc, Channels.getChannelMax(RobotType.TECHNOLOGYINSTITUTE));
            max_trngfld = Broadcast.read(rc, Channels.getChannelMax(RobotType.TRAININGFIELD));

        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (!Bot.bfsInitialized) {
                    if (Broadcast.read(rc, 52) == 1) {
                        Bfs.init(rc);
                        Bot.setBfsInitialized(true);
                    }
                }
                int rd = Clock.getRoundNum() % 20;
                switch (rd) {
                    case 0:
                        max_minrfct = Broadcast.read(rc, Channels.getChannelMax(RobotType.MINERFACTORY));
                        break;
                    case 1:
                        max_barcks = Broadcast.read(rc, Channels.getChannelMax(RobotType.BARRACKS));
                        break;
                    case 2:
                        max_aerspclab = Broadcast.read(rc, Channels.getChannelMax(RobotType.AEROSPACELAB));
                        break;
                    case 3:
                        max_hlpd = Broadcast.read(rc, Channels.getChannelMax(RobotType.HELIPAD));
                        break;
                    case 4:
                        max_hndwshstn = Broadcast.read(rc, Channels.getChannelMax(RobotType.HANDWASHSTATION));
                        break;
                    case 5:
                        max_supdep = Broadcast.read(rc, Channels.getChannelMax(RobotType.SUPPLYDEPOT));
                        break;
                    case 6:
                        max_tnkfct = Broadcast.read(rc, Channels.getChannelMax(RobotType.TANKFACTORY));
                        break;
                    case 7:
                        max_techinst = Broadcast.read(rc, Channels.getChannelMax(RobotType.TECHNOLOGYINSTITUTE));
                        break;
                    case 8:
                        max_trngfld = Broadcast.read(rc, Channels.getChannelMax(RobotType.TRAININGFIELD));
                        break;
                    default:
                        break;
                }
                MapLocation here = rc.getLocation();
                double ore = rc.getTeamOre();
                try_build(RobotType.MINERFACTORY, ore);
                try_build(RobotType.BARRACKS, ore);
                try_build(RobotType.SUPPLYDEPOT, ore);
                try_build_dep(RobotType.TANKFACTORY, ore, RobotType.BARRACKS);
                //try_build(RobotType.TECHNOLOGYINSTITUTE, ore);
                //try_build_dep(RobotType.TRAININGFIELD, ore);
                //try_build(RobotType.HELIPAD, ore);
                //try_build_dep(RobotType.AEROSPACELAB, ore, RobotType.HELIPAD);

                /*if (Clock.getRoundNum() < 300) {
                    Bot.mine_well(RobotType.BEAVER, 2, 20);
                }*/
                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void do_build(RobotType type, double ore, int num_buildings) throws GameActionException {
        if (rc.isCoreReady()) {
            if (ore >= type.oreCost) {
                /*
                MapLocation lastBuilding = Broadcast.readLocation(rc, 59);
                if (rc.senseTerrainTile(lastBuilding.add(Direction.SOUTH)) == TerrainTile.NORMAL) {
                    Nav.goTo(lastBuilding.add(Direction.SOUTH), Nav.Engage.NO, new int[0]);
                    if (rc.canBuild(Direction.EAST, type) && rc.isCoreReady()) {
                        rc.build(Direction.EAST, type);
                        Broadcast.incChannel(rc, Channels.getChannel(type));
                        Broadcast.bc_location(rc, 59, lastBuilding.add(Direction.SOUTH_EAST));
                        Bot.yield_actions(rc);
                    } else if (rc.canBuild(Direction.WEST, type) && rc.isCoreReady()) {
                        rc.build(Direction.WEST, type);
                        Broadcast.incChannel(rc, Channels.getChannel(type));
                        Broadcast.bc_location(rc, 59, lastBuilding.add(Direction.SOUTH_WEST));
                        Bot.yield_actions(rc);
                    }
                } else {
                    if (rc.senseTerrainTile(lastBuilding.add(Direction.NORTH)) == TerrainTile.NORMAL) {
                        Nav.goTo(lastBuilding.add(Direction.NORTH), Nav.Engage.NO, new int[0]);
                    }
                    if (rc.canBuild(Direction.EAST, type) && rc.isCoreReady()) {
                        rc.build(Direction.EAST, type);
                        Broadcast.incChannel(rc, Channels.getChannel(type));
                        Broadcast.bc_location(rc, 59, lastBuilding.add(Direction.NORTH_EAST));
                        Bot.yield_actions(rc);
                    } else if (rc.canBuild(Direction.WEST, type) && rc.isCoreReady()) {
                        rc.build(Direction.WEST, type);
                        Broadcast.incChannel(rc, Channels.getChannel(type));
                        Broadcast.bc_location(rc, 59, lastBuilding.add(Direction.NORTH_WEST));
                        Bot.yield_actions(rc);
                    }
                }*/

                Direction towards = rc.getLocation().directionTo(Bot.theirHQ).opposite();
                if (rc.canBuild(towards, type)) {
                    Broadcast.incChannel(rc, Channels.getChannel(type));
                    rc.build(towards, type);
                } else if (rc.canBuild(towards.rotateRight(), type)) {
                    Broadcast.incChannel(rc, Channels.getChannel(type));
                    rc.build(towards.rotateRight(), type);
                } else if (rc.canBuild(towards.rotateLeft(), type)) {
                    Broadcast.incChannel(rc, Channels.getChannel(type));
                    rc.build(towards.rotateLeft(), type);
                } else {
                    Direction next = Bot.getRandomDirection();
                    if (rc.canMove(next)) {
                        rc.move(next);
                        do_build(type, ore, num_buildings);
                    }
                }
            }
        }
    }

    private static void try_build(RobotType type, double ore) throws GameActionException {
        int num_buildings = Broadcast.read(rc, Channels.getChannel(type));
        if (num_buildings < get_max_buildings(type)) {
            do_build(type, ore, num_buildings);
        }
    }

    private static void try_build_dep(RobotType type, double ore, RobotType dependency) throws GameActionException {
        if (rc.checkDependencyProgress(dependency) == DependencyProgress.DONE) {
            try_build(type, ore);
        }

    }

    public static int get_max_buildings(RobotType type) {
        switch (type) {
            case MINERFACTORY:
                return max_minrfct;
            case AEROSPACELAB:
                return max_aerspclab;
            case BARRACKS:
                return max_barcks;
            case HELIPAD:
                return max_hlpd;
            case TANKFACTORY:
                return max_tnkfct;
            case TECHNOLOGYINSTITUTE:
                return max_techinst;
            case TRAININGFIELD:
                return max_trngfld;
            default:
                return max_supdep;
        }
    }
}