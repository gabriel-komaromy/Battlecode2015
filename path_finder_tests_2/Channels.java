package path_finder_tests_2;

import battlecode.common.*;

public class Channels {
    public static final int minrfct = 0;
    public static final int supdep = 1;
    public static final int barcks = 2;
    public static final int hlpd = 3;
    public static final int lnchr = 4;
    public static final int tnkfct = 5;
    public static final int techinst = 6;
    public static final int trngfld = 7;
    public static final int aerspclab = 8;
    public static final int hndwshstn = 9;
    public static final int minr = 10;
    public static final int bvr = 11;
    public static final int soldr = 12;
    public static final int drn = 13;
    public static final int cmdr = 14;
    public static final int cmptr = 15;
    public static final int misl = 16;
    public static final int tnk = 17;
    public static final int hq = 18;
    public static final int towr = 19;
    public static final int prelim_x_1 = 20;
    public static final int prelim_x_2 = 21;
    public static final int prelim_y_1 = 22;
    public static final int prelim_y_2 = 23;
    public static final int actual_x_1 = 24;
    public static final int actual_x_2 = 25;
    public static final int actual_y_1 = 26;
    public static final int actual_y_2 = 27;
    public static final int prelim_width = 28;
    public static final int prelim_height = 29;
    public static final int actual_width = 30;
    public static final int actual_height = 31;
    public static final int rally = 32;


    public static int getChannel(RobotType type) {
        switch (type) {
            case MINER:
                return minr;
            case BEAVER:
                return bvr;
            case SOLDIER:
                return soldr;
            case DRONE:
                return drn;
            case COMMANDER:
                return cmdr;
            case COMPUTER:
                return cmptr;
            case MISSILE:
                return misl;
            case TANK:
                return tnk;
            case LAUNCHER:
                return lnchr;
            case MINERFACTORY:
                return minrfct;
            case SUPPLYDEPOT:
                return supdep;
            case BARRACKS:
                return barcks;
            case HELIPAD:
                return hlpd;
            case TANKFACTORY:
                return tnkfct;
            case TECHNOLOGYINSTITUTE:
                return techinst;
            case TRAININGFIELD:
                return trngfld;
            case AEROSPACELAB:
                return aerspclab;
            case HANDWASHSTATION:
                return hndwshstn;
            case TOWER:
                return towr;
            default:
                return hq;
        }
    }
}