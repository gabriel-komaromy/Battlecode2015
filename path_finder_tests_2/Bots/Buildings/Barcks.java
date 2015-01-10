package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Barcks extends Bot {
    private static int max_soldrs;
    private static int max_bashrs;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            max_soldrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.SOLDIER));
            max_bashrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.BASHER));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (Clock.getRoundNum() % 20 == 0) {
                    max_soldrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.SOLDIER));
                    max_bashrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.BASHER));
                }
                if (rand.nextDouble() <= 0.5) {
                    Bot.spawnUnit(RobotType.SOLDIER, max_soldrs);
                } else {
                    Bot.spawnUnit(RobotType.BASHER, max_bashrs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}