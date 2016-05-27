package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.robot.Robot.AutoTask;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class for Autonomous commands. Uses switch-cases in order to acheive
 * multi-threading without creating multiple threads. Also much faster compared
 * to multi-threading and takes less space.
 * 
 * @author Usaid Malik
 */
public class Autonomous {
	private double gyroTurnAngle = 0;
	public static int driveForwardState = 0;
	private int driveForwardGyroState = 0;
	private int touchForwardState = 0;
	private int moatForwardState = 0;
	
	public static boolean autoAIMState = false;
	public static boolean seeOnlyTowerState = false;
	private static long forwardGyro_neededTime = 0;
	private static long forwardGyro_landTime = 0;
	private static long forwardGyro_landTurn = 0;
	private static long FlyWheelTimer = 0;
	public static int currAIM = 1;
	private long crossMoatTimer = 0;
	private static double[] driveDistance = { 0, 0 };
	private static double[] off = { 0.0, 0.0 };

	private final double[] speedToOuterWork = { 0.65, 0.65 }, speedToCrossMoat = { 1, 1 };
	
	
	public static int stationNumber = 1;

	private static final double distanceToOuterWork = 48, distanceToCrossWork = 135, // 128
			distanceToCrossRough = 130, distanceToSeeOnlyTower = 12, forwardGyro_barelyCross = 122, forwardGyro_barelyRough = 132, forwardGyro_barelyRock = 150;// 122

	public static enum FirstMove {
		StandStill, TouchOuterWork, Lowbar, RockWall, RoughTerrain, Portcullis, ChevalDeFrise, Moat;
	}

	public static enum AutoShoot {
		False, True;
	}

	/**
	 * Initializes the Autonomous class.
	 */
	public Autonomous() {

	}

	private void curveFix(double speeds[]) {
		driveBase.drive(-speeds[0], -speeds[1]);
	}

	private void touchForward() {

		if ((driveDistance[0] < distanceToOuterWork || driveDistance[1] < distanceToOuterWork)
				&& driveForwardState == 0) {
			curveFix(speedToOuterWork);
		} else {
			Robot.drivebase.resetDrive();
			driveForwardState = 1;
		}

		// touchForwardState = SwitchCase.driveForward(touchForwardState, 72);
	}

