package kca;

import robocode.*;
import robocode.Robot;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class KitKat extends Robot {

    private int scanDirection = 1;
    private double firePower;
    HashMap<String, ScannedRobotEvent> enemyData = new HashMap<>();
    private int enemyEnergyCount = 0;
    private String trackName = null;
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> friendsInPlay = new ArrayList<>();

    public void run() {
        setBodyColor(new Color(234, 14, 128));
        setGunColor(new Color(89, 20, 56));
        setRadarColor(new Color(211, 173, 193));
        setScanColor(new Color(209, 192, 226));
        setBulletColor(new Color(131, 58, 209));

        setAdjustRadarForGunTurn(true);
        //setAdjustGunForRobotTurn(true);

        friends.add("Fedora");

        while(true) {
            out.printf("Num friends in play: %d\n", friendsInPlay.size());
            turnRadarRight(360 * scanDirection);
            //turnGunRight(10);
            turnRight(10);
            ahead(250/getEnergy());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(arrayListContainsSubstring(friends, e.getName())) {
            if(!arrayListContainsSubstring(friendsInPlay, e.getName())) {
                friendsInPlay.add(e.getName());
            }
        }
        if(getOthers() > friendsInPlay.size()) {
            if(arrayListContainsSubstring(friendsInPlay, e.getName())) {
                return;
            }
        }
        if(e.isSentryRobot()) {
            return;
        }
        if(enemyEnergyCount % 10 == 0) {
            enemyData.clear();
        }
        enemyData.put(e.getName(), e);

        double weakestAndClosest = Double.POSITIVE_INFINITY;
        for(ScannedRobotEvent scannedRobotEvent : enemyData.values()) {
            if(scannedRobotEvent.getDistance() < 250 &&
                            Math.abs(scannedRobotEvent.getBearing()) < 45 &&
                            Math.abs(scannedRobotEvent.getVelocity()) < 4) {
                double vulnerability =
                        scannedRobotEvent.getDistance()/400 * 4 +   // (0 to ~400) * 4
                                Math.abs(scannedRobotEvent.getBearing())/180 * 2 +  // (0 to 180) * 2
                                scannedRobotEvent.getEnergy()/100 + // (0 to 100)
                                Math.abs(scannedRobotEvent.getVelocity())/8;  // (0 to 8)

                if (vulnerability < weakestAndClosest) {
                    weakestAndClosest = vulnerability;
                    trackName = scannedRobotEvent.getName();
                    out.println("Tracking " + trackName);
                }
            }
        }

        if(trackName == null) {
            trackName = e.getName();
            out.println("Tracking " + trackName);
            //turnGunRight(10);
            turnRight(10);
            ahead(250/getEnergy());
        }


        if(e.getName().equals(trackName)) {
            //turnGunRight(e.getBearing());
            turnRight(e.getBearing());
            firePower = Math.abs(2500/(e.getDistance()*e.getVelocity()));
            out.println(firePower);
            fire(firePower);
            ahead(Math.min(e.getDistance() - 100, 150));
        }
        scanDirection *= -1;
    }

    public void onHitByBullet(HitByBulletEvent e) {
        if(enemyData.containsKey(e.getName())) {
            double attackingRobotBearing = enemyData.get(e.getName()).getBearing();
            if(attackingRobotBearing < 0) {
                //turnGunRight(attackingRobotBearing + 90);
                turnRight(attackingRobotBearing + 90);
            }
            else {
                //turnGunLeft(90 - attackingRobotBearing);
                turnLeft(90 - attackingRobotBearing);
            }
        }
        ahead(100);
    }

    public void onHitRobot(HitRobotEvent e) {
        back(80);
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if(e.getName().equals(trackName)) {
            trackName = null;
            enemyData.remove(e.getName());
        }
        if(arrayListContainsSubstring(friendsInPlay, e.getName())) {
            friendsInPlay.remove(e.getName());
        }
    }

    public void onHitWall(HitWallEvent e) {
        back(30);
        //turnGunRight(e.getBearing() + 90);
        turnRight(e.getBearing() + 90);
        ahead(30);
    }

    private boolean arrayListContainsSubstring(ArrayList<String> robotNames, String name) {
        for(String s : robotNames) {
            if(name.contains(s)) {
                return true;
            }
        }
        return false;
    }

//    private String getRobotName(String fullPackageName) {
//        for(String s : friendsInPlay) {
//            if(fullPackageName.contains(s)) {
//                return s;
//            }
//        }
//        return null;
//    }
}
