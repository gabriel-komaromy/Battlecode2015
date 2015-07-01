package team190;

import battlecode.common.*;
import battlecode.common.RobotType;

public class RobotPlayer {

    public static void run(RobotController rc) throws Exception {

        switch (rc.getType()) {
            case HQ:
                HQueue.loop(rc);
                break;
            case BEAVER:
                Bvr.loop(rc);
                break;
            case TOWER:
                Towr.loop(rc);
                break;
            case MINER:
                Minr.loop(rc);
                break;
            case AEROSPACELAB:
                AerSpcLab.loop(rc);
                break;
            case BARRACKS:
                Barcks.loop(rc);
                break;
            case BASHER:
                Bashr.loop(rc);
                break;
            case COMMANDER:
                Cmdr.loop(rc);
                break;
            case DRONE:
                Drn.loop(rc);
                break;
            case HELIPAD:
                Hlpd.loop(rc);
                break;
            case HANDWASHSTATION:
                HndWshStn.loop(rc);
                break;
            case LAUNCHER:
                Lnchr.loop(rc);
                break;
            case MINERFACTORY:
                MinrFct.loop(rc);
                break;
            case MISSILE:
                Misl.loop(rc);
                break;
            case SOLDIER:
                Soldr.loop(rc);
                break;
            case SUPPLYDEPOT:
                SupDep.loop(rc);
                break;
            case TANK:
                Tnk.loop(rc);
                break;
            case TANKFACTORY:
                TnkFct.loop(rc);
                break;
            case TECHNOLOGYINSTITUTE:
                TechInst.loop(rc);
                break;
            case TRAININGFIELD:
                TrngFld.loop(rc);
                break;
        }
    }

    /*

    **/
}