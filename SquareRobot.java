package kca;
import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * SquareRobot - a robot by (your name here)
 */
public class SquareRobot extends Robot
{
	int turnDirection = 1;
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		turnLeft(getHeading() % 90);
		ahead(Math.max(getBattleFieldWidth(), getBattleFieldHeight()));
		turnGunRight(90);
		turnRight(90);

		// Robot main loop
		while(true) {
			turnLeft(getHeading() % 90);
			ahead(Math.max(getBattleFieldWidth(), getBattleFieldHeight()));
			turnRight(90);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if(getNumSentries() > 5) {
			turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			if (getEnergy() > 20) {
				if (e.getDistance() < 250) {
					if (Math.abs(e.getVelocity()) < 3) {
						fire(3);
					} else {
						fire(2.5);
					}
				} else {
					fire(2.5);
				}
			} else {
				if (e.getDistance() < 50) {
					fire(2);
				}
			}
		}
		else {
			turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			if (getEnergy() > 20) {
				if (e.getDistance() < 250) {
					if (Math.abs(e.getVelocity()) < 3) {
						fire(3);
					} else {
						fire(1);
					}
				} else {
					fire(1);
				}
			} else {
				if (e.getDistance() < 50) {
					fire(1);
				}
			}
		}


	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		if(getNumSentries() > 5) {
			if(getEnergy() > 20) {
				ahead(100);
				turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
				fire(3);
			} else {
				ahead(200);
				turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
				fire(1.5);
			}
		} else {
			turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			fire(3);
			ahead(50);
		}
	}
}
