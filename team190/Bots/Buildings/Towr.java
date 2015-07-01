package team190;

import battlecode.common.*;

public class Towr extends Bot {
    public static void loop(RobotController cnt) throws Exception {
        Bot.init(cnt);
        while (true) {
            try {
                if (rc.isWeaponReady() == true) {
                    Bot.attackWeakest(RobotType.TOWER);
                }

            } catch (Exception e) {

            }
        }

    }
}