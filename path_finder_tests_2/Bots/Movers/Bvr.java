package path_finder_tests_2;


import battlecode.common.*;

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
                /**
                 try_build(RobotType.BARRACKS, ore, here);
                 try_build(RobotType.SUPPLYDEPOT, ore, here);
                 try_build_dep(RobotType.TANKFACTORY, ore, here);
                 try_build(RobotType.TECHNOLOGYINSTITUTE, ore, here);
                 try_build_dep(RobotType.TRAININGFIELD, ore, here);
                 try_build(RobotType.HELIPAD, ore, here);
                 try_build_dep(RobotType.AEROSPACELAB, ore, here);
                 **/
                try_build(RobotType.MINERFACTORY, ore, here);
                Bot.mine_well(RobotType.BEAVER, 2, 20);
                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void do_build(RobotType type, double ore, MapLocation here, int num_buildings) throws GameActionException {
        if (rc.isCoreReady()) {
            if (ore > type.oreCost && !here.isAdjacentTo(ourHQ)) {
                Direction towards = Bot.getRandomDirection();
                MapLocation loc = here.add(towards);
                if (rc.canBuild(towards, type) && !loc.isAdjacentTo(ourHQ)) {
                    Broadcast.incChannel(rc, Channels.getChannel(type));
                    rc.build(towards, type);
                    Bot.yield_actions(rc);
                }
            }
        }
    }

    private static void try_build(RobotType type, double ore, MapLocation here) throws GameActionException {
        int num_buildings = Broadcast.read(rc, Channels.getChannel(type));
        if (num_buildings < get_max_buildings(type)) {
            do_build(type, ore, here, num_buildings);
        }
    }

    private static void try_build_dep(RobotType type, double ore, MapLocation here) throws GameActionException {
        if (rc.checkDependencyProgress(type) == DependencyProgress.DONE) {
            try_build(type, ore, here);
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

    public static void set_max_buildings(RobotType type, int num) {
        switch (type) {
            case MINERFACTORY:
                max_minrfct = num;
                break;
            case AEROSPACELAB:
                max_aerspclab = num;
                break;
            case BARRACKS:
                max_barcks = num;
                break;
            case HELIPAD:
                max_hlpd = num;
                break;
            case TANKFACTORY:
                max_tnkfct = num;
                break;
            case TECHNOLOGYINSTITUTE:
                max_techinst = num;
                break;
            case TRAININGFIELD:
                max_trngfld = num;
                break;
            case SUPPLYDEPOT:
                max_supdep = num;
                break;
            default:
                break;
        }
    }
}