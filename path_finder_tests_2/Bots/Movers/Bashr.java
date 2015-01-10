package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Bashr extends Bot {

    public static void loop(RobotController cnt) throws Exception {
        Nav.init(cnt);
        Bot.init(cnt);
        MapLocation rally = Broadcast.readLocation(rc, Channels.rally);

        while (true) {
            try {
                if (Clock.getRoundNum() < 1000 && rally != null) {
                        Nav.goTo(rally, Nav.Engage.YES);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}