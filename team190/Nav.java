package team190;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

import java.lang.Exception;

public class Nav {
    private static MapLocation dest;
    private static RobotController rc;
    private static boolean engage = false;
    private static int[] numEnemiesAttackingMoveDirs;

    private enum BugState {
        DIRECT,
        BUG
    }

    private enum WallSide {
        LEFT,
        RIGHT
    }

    private static BugState bugState;
    private static WallSide bugWallSide = WallSide.LEFT;
    private static int bugStartDistSq;
    private static Direction bugLastMoveDir;
    private static Direction bugLookStartDir;
    private static int bugRotationCount;
    private static int bugMovesSinceSeenObstacle = 0;

    private static boolean tryMoveDirect() throws GameActionException {
        MapLocation here = rc.getLocation();
        Direction toDest = here.directionTo(dest);
        Direction[] dirs = new Direction[3];
        dirs[0] = toDest;
        Direction dirLeft = toDest.rotateLeft();
        Direction dirRight = toDest.rotateRight();
        if (here.add(dirLeft).distanceSquaredTo(dest) < here.add(dirRight).distanceSquaredTo(dest)) {
            dirs[1] = dirLeft;
            dirs[2] = dirRight;
        } else {
            dirs[1] = dirRight;
            dirs[2] = dirLeft;
        }
        for (Direction dir : dirs) {
            if (canMoveSafely(dir)) {
                if (moveIsAllowedByEngagementRules(dir)) {
                    moveAndAttack(rc, dir);
                    return true;
                }
            }
        }
        return false;
    }

    private static void moveAndAttack(RobotController rc, Direction dir) throws GameActionException {
        RobotType type = rc.getType();
        if (rc.senseNearbyRobots(type.attackRadiusSquared, Bot.them).length != 0) {
                Bot.attackWeakest(type);
        } else {
            move(dir);
        }
    }
    private static boolean moveIsAllowedByEngagementRules(Direction dir) throws GameActionException {
        return true;
    }

    private static void bugTo(MapLocation theDest) throws GameActionException {
        if (bugState == BugState.BUG) {
            if (canEndBug()) {
                bugState = BugState.DIRECT;
            }
        }

        if (bugState == BugState.DIRECT) {
            if (!tryMoveDirect()) {
                bugState = BugState.BUG;
                startBug();
            }
        }

        if (bugState == BugState.BUG) {
            bugTurn();
        }
    }

    private static boolean canEndBug() {
        if (bugMovesSinceSeenObstacle >= 4) return true;
        return (bugRotationCount <= 0 || bugRotationCount >= 8) && rc.getLocation().distanceSquaredTo(dest) <= bugStartDistSq;
    }

    private static void bugTurn() throws GameActionException {
        if (detectBugIntoEdge()) {
            reverseBugWallFollowDir();
        }
        Direction dir = findBugMoveDir();
        if (dir != null) {
            bugMove(dir);
        }
    }

    private static void reverseBugWallFollowDir() throws GameActionException {
        bugWallSide = (bugWallSide == WallSide.LEFT ? WallSide.RIGHT : WallSide.LEFT);
        startBug();
    }

    private static boolean detectBugIntoEdge() {
        if (rc.senseTerrainTile(rc.getLocation().add(bugLastMoveDir)) != TerrainTile.OFF_MAP) return false;

        if (bugLastMoveDir.isDiagonal()) {
            if (bugWallSide == WallSide.LEFT) {
                return !canMoveSafely(bugLastMoveDir.rotateLeft());
            } else {
                return !canMoveSafely(bugLastMoveDir.rotateRight());
            }
        } else {
            return true;
        }
    }

