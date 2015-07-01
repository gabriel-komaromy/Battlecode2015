package team190;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Cmptr extends Bot {
    public static boolean bfsInitialized = false;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (!Bot.bfsInitialized) {
                    if (Broadcast.read(rc, 52) == 1) {
                        Bfs.init(rc);
                        bfsInitialized = true;
                    }
                }
                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}