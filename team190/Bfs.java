package team190;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

import java.lang.Math.*;

public class Bfs {

    private static int NUM_PAGES;
    private static int PAGE_SIZE;
    private static int MAP_HEIGHT;
    private static final int MAX_PAGES = 5;

    private static RobotController rc;

    public static void init(RobotController cnt) throws GameActionException {
        rc = cnt;
        MAP_HEIGHT = Broadcast.read(cnt, 51);
        PAGE_SIZE = MAP_HEIGHT * Broadcast.read(cnt, 50);
        if (PAGE_SIZE > 0) {
            NUM_PAGES = Math.min(40000 / PAGE_SIZE, MAX_PAGES);
        }
    }

    private static final int pageMetadataBaseChannel = GameConstants.BROADCAST_MAX_CHANNELS - 500;

    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_LOW = 1;

    // Page allocation:
    // From time to time various different robots will want to use the Bfs class to
    // calculate pathing information for various different destinations. In each case, we need
    // to be able to answer the following questions:
    // - Does a complete, undamaged pathfinding map already exist in some page for the specified destination?
    // If so, no point doing any more work on that destination.
    // - Is there another robot that is at this very moment computing pathing information for the specified destination?
    // If so, no point duplicating their work
    // - If no complete undamaged map exists and no other robot is working on the specified destination, is
    // there a free page that can be used to build a map for the specified destination? By "free" we mean a
    // page that (a) is not at this very moment being added to by another robot and (b) does not contain
    // pathing information for a destination more important than the specified one.
    // If such a free page exists, we can work on it.
    // metadata format:
    // fprrrrxxyy
    // f = finished or not
    // p = priority
    // rrrr = round last updated
    // xx = dest x coordinate
    // yy = dest y coordinate

    private static void writePageMetadata(int page, int roundLastUpdated, MapLocation dest, int priority, boolean finished, RobotController rc) throws GameActionException {
        int channel = pageMetadataBaseChannel + page;
        int data = (finished ? 1000000000 : 0) + 100000000 * priority + 10000 * roundLastUpdated + MAP_HEIGHT * dest.x + dest.y;
        Broadcast.bc(rc, channel, data);
    }

    private static boolean getMetadataIsFinished(int metadata) {
        return metadata >= 1000000000;
    }

    private static int getMetadataPriority(int metadata) {
        return (metadata % 1000000000) / 100000000;
    }

    private static int getMetadataRoundLastUpdated(int metadata) {
        return (metadata % 100000000) / 10000;
    }

    private static MapLocation getMetadataDestination(int metadata) throws GameActionException{
        metadata %= 10000;
        return new MapLocation(metadata / MAP_HEIGHT, metadata % MAP_HEIGHT);
    }

    private static int readPageMetadata(int page) throws GameActionException {
        int channel = pageMetadataBaseChannel + page;
        int data = Broadcast.read(Bot.rc, channel);
        return data;
    }

    private static int findFreePage(MapLocation dest, int priority) throws GameActionException {
        if (dest.equals(previousDest) && previousPage != -1) {
            int previousPageMetadata = readPageMetadata(previousPage);
            if (getMetadataRoundLastUpdated(previousPageMetadata) == previousRoundWorked && getMetadataDestination(previousPageMetadata).equals(dest)) {
                if (getMetadataIsFinished(previousPageMetadata)) {
                    return -1;
                } else {
                    return previousPage;
                }
            }
        }
        int lastRound = Clock.getRoundNum() - 1;
        int oldestPage = -1;
        int oldestPageRoundUpdated = 999999;
        for (int page = 0; page < NUM_PAGES; page++) {
            int metadata = readPageMetadata(page);
            if (metadata == 0) {
                if (oldestPageRoundUpdated > 0) {
                    oldestPage = page;
                    oldestPageRoundUpdated = 0;
                }
            } else {
                int roundUpdated = getMetadataRoundLastUpdated(metadata);
                boolean isFinished = getMetadataIsFinished(metadata);
                if (roundUpdated >= lastRound || isFinished) {
                    if (getMetadataDestination(metadata).equals(dest)) {
                        return -1;
                    }
                }
                if (roundUpdated < oldestPageRoundUpdated) {
                    oldestPageRoundUpdated = roundUpdated;
                    oldestPage = page;
                }
            }
        }

        if (oldestPage != -1 && oldestPageRoundUpdated < lastRound) return oldestPage;

        if (priority == PRIORITY_HIGH) return 0;

        return -1;
    }

