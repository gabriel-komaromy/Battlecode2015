package path_finder_tests_2;

import battlecode.common.*;
import battlecode.common.GameActionException;

import java.lang.*;
import java.util.*;

public class MinrFct extends Bot {
    private static int max_minrs = 10;

    public static int getMax_minrs() {
        return max_minrs;
    }

    public static void setMax_minrs(int max_minrs) {
        MinrFct.max_minrs = max_minrs;
    }

    public static void loop(RobotController cnt) throws GameActionException {
        Bot.init(cnt);

        while (true) {
            try {
                Bot.spawnUnit(RobotType.MINER, max_minrs);
                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}