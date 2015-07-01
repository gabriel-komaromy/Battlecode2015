package team190;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Hlpd extends Bot {

    private static int max_drns;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            max_drns = Broadcast.read(rc, Channels.getChannelMax(RobotType.DRONE));
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {
            try {
                if (Clock.getRoundNum() % 20 == 0) {
                    max_drns = Broadcast.read(rc, Channels.getChannelMax(RobotType.DRONE));
                }

                Bot.spawnUnit(RobotType.DRONE, max_drns);
                Bot.yield_actions(rc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}