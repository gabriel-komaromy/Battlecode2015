package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class AerSpcLab extends Bot {

    private static int max_lnchrs;

    public static void loop(RobotController cnt) throws Exception {
        Bot.init(cnt);
        max_lnchrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.LAUNCHER));

        while (true) {
            try {
                if (Clock.getRoundNum() % 20 == 0) {
                    max_lnchrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.LAUNCHER));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}