package team190;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class TnkFct extends Bot {

    private static int max_tnks;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            max_tnks = Broadcast.read(rc, Channels.getChannelMax(RobotType.TANK));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (Clock.getRoundNum() % 20 == 0) {
                    max_tnks = Broadcast.read(rc, Channels.getChannelMax(RobotType.TANK));
                }
                Bot.spawnUnit(RobotType.TANK, max_tnks);
                Bot.yield_actions(rc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}