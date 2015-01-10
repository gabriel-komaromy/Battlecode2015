package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class TechInst extends Bot {

    private static int max_cmptrs;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            max_cmptrs = Broadcast.read(rc, Channels.getChannel(RobotType.COMPUTER));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (Clock.getRoundNum() % 20 == 0) {
                    max_cmptrs = Broadcast.read(rc, Channels.getChannel(RobotType.COMPUTER));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}