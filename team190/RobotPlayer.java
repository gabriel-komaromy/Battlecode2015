package team190;

import battlecode.common.*;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import java.lang.Exception;
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
                switch (rc.getType()) {
                    case HQ:
                        attackEnemyZero();
                        spawnUnit(RobotType.BEAVER);
                        break;

                    case BEAVER:
                        attackEnemyZero();
                        if (Clock.getRoundNum() < 700) {
                            buildUnit(RobotType.MINERFACTORY);
                        } else {
                            buildUnit(RobotType.BARRACKS);
                        }
                        mineAndMove();
                        break;

                    case MINER:
                        attackEnemyZero();
                        mineAndMove();
                        break;

                    case MINERFACTORY:
                        spawnUnit(RobotType.MINER);
                        break;

                    case TOWER:
                        attackEnemyZero();
                        break;

                    case BARRACKS:
                        spawnUnit(RobotType.SOLDIER);
                        break;

                    case SOLDIER:
                        attackEnemyZero();
                        moveAround();
                        break;
                }

                transferSupplies();

            } catch (Exception e) {
                e.printStackTrace();
            }
            rc.yield();

        }

    }

    private static void moveAround() throws Exception {
        if (rand.nextDouble() <= 0.05) {
            if (rand.nextDouble() < 0.5) {
                facing = facing.rotateLeft();
            } else {
                facing = facing.rotateRight();
            }
        }

        MapLocation tileInFront = rc.getLocation().add(facing);

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        boolean tileInFrontSafe = true;
        for (MapLocation m: enemyTowers) {
            if (m.distanceSquaredTo(tileInFront) <= RobotType.TOWER.attackRadiusSquared) {
                tileInFrontSafe = false;
                break;
            }
        }

        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL || !tileInFrontSafe) {
            facing = facing.rotateLeft();
        } else {
            if (rc.isCoreReady() && rc.canMove(facing)) {
                rc.move(facing);
            }
        }

    }

    private static void mineAndMove() throws Exception {
        if (rc.senseOre(rc.getLocation()) > 1) {
            if (rc.isCoreReady() && rc.canMine()) {
                rc.mine();
            }
        } else {
            moveAround();
        }
    }

    private static Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    private static void spawnUnit(RobotType type) throws Exception {
        Direction randomDir = getRandomDirection();
        if (rc.isCoreReady() && rc.canSpawn(randomDir, type)) {
            rc.spawn(randomDir, type);
        }
    }

    private static void attackEnemyZero() throws Exception {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, them);
        if (nearbyEnemies.length > 0) {
            if (rc.isWeaponReady() && rc.canAttackLocation(nearbyEnemies[0].location)) {
                rc.attackLocation(nearbyEnemies[0].location);
            }
        }
    }

    private static void buildUnit(RobotType type) throws Exception {
        if (rc.getTeamOre() > type.oreCost) {
            Direction buildDir = getRandomDirection();
            if (rc.isCoreReady() && rc.canBuild(buildDir, type)) {
                rc.build(buildDir, type);
            }
        }
    }

    private static void transferSupplies() throws Exception {
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
}