    private static void startBug() throws GameActionException {
        bugStartDistSq = rc.getLocation().distanceSquaredTo(dest);
        bugLastMoveDir = rc.getLocation().directionTo(dest);
        bugLookStartDir = rc.getLocation().directionTo(dest);
        bugRotationCount = 0;
        bugMovesSinceSeenObstacle = 0;

        Direction leftTryDir = bugLastMoveDir.rotateLeft();
        for (int i = 0; i < 3; i++) {
            if(!canMoveSafely(leftTryDir) || !moveIsAllowedByEngagementRules(leftTryDir)) leftTryDir = leftTryDir.rotateLeft();
            else break;
        }
        Direction rightTryDir = bugLastMoveDir.rotateRight();
        for (int i = 0; i < 3; i++) {
            if (!canMoveSafely(rightTryDir) || !moveIsAllowedByEngagementRules(rightTryDir)) rightTryDir = rightTryDir.rotateRight();
            else break;
        }
        if (dest.distanceSquaredTo(rc.getLocation().add(leftTryDir)) < dest.distanceSquaredTo(rc.getLocation().add(rightTryDir))) {
            bugWallSide = WallSide.RIGHT;
        } else {
            bugWallSide = WallSide.LEFT;
        }
    }

    private static boolean canMoveSafely(Direction dir) {
        return rc.canMove(dir) && (!Bot.isInTheirStaticAttackRange(rc.getLocation().add(dir)) || engage);
    }

    private static Direction findBugMoveDir() throws GameActionException {
        bugMovesSinceSeenObstacle++;
        Direction dir = bugLookStartDir;
        for (int i = 8; i-- > 0;) {
            if (canMoveSafely(dir) && moveIsAllowedByEngagementRules(dir)) return dir;
            dir = (bugWallSide == WallSide.LEFT ? dir.rotateRight() : dir.rotateLeft());
            bugMovesSinceSeenObstacle = 0;
        }
        return null;
    }

    private static void bugMove(Direction dir) throws GameActionException {
        moveAndAttack(rc, dir);
        bugRotationCount += calculateBugRotation(dir);
        bugLastMoveDir = dir;

        if (bugWallSide == WallSide.LEFT) bugLookStartDir = dir.rotateLeft().rotateLeft();
        else bugLookStartDir = dir.rotateRight().rotateRight();
    }

    private static int calculateBugRotation(Direction moveDir) {
        if (bugWallSide == WallSide.LEFT) {
            return numRightRotations(bugLookStartDir, moveDir) - numRightRotations(bugLookStartDir, bugLastMoveDir);
        } else {
            return numLeftRotations(bugLookStartDir, moveDir) - numLeftRotations(bugLookStartDir, bugLookStartDir);
        }
    }

    private static int numRightRotations(Direction start, Direction end) {
        return (end.ordinal() - start.ordinal() + 8) % 8;
    }

    private static int numLeftRotations(Direction start, Direction end) {
        return (-end.ordinal() + start.ordinal() + 8) % 8;
    }

    private static void move(Direction toDest) throws GameActionException{
        if (rc.isCoreReady() && rc.canMove(toDest)) {
            rc.move(toDest);
        }
    }

    private static boolean tryMoveBfs(MapLocation here) throws GameActionException {
        if (Bot.bfsInitialized) {
            Direction bfsDir = Bfs.readResult(here, dest);

            if (bfsDir == null) return false;

            Direction[] dirs = new Direction[]{bfsDir, bfsDir.rotateLeft(), bfsDir.rotateRight()};
            Direction bestDir = null;
            for (Direction dir : dirs) {
                if (canMoveSafely(dir)) {
                    if (moveIsAllowedByEngagementRules(dir)) {
                        moveAndAttack(rc, dir);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void init(RobotController theRC) {
        rc = theRC;
    }

    public enum Engage {
        YES,
        NO
    }

    public static void goTo(MapLocation theDest, Engage theEngage, int[] theNumEnemiesAttackingMoveDirs) throws GameActionException{
        engage = (theEngage == Engage.YES);

        numEnemiesAttackingMoveDirs = theNumEnemiesAttackingMoveDirs;

        if (!theDest.equals(dest)) {
            dest = theDest;
            bugState = BugState.DIRECT;
        }

        MapLocation here = rc.getLocation();

        if (here.equals(theDest)) return;

        if (tryMoveBfs(here)) {
            bugState = BugState.DIRECT;
            return;
        }

        bugTo(dest);

    }
}