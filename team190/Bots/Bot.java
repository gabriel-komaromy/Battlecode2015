package team190;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;
import battlecode.world.Robot;

import java.lang.Math.*;
import java.util.*;

public class Bot {
    static RobotController rc;
    static Team us;
    static Team them;
    static Random rand;
    static MapLocation ourHQ, theirHQ;
    static MapLocation[] ourTowers, theirTowers;
    //static int[] cachedNumEnemiesAttackingMoveDirs;

    public static void setBfsInitialized(boolean bfsInitialized) {
        Bot.bfsInitialized = bfsInitialized;
    }

    public static boolean bfsInitialized = false;

    public static void init(RobotController cnt) throws GameActionException {
        rc = cnt;
        us = rc.getTeam();
        them = us.opponent();
        rand = new Random(rc.getID());
        theirHQ = rc.senseEnemyHQLocation();
        theirTowers = rc.senseEnemyTowerLocations();
        ourHQ = rc.senseHQLocation();
        ourTowers = rc.senseTowerLocations();
    }

    public static boolean attackWeakest(RobotType type) throws GameActionException {
        if (type != RobotType.LAUNCHER) {
            if (rc.isWeaponReady()) {
                RobotInfo[] near = rc.senseNearbyRobots(type.attackRadiusSquared, them);
                if (near.length > 0) {
                    double lowest_health = near[0].health;
                    RobotInfo weakest = near[0];
                    for (RobotInfo r : near) {
                        if (r.health < lowest_health) {
                            weakest = r;
                            lowest_health = r.health;
                        }
                    }
                    rc.attackLocation(weakest.location);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean spawnUnit(RobotType type, int max_units) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(theirHQ);
        if (Broadcast.read(rc, Channels.getChannel(type)) < max_units) {
            while (!spawnUnit(type, max_units, dir)) {
                dir = Bot.getRandomDirection();
            }
        }
        return true;
    }

    public static boolean spawnUnit(RobotType type, int max_units, Direction dir) throws GameActionException {
        if (Broadcast.read(rc, Channels.getChannel(type)) < max_units) {
            boolean spawned = false;
            if (rc.isCoreReady() && rc.canSpawn(dir, type)) {
                rc.spawn(dir, type);
                Broadcast.incChannel(rc, Channels.getChannel(type));
                spawned = true;
            }
            /*while (!spawned) {
                dir = getRandomDirection();
                if (rc.isCoreReady() && rc.canSpawn(dir, type)) {
                    rc.spawn(dir, type);
                    spawned = true;
                }
            }*/
            return spawned;
        }
        return false;
    }

    public static Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    public static void mine_well(RobotType type, int constant, int div) throws GameActionException {
        if (rc.isCoreReady()) {
            MapLocation here = rc.getLocation();
            MapLocation best = here;
            MapLocation test;

            double compareTo = .5;
            if (type == RobotType.MINER) {
                compareTo = .5;
            }
            double best_rate = Math.min(constant, rc.senseOre(here) / div);
            if (best_rate >= compareTo) {
            } else {
                MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(here, type.sensorRadiusSquared);
                int element = (int) (rand.nextDouble() * locs.length);
                for (int i = element; i < locs.length; i++) {
                    test = locs[i];
                    if (location_rate(test, constant, div) > best_rate && rc.isPathable(type, test)) {
                        best = test;
                        best_rate = location_rate(test, constant, div);
                        if (best_rate >= compareTo) {
                            break;
                        }
                    }
                }
                if (best_rate < compareTo) {
                    for (int i = 0; i < element; i++) {
                        test = locs[i];
                        if (location_rate(test, constant, div) > best_rate && rc.isPathable(type, test)) {
                            best = test;
                            best_rate = location_rate(test, constant, div);
                            if (best_rate >= compareTo) {
                                break;
                            }
                        }
                    }
                }
            }

            if (best_rate > 0) {
                if (best.equals(here)) {
                    if (rc.canMine()) {
                        rc.mine();
                    }
                } else {
                    int[] num = new int[0];

                    Nav.goTo(best, Nav.Engage.NO, num);
                }
            }
        }
    }


    private static double location_rate(MapLocation loc, int constant, int div) throws GameActionException {
        return Math.min(constant, rc.senseOre(loc) / div);
    }

    public static void transfer_Supplies() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(), GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, us);
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for (RobotInfo ri : nearbyAllies) {
            if (ri.supplyLevel < lowestSupply) {
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                suppliesToThisLocation = ri.location;
            }
        }
        if (suppliesToThisLocation != null) {
            rc.transferSupplies((int) transferAmount, suppliesToThisLocation);
        }
    }

    public static void yield_actions(RobotController rc) throws GameActionException {
        transfer_Supplies();
        int left = Clock.getBytecodesLeft();
        if (left > 100) {
            updateEdges(rc);
            if (Broadcast.read(rc, 52) != 0) {
                if (!Bot.bfsInitialized) {
                    Bfs.init(rc);
                    Bot.bfsInitialized = true;
                }
                int rally_x = Broadcast.read(rc, 53);
                if (rally_x != 0) {
                    Bfs.work(new MapLocation(rally_x, Broadcast.read(rc, 54)), Bfs.PRIORITY_HIGH, 2000, rc);
                }
            }
        }

        rc.yield();
    }

    public static boolean isInTheirStaticAttackRange(MapLocation loc) {
        int range_sq = theirHQ.distanceSquaredTo(loc);
        if (range_sq < 25 || (loc.x == theirHQ.x && loc.y == theirHQ.y)) {
            return true;
        }
        for (MapLocation tower : theirTowers) {
            range_sq = tower.distanceSquaredTo(loc);
            if (range_sq < 25 || (loc.x == tower.x && loc.y == tower.y)) {
                return true;
            }
        }
        return false;
    }

    public static void updateEdges(RobotController rc) throws GameActionException {
        if (Broadcast.read(rc, 52) == 0) {
            MapLocation loc = rc.getLocation();
            int rad = (int) Math.sqrt(rc.getType().sensorRadiusSquared);
            boolean x_finalized = false;
            boolean y_finalized = false;

            if (Broadcast.read(rc, 44) == 0) {
                int lowest_x = Broadcast.read(rc, 40);
                int highest_x = Broadcast.read(rc, 41);
                int diff = loc.x - lowest_x;
                int new_x = lowest_x;
                if (rad >= diff) {
                    TerrainTile tile;
                    for (int i = rad - diff; i > 0; i--) {
                        if (rc.senseTerrainTile(loc.add(Direction.WEST, i)) == TerrainTile.OFF_MAP) {
                            x_finalized = true;
                        } else {
                            new_x = lowest_x - i;
                            break;
                        }
                    }
                }

                int change = lowest_x - new_x;
                if (change > 0) {
                    if (x_finalized) {
                        Broadcast.bc(rc, 44, new_x);
                        int new_high = highest_x + change;
                        Broadcast.bc(rc, 45, new_high);
                        Broadcast.bc(rc, 50, new_high - new_x);
                    } else {
                        Broadcast.bc(rc, 40, new_x);
                        int new_high = highest_x + change;
                        Broadcast.bc(rc, 41, highest_x + change);
                        Broadcast.bc(rc, 48, new_high - new_x);
                    }
                }
            } else {
                x_finalized = true;
            }

            if (Broadcast.read(rc, 46) == 0) {
                int lowest_y = Broadcast.read(rc, 42);
                int highest_y = Broadcast.read(rc, 43);
                int diff = loc.y - lowest_y;
                int new_y = lowest_y;
                if (rad >= diff) {
                    TerrainTile tile;
                    for (int i = rad - diff; i > 0; i--) {
                        if (rc.senseTerrainTile(loc.add(Direction.NORTH, i)) == TerrainTile.OFF_MAP) {
                            y_finalized = true;
                        } else {
                            new_y = lowest_y - i;
                            break;
                        }
                    }
                }

                int change = lowest_y - new_y;
                if (change > 0) {
                    if (y_finalized) {
                        Broadcast.bc(rc, 46, new_y);
                        int new_high = highest_y + change;
                        Broadcast.bc(rc, 47, new_high);
                        System.out.println(new_high - new_y);
                        Broadcast.bc(rc, 51, new_high - new_y);
                    } else {
                        Broadcast.bc(rc, 42, new_y);
                        int new_high = highest_y + change;
                        Broadcast.bc(rc, 43, highest_y + change);
                        Broadcast.bc(rc, 49, new_high - new_y);
                    }
                }
            } else {
                y_finalized = true;
            }

            if (x_finalized && y_finalized) {
                Broadcast.bc(rc, 52, 1);
            }
        }
    }

    //returns the number of [bashers, commander, drone, missile, soldier, tank] that can attack the direction
    public static int[] countNumEnemiesAttackingLoc(MapLocation loc) {
        RobotInfo[] enemies = rc.senseNearbyRobots(loc, 6, them);
        int[] attackers = new int[6];
        for (int i = enemies.length; i-- > 0; ) {
            RobotInfo enemy = enemies[i];
            switch (enemy.type) {
                case BASHER:
                    if (loc.distanceSquaredTo(enemy.location) <= 2) {
                        attackers[0]++;
                    }
                    break;
                case COMMANDER:
                    if (loc.distanceSquaredTo(enemy.location) <= 10) {
                        attackers[1]++;
                    }
                    break;
                case DRONE:
                    if (loc.distanceSquaredTo(enemy.location) <= 10) {
                        attackers[2]++;
                    }
                    break;
                case MISSILE:
                    if (loc.distanceSquaredTo(enemy.location) <= 2) {
                        attackers[3]++;
                    }
                    break;
                case SOLDIER:
                    if (loc.distanceSquaredTo(enemy.location) <= 5) {
                        attackers[4]++;
                    }
                    break;
                case TANK:
                    if (loc.distanceSquaredTo(enemy.location) <= 15) {
                        attackers[5]++;
                    }
                    break;
                default:
                    break;
            }
        }
        return attackers;
    }
}