    private static MapLocation[] locQueue = new MapLocation[GameConstants.MAP_MAX_WIDTH * GameConstants.MAP_MAX_HEIGHT];
    private static int locQueueHead = 0;
    private static int locQueueTail = 0;
    private static boolean[][] wasQueued = new boolean[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];

    private static Direction[] dirs = new Direction[]{Direction.NORTH_WEST, Direction.SOUTH_WEST, Direction.SOUTH_EAST, Direction.NORTH_EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};
    private static int[] dirsX = new int[]{1, 1, -1, -1, 0, 1, 0, -1};
    private static int[] dirsY = new int[]{1, -1, -1, 1, 1, 0, -1, 0};

    private static MapLocation previousDest = null;
    private static int previousRoundWorked = -1;
    private static int previousPage = -1;

    private static void initQueue(MapLocation dest) {
        locQueueHead = 0;
        locQueueTail = 0;

        wasQueued = new boolean[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];

        locQueue[locQueueTail] = dest;
        locQueueTail++;
        wasQueued[Math.abs(dest.x) % GameConstants.MAP_MAX_WIDTH][Math.abs(dest.y) % GameConstants.MAP_MAX_HEIGHT] = true;
    }

    public static void work(MapLocation dest, int priority, int bytecodeLimit, RobotController rc) throws GameActionException {
        int page = findFreePage(dest, priority);
        if ((page == -1) || (MAP_HEIGHT == 0)) return;

        if (!dest.equals(previousDest)) {
            initQueue(dest);
        }

        previousDest = dest;
        previousRoundWorked = Clock.getRoundNum();
        previousPage = page;

        int mapWidth = Broadcast.read(rc, 50);
        int mapHeight = Broadcast.read(rc, 51);
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        int destDistSqToEnemyHQ = dest.distanceSquaredTo(enemyHQ);
        boolean destInSpawn = destDistSqToEnemyHQ <= 25;

        while (locQueueHead != locQueueTail && Clock.getBytecodeNum() < bytecodeLimit) {
            MapLocation loc = locQueue[locQueueHead];
            locQueueHead++;
            if (loc.equals(Bot.ourHQ) && !loc.equals(dest)) continue;

            int locX = loc.x;
            int locY = loc.y;
            for (int i = 8; i-- > 0;) {
                int x = locX + dirsX[i];
                int y = locY + dirsY[i];
                if (x >= 0 && y >= 0 && x < mapWidth && y < mapHeight && !wasQueued[x][y]) {
                    MapLocation newLoc = new MapLocation(x, y);
                    if (rc.senseTerrainTile(newLoc) != TerrainTile.VOID) {
                        if (destInSpawn) {
                            if (Bot.isInTheirStaticAttackRange(loc)) {
                                if (Bot.theirHQ.distanceSquaredTo(newLoc) < Bot.theirHQ.distanceSquaredTo(loc))
                                    continue;
                            } else {
                                if (Bot.isInTheirStaticAttackRange(newLoc)) continue;
                            }
                            publishResult(page, newLoc, dest, dirs[i]);

                            locQueue[locQueueTail] = newLoc;
                            locQueueTail++;
                            wasQueued[x][y] = true;
                        }
                    }
                }
            }
        }

        boolean finished = locQueueHead == locQueueTail;
        writePageMetadata(page, Clock.getRoundNum(), dest, priority, finished, rc);
    }

    private static int locChannel(int page, MapLocation loc) {
        return PAGE_SIZE * page + MAP_HEIGHT * (loc.x % 100) + loc.y;
    }

    // We store the data in this format:
    // 10d0xxyy
    // 1 = validation to prevent mistaking the initial 0 value for a valid pathing instruction
    // d = direction to move
    // xx = x coordinate of destination
    // yy = y coordinate of destination
    private static void publishResult(int page, MapLocation here, MapLocation dest, Direction dir) throws GameActionException {
        int data = 10000000 + (dir.ordinal() * 100000) + (dest.x * MAP_HEIGHT) + (dest.y);
        int channel = locChannel(page, here);
        System.out.println(channel);
        Broadcast.bc(rc, channel, data);
    }

    public static Direction readResult(MapLocation here, MapLocation dest) throws GameActionException {
        for (int page = 0; page < NUM_PAGES; page++) {
            int data = rc.readBroadcast(locChannel(page, here));
            if (data != 0) {
                data -= 10000000;
                if (((dest.x * MAP_HEIGHT) + (dest.y)) == (data % 100000)) {
                    return Direction.values()[data / 100000];
                }
            }
        }
        return null;
    }
}