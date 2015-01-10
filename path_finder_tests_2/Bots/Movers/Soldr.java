package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Soldr extends Bot {

    public static void loop(RobotController cnt) throws Exception {
        try {
            Nav.init(cnt);
            Bot.init(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {
            try {
                MapLocation rally = Broadcast.readLocation(rc, 52);

                if (Bot.attackWeakest(RobotType.SOLDIER)) {
                    Bot.yield_actions(rc);
                } else {
                    if (Clock.getRoundNum() < 1000 && rally != null) {
                        int[] num = new int[0];
                        Nav.goTo(rally, Nav.Engage.YES, num);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}