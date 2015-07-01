package team190;

import battlecode.common.*;
import battlecode.common.GameActionException;

import java.lang.*;
import java.util.*;

public class MinrFct extends Bot {
    private static int max_minrs = 10;

    public static void loop(RobotController cnt) throws GameActionException {
        try {
            Bot.init(cnt);
            max_minrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.MINER));

        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (Clock.getRoundNum() % 20 == 0) {
                    max_minrs = Broadcast.read(rc, Channels.getChannelMax(RobotType.MINER));
                }

                Bot.spawnUnit(RobotType.MINER, max_minrs);
                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}