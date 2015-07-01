package team190;

import battlecode.common.*;

public class Channels {

    public static int getChannel(RobotType type) {
        switch (type) {
            case MINER:
                return 10;
            case BEAVER:
                return 11;
            case SOLDIER:
                return 12;
            case DRONE:
                return 13;
            case COMMANDER:
                return 14;
            case COMPUTER:
                return 15;
            case MISSILE:
                return 16;
            case TANK:
                return 17;
            case LAUNCHER:
                return 4;
            case MINERFACTORY:
                return 0;
            case SUPPLYDEPOT:
                return 1;
            case BARRACKS:
                return 2;
            case HELIPAD:
                return 3;
            case TANKFACTORY:
                return 5;
            case TECHNOLOGYINSTITUTE:
                return 6;
            case TRAININGFIELD:
                return 7;
            case AEROSPACELAB:
                return 8;
            case HANDWASHSTATION:
                return 9;
            case TOWER:
                return 19;
            default:
                return 18;
        }
    }

    public static int getChannelMax(RobotType type) {
        return getChannel(type) + 20;
    }
}