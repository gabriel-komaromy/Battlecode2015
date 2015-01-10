package path_finder_tests;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

import java.lang.Exception;
import java.lang.StringBuilder;
import java.util.*;
import java.lang.Math.*;

public class RobotPlayer {
    static RobotController rc;
    static Random rand;
    static Direction facing;
    static Team us;
    static Team them;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};


    public static void run(RobotController cnt) {
        rc = cnt;
        rand = new Random(rc.getID());
        facing = getRandomDirection();
        us = rc.getTeam();
        them = us.opponent();
        while (true) {
            try {
                switch(rc.getType()) {
                    case HQ:
                        spawnUnit(RobotType.BEAVER);
                        break;
                    case BEAVER:
                        bugNav(RobotType.BEAVER, rc.senseEnemyHQLocation());
                        break;
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            rc.yield();
        }
    }

    private static void spawnUnit(RobotType type) throws Exception {
        Direction randomDir = getRandomDirection();
        if (rc.isCoreReady() && rc.canSpawn(randomDir, type)) {
            rc.spawn(randomDir, type);
        }
    }

    private static Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    private static class Square {
        MapLocation location;
        int length;
        Square prev_square;
        boolean is_tracing;
        boolean direction;   // false is ccw

        // direction is cw or ccw
        public Square(MapLocation location, int length, boolean is_tracing, boolean direction) {
            this.location = location;
            this.length = length;
            this.is_tracing = is_tracing;
            this.direction = direction;
        }

        public void set_prev_square(Square s) {
            this.prev_square = s;
        }

        public MapLocation getLocation() {
            return location;
        }
    }

    private static class SquareSet {
        private static final int HASH = Math.max(GameConstants.MAP_MAX_HEIGHT, GameConstants.MAP_MAX_WIDTH);

        private int size = 0;
        private Square[][] grid = new Square[HASH][HASH];

        public void add(Square square) {
            MapLocation loc = square.getLocation();
            int x = loc.x % HASH;
            int y = loc.y % HASH;
            if (grid[x][y] == null) {
                size++;
                grid[x][y] = square;
            }
        }

        public void remove(Square square) {
            MapLocation loc = square.getLocation();
            int x = loc.x % HASH;
            int y = loc.y % HASH;
            if (grid[x][y] != null) {
                size--;
                grid[x][y] = null;
            }
        }

        public boolean contains(Square square) {
            MapLocation loc = square.getLocation();
            return (grid[loc.x % HASH][loc.y % HASH] != null);
        }

        public void clear() {
            grid = new Square[HASH][HASH];
            size = 0;
        }

        public int getSize() {
            return this.size;
        }

        public Square getLowestCostSquare() {
            int min_length = -1;
            Square lowest_cost_square;
            return null;

        }
    }

    private static class FastIterableLocSet {
        private int size = 0;
        private StringBuilder keys = new StringBuilder();

        private String locToStr(MapLocation loc) {
            return "^" + (char) (loc.x) + (char) (loc.y);
        }

        public void add(MapLocation loc) {
            String key = locToStr(loc);
            if (keys.indexOf(key) == -1) {
                keys.append(key);
                size++;
            }
        }

        public void remove(MapLocation loc) {
            String key = locToStr(loc);
            int index;
            if ((index = keys.indexOf(key)) != -1) {
                keys.delete(index, index + 3);
                size--;
            }
        }

        public boolean contains(MapLocation loc) {
            return keys.indexOf(locToStr(loc)) != -1;
        }

        public void clear() {
            keys = new StringBuilder();
            size = 0;
        }

        public MapLocation[] getKeys() {
            MapLocation[] locs = new MapLocation[size];
            for (int i = 0; i < size; i++) {
                locs[i] = new MapLocation(keys.charAt(i * 3 + 1), keys.charAt(i * 3 + 2));
            }
            return locs;
        }

        public void replace(String newSet) {
            keys.replace(0, keys.length(), newSet);
            size = newSet.length() / 3;
        }
    }

    private static void tangentBug(RobotType type, MapLocation dest) throws Exception {
        SquareSet open = new SquareSet();
        int length = 0;

        open.add(new Square(rc.getLocation(), length, false, false));

        while (open.getSize() > 0) {

        }
    }

    private static void bugNav(RobotType type, MapLocation dest) throws Exception {
        MapLocation src = rc.getLocation();
        Direction next = src.directionTo(dest);
        MapLocation tileInFront = src.add(next);
        MapLocation next_on_line = tileInFront.add(next);
        boolean blocked = false;
        int rad = type.sensorRadiusSquared;
        while (src.distanceSquaredTo(next_on_line) < rad) {
            if (!rc.isPathable(type, next_on_line)) {
                blocked = true;
                break;
            }
            next_on_line = next_on_line.add(next);
        }

        if (blocked) {
            int rotation = (int) (rand.nextDouble() * 2);
            rc.broadcast(rc.getID() % 5000, rotation);
        }

        /*
        if (blocked) {
            MapLocation opening = check_tile_neighbors(src, next_on_line, next, rad, type);
            System.out.println(opening.toString());
            if (!opening.equals(src)) {
                next = src.directionTo(opening);
            } else {
                next = getRandomDirection(); // wander aimlessly, basically
            }
        }
         **/

        if (rc.canMove(next)) {
            ;
        } else if (rc.canMove(next.rotateLeft())) {
            next = next.rotateLeft();
        } else if (rc.canMove(next.rotateRight())) {
            next = next.rotateRight();
        }
        if (rc.isCoreReady() && rc.canMove(next)) {
            rc.move(next);
        }
    }

    private static MapLocation check_tile_neighbors(MapLocation src, MapLocation loc, Direction dir, int rad, RobotType type) {
        Direction left = dir.opposite().rotateLeft();
        Direction right = dir.opposite().rotateRight();

        MapLocation left_tile = loc.add(left);
        MapLocation right_tile = loc.add(right);


        while (rc.canSenseLocation(left_tile) || rc.canSenseLocation(right_tile)) {
            if (rc.isPathable(type, left_tile)) {
                return left_tile;
            } else if (rc.isPathable(type, right_tile)) {
                return right_tile;
            }
            left_tile = left_tile.add(left);
            right_tile = right_tile.add(right);
        }

        return src;
    }
}