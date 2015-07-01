package team190;

import battlecode.common.*;
import battlecode.common.MapLocation;

import java.lang.*;
import java.util.*;

public class Bashr extends Bot {
    private static MapLocation rally;
    private static boolean engage;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            Nav.init(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {

            try {
                if (!Bot.bfsInitialized) {
                    if (Broadcast.read(rc, 52) == 1) {
                        Bfs.init(rc);
                        Bot.setBfsInitialized(true);
                    }
                }
                int rd = Clock.getRoundNum();
                rally = Broadcast.readLocation(rc, 53);
                if (Broadcast.read(rc, 55) == 1) {
                    engage = true;
                } else {
                    engage = false;
                }
                if (rally != null) {
                    int[] num = new int[0];

                    if (engage) {
                        Nav.goTo(rally, Nav.Engage.YES, num);
                    } else {
                        Nav.goTo(rally, Nav.Engage.NO, num);
                    }                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}