	private void PorticShoot() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < (distanceToCrossWork + 24) || driveDistance[1] < (distanceToCrossWork + 24))
				&& driveForwardState == 0) {
			driveBase.drive(-0.7, -0.73);
			Robot.drivebase.chopperDown();
			SmartDashboard.putString("READY READY READY", "Auto driving");
		} else {
			driveBase.drive(0, 0);
			Robot.drivebase.resetDrive();
			Robot.drivebase.chopperUp();
			Timer.delay(0.25);
			driveBase.drive(0,
					0);/*
						 * 
						 * 
						 * for(int time = 0; time < 15; time++) {
						 * Robot.drivebase.drive(-0.78, 0.78);
						 * Timer.delay(0.005); } for(int time2 = 0; time2 < 30;
						 * time2++) { Robot.drivebase.drive(-0.76, -0.76);
						 * Timer.delay(0.005); }
						 */
			driveForwardState = 1;
			if (!autoAIMState) {
				autoAIMState = true;
				Timer.delay(0.75);
				// SwitchCase.moveAmount = 0.43;
				SwitchCase.checkAmount = 1;
				SwitchCase.shotTheBall = false;
				currAIM = SwitchCase.autoAim(currAIM, 3350);
			}
			if (autoAIMState) {

				SmartDashboard.putString("READY READY READY", "Auto aiming");
				currAIM = SwitchCase.autoAim(currAIM, 3350);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 1;
				}
			}
		}

	}

	private void LowbarShoot() {
		if ((driveDistance[0] < distanceToCrossWork || driveDistance[1] < distanceToCrossWork)
				&& driveForwardState == 0) {
			// curveFix(speedToOuterWork);
			driveBase.drive(-0.80, -0.83);
		} else if (!autoAIMState && !seeOnlyTowerState) {
			driveForwardState = 1;
			Timer.delay(0.5);
			for (int time = 0; time < 30; time++) {
				driveBase.drive(0.78, -0.78);
				Timer.delay(0.005);
			}
			for (int time2 = 0; time2 < 35; time2++) {
				driveBase.drive(-0.76, -0.76);
				Timer.delay(0.005);
			}
			Robot.drivebase.resetDrive(); // Reset encoders for moving forward
			seeOnlyTowerState = true;
		} else if (!autoAIMState && seeOnlyTowerState) {
			if (driveDistance[0] < distanceToSeeOnlyTower && driveDistance[1] < distanceToSeeOnlyTower) {
				driveBase.drive(-.70, -.73);
			} else {
				autoAIMState = true;
				Timer.delay(0.15);
				// SwitchCase.moveAmount = 0.43;
				SwitchCase.checkAmount = 1;
				SwitchCase.shotTheBall = false;
				currAIM = SwitchCase.autoAim(currAIM, 3350);
			}
		}
		if (autoAIMState) {
			SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(currAIM, 3350);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 1;
			}
		}

	}

	private void crossForward() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < distanceToCrossWork && driveDistance[1] < distanceToCrossWork)
				&& driveForwardState == 0) {
			// curveFix(speedToOuterWork);
			driveBase.drive(-0.70, -0.73);
		} else {
			driveBase.drive(0, 0);
			Robot.drivebase.resetDrive();
			driveForwardState = 1;
		}

	}

	private void crossRockWall() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < distanceToCrossWork - 5 && driveDistance[1] < distanceToCrossWork - 5)
				&& driveForwardState == 0) {
			driveBase.drive(-1, -1);
		} else {
			driveBase.drive(0, 0);
			Robot.drivebase.resetDrive();
			driveForwardState = 1;
		}
	}


	private void shootRockWall() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < (distanceToCrossWork - 24) || driveDistance[1] < (distanceToCrossWork - 24))
				&& driveForwardState == 0) {
			driveBase.drive(-1, -1);
			SmartDashboard.putString("READY READY READY", "Auto driving");
		} else {
			driveBase.drive(0, 0);
			Robot.drivebase.resetDrive();
			Timer.delay(0.7);
			driveBase.drive(0, 0);

			driveForwardState = 1;
			if (!autoAIMState) {
				for (int time = 0; time < 10; time++) {
					driveBase.drive(-0.468, 0.468);
					Timer.delay(0.005);
				}
				Timer.delay(0.2);
				for (int time2 = 0; time2 < 70; time2++) {
					driveBase.drive(-0.468, -0.468);
					Timer.delay(0.005);
				}
				autoAIMState = true;
				// Timer.delay(0.2);
				SwitchCase.shotTheBall = false;
			}
		}

		if (autoAIMState) {
			SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(currAIM, 3350);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 1;
			}
		}

	}

	private int moatForward(int state) {
		switch (state) {
		case 0:
			break;
		default:
			break;
		case 1:
			curveFix(speedToCrossMoat);
			state = 2;
			break;
		case 2:
			if (!(driveDistance[0] < distanceToCrossWork || driveDistance[1] < distanceToCrossWork)
					&& driveForwardState == 0) {
				Robot.drivebase.resetDrive();
				driveForwardState = 1;
				state = 3;
			}
			break;
		case 3:
			crossMoatTimer = System.currentTimeMillis() + 3000;
			state = 4;
			break;
		case 4:
			if (System.currentTimeMillis() >= crossMoatTimer) {
				driveBase.drive(0, 0);
				state = 5;
			}
			break;
		case 5:
			state = 0;
			break;
		}
		/*
		 * if((driveDistance[0] < distanceToCrossWork || driveDistance[1] <
		 * distanceToCrossWork) && driveForwardState == 0) {
		 * curveFix(speedToCrossMoat); } else { Robot.drivebase.resetDrive();
		 * driveForwardState = 1; }
		 */
		return state;
	}

	/**
	 * Shoots from the spybox position.
	 */
	private void spyboxShoot() {
		for (int time = 0; time < 35; time++) {
			driveBase.drive(0.8, -0.8);
			Timer.delay(0.005);
		}
		for (int time2 = 0; time2 < 35; time2++) {
			driveBase.drive(-0.6, -0.6);
			Timer.delay(0.005);
		}
		if (!autoAIMState) {
			driveForwardState = 1;
			autoAIMState = true;
			Timer.delay(0.75);
			// SwitchCase.moveAmount = 0.43;
			SwitchCase.checkAmount = 1;
			SwitchCase.shotTheBall = false;
			currAIM = SwitchCase.autoAim(currAIM, 3000);
		}
		if (autoAIMState) {
			SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(currAIM, 3000);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 1;
			}
		}
	}

	private static void encoderUpdate() {
		driveDistance = Robot.drivebase.getEncDistance();
	}

	static double maximumError = 0, minimumError = 0, forwardGyro_previousAngle = 0;

	
	/**
	 * TODO Update this JavaDoc!
	 * <b>This Javadoc is outdated. Please fix!</b>
	 * Updates the state of various autonomous functions. This must be called in
	 * <b>autonomousPeriodic()</b>.
	 * <ul>
	 * Currently updates
	 * </ul>
	 * :
	 * <li>driveForward()</li>
	 * <li>autoAim()</li>
	 */
	public void updateStates(AutoTask currentAuto) {

		encoderUpdate();//Updating encoders - required, don't take out
		
		
		
		//SmartDashboard.putString("CURRENT SELECTED", currentAuto.name());
		//SmartDashboard.putNumber("NavexState", autonOtherLowbarState);
		touchForwardState = SwitchCase.driveForward(touchForwardState, 72);
		moatForwardState = moatForward(moatForwardState);
		driveForwardGyroState = SwitchCase.driveForwardGyro(driveForwardGyroState, distanceToCrossWork);
		
		// driveForwardState = SwitchCase.driveForward(driveForwardState);
		// autoAIMState = SwitchCase.autoAim(autoAIMState);

	}

}
