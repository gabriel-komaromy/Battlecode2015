package path_finder_tests_2;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.lang.Math.*;
import java.util.*;

public class Bot {
    static RobotController rc;
    static Team us;
    static Team them;
    static Random rand;
    static MapLocation ourHQ, theirHQ;
    static MapLocation[] ourTowers, theirTowers;

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
    }

    public static void spawnUnit(RobotType type, int max_units) throws GameActionException {
        if (Broadcast.read(rc, Channels.getChannel(type)) < max_units) {
            Direction dir = rc.getLocation().directionTo(theirHQ);
            boolean spawned = false;
            if (rc.isCoreReady() && rc.canSpawn(dir, type)) {
                rc.spawn(dir, type);
                spawned = true;
            }
            while (!spawned) {
                dir = getRandomDirection();
                if (rc.isCoreReady() && rc.canSpawn(dir, type)) {
                    rc.spawn(dir, type);
                    spawned = true;
                }
            }
            Broadcast.incChannel(rc, Channels.getChannel(type));
        }
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
                compareTo = 2.5;
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
                    Nav.goTo(best, Nav.Engage.NO);
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
        rc.yield();
    }

    public static boolean isInTheirStaticAttackRange(MapLocation loc) {
        int dist_sq = theirHQ.distanceSquaredTo(loc);
        if (dist_sq < 25 || (loc.x == theirHQ.x && loc.y == theirHQ.y)) {
            return true;
        }
        int dist_sq;
        for (MapLocation tower : theirTowers) {
            dist_sq = tower.distanceSquaredTo(loc);
            if (dist_sq < 25 || (loc.x == tower.x && loc.y == tower.y)) {
                return true;
            }
        }
        return false;
    }
}