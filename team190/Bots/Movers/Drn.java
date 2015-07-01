package team190;

import battlecode.common.*;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import java.lang.*;
import java.util.*;

public class Drn extends Bot {
    private static MapLocation loc;
    private static MapLocation rally;
    private static MapLocation next;
    private static MapLocation right;
    private static MapLocation left;
    private static Direction toRally;
    private static Direction toRally_save;
    private static boolean moved = false;
    private static int tries = 0;

    public static void loop(RobotController cnt) throws Exception {
        try {
            Bot.init(cnt);
            Nav.init(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (rc.isCoreReady()) {
                    Bot.attackWeakest(RobotType.DRONE);
                    loc = rc.getLocation();
                    rally = Broadcast.readLocation(rc, 56);
                    toRally = loc.directionTo(rally);
                    toRally_save = loc.directionTo(rally);

                    if (tries > 20) {
                        Direction trial;
                        if (Bot.rand.nextDouble() <= 0.5) {
                            trial = toRally_save.rotateLeft();
                        } else {
                            trial = toRally_save.rotateRight();
                        }
                        MapLocation try_loc = rc.getLocation().add(trial, 4);
                        if (!Bot.isInTheirStaticAttackRange(try_loc) && !attackedByTanksMissilesBashers(try_loc)) {
                            toRally = trial;
                            moved = droneMove(try_loc, loc, trial);
                        }
                    }

                    moved = droneMove(rally, loc, toRally);

                    if (!moved) {
                        //retreat
                        toRally = toRally_save;
                        if (rc.canMove(toRally.opposite()) && !Bot.isInTheirStaticAttackRange(loc.add(toRally.opposite()))) {
                            try_move(toRally.opposite());
                        } else if (rc.canMove(toRally.opposite().rotateLeft()) && !Bot.isInTheirStaticAttackRange(loc.add(toRally.opposite().rotateLeft()))) {
                            try_move(toRally.opposite().rotateLeft());
                        } else if (rc.canMove(toRally.opposite().rotateRight()) && !Bot.isInTheirStaticAttackRange(loc.add(toRally.opposite().rotateRight()))) {
                            try_move(toRally.opposite().rotateRight());
                        }
                    }
                }

                Bot.yield_actions(rc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean droneMove(MapLocation rally, MapLocation loc, Direction toRally) throws GameActionException {
        next = loc.add(toRally);
        boolean done = false;
        if (rc.canMove(toRally) && !Bot.isInTheirStaticAttackRange(next) && !attackedByTanksMissilesBashers(next)) {
            done = true;
            try_move(toRally);
        } else if (Bot.rand.nextDouble() <= 0.5) {
            toRally = toRally.rotateLeft();
            left = loc.add(toRally);
            if (rc.canMove(toRally) && !Bot.isInTheirStaticAttackRange(left) && !attackedByTanksMissilesBashers(left)) {
                done = true;
                tries++;
                try_move(toRally);
            } else {
                toRally = toRally.rotateRight().rotateRight();
                right = loc.add(toRally);
                if (rc.canMove(toRally) && !Bot.isInTheirStaticAttackRange(right) && !attackedByTanksMissilesBashers(right)) {
                    done = true;
                    tries++;
                    try_move(toRally);
                }
            }
        } else {
            toRally = toRally.rotateRight();
            right = loc.add(toRally);
            if (rc.canMove(toRally) && !Bot.isInTheirStaticAttackRange(right) && !attackedByTanksMissilesBashers(right)) {
                done = true;
                tries++;
                try_move(toRally);
            } else {
                toRally = toRally.rotateLeft().rotateLeft();
                left = loc.add(toRally);
                if (rc.canMove(toRally) && !Bot.isInTheirStaticAttackRange(left) && !attackedByTanksMissilesBashers(left)) {
                    done = true;
                    tries++;
                    try_move(toRally);
                }
            }
        }
        return done;
    }

    private static boolean attackedByTanksMissilesBashers(MapLocation loc) {
        int[] attackers = Bot.countNumEnemiesAttackingLoc(loc);
        if (attackers[0] + attackers[3] + attackers[5] > 0) {
            return true;
        }

        return false;
    }

    private static void try_move(Direction dir) throws GameActionException {
        if (rc.isCoreReady()) {
            rc.move(dir);
        }
    }
}