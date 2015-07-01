package team190;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class SupDep extends Bot {

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
        }  catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}