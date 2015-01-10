package path_finder_tests_2;


import battlecode.common.*;

import java.lang.*;
import java.util.*;
import java.lang.Math.*;

public class Bvr extends Bot {
    private static int max_minrfct = 2;
    private static int max_barcks = 1;
    private static int max_aerspclab = 1;
    private static int max_hlpd = 1;
    private static int max_hndwshstn = 0;
    private static int max_supdep = 5;
    private static int max_tnkfct = 1;
    private static int max_techinst = 1;
    private static int max_trngfld = 1;

    public static void loop(RobotController cnt) throws Exception {
        Bot.init(cnt);
        Nav.init(cnt);

        while (true) {
            MapLocation here = rc.getLocation();
            double ore = rc.getTeamOre();
            try {
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