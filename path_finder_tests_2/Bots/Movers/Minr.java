package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Minr extends Bot {

    public static void loop(RobotController cnt) throws Exception {
        Bot.init(cnt);
        Nav.init(cnt);

        while (true) {
            try {
                Bot.mine_well(RobotType.MINER, 3, 4);
                Bot.yield_actions(rc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}