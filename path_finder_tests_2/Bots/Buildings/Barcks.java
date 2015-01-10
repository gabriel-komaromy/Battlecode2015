package path_finder_tests_2;

import battlecode.common.*;

import java.lang.*;
import java.util.*;

public class Barcks extends Bot {
    private static int max_soldrs = 20;
    private static int max_bashrs = 20;

    public static int getMax_soldrs() {
        return max_soldrs;
    }

    public static void setMax_soldrs(int max_soldrs) {
        Barcks.max_soldrs = max_soldrs;
    }

    public static int getMax_bashrs() {
        return max_bashrs;
    }

    public static void setMax_bashrs(int max_bashrs) {
        Barcks.max_bashrs = max_bashrs;
    }

    public static void loop(RobotController cnt) throws Exception {
        Bot.init(cnt);
        while (true) {
            try {
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