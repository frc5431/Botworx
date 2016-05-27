package org.usfirst.frc.team5431.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwitchCase {
	private static double[] encodersDistance = { 0, 0 };
	// private double[] encodersRPM = {0};
	private static final double wheelCircum = driveBase.wheelDiameter * Math.PI;
	private static double driveForwardDistance = 0;
	static Vision cameraVision = new Vision();
	private static boolean pass = false;
	public final static int abortAutoAim = -42; // Get the joke, anyone?
	private static long autoshootTimer = 0;
	private static long autoAimTurnTimer = 0;
	private static long autoAimIntakeTimer = 0;
	private static long autoAimManualTimer = 0;
	private static long chopperDownTimer = 0;
	private static long shootTimer = 0;
	private static float initRoll = 0.0f;
	private static float initPitch = 0.0f;
	private static float changeRoll = 0.0f;
	private static float changePitch = 0.0f;
	private static float changePitchMax = 0.0f;
	private static float changeRollMax = 0.0f;
	private static long forwardGyro_neededTime = 0;
	private static long forwardGyro_landTime = 0;
	private static long forwardGyro_landTurn = 0;
	private static long FlyWheelTimer = 0;
	public static int currAIM = 1;
	private static double[] driveDistance = { 0, 0 };
	
	// private static int autoAimRemoteState = 0; //Used for the shoot()
	// function within autoAim()
	private static final int[] off = { 0, 0 };
	private static boolean inAuto = false;
	public static final double moveAmount = 0.455;//.42 for comp
	public static int checkAmount = 3;
	private static int timesCount = 0;
	public static boolean shotTheBall = false;
	private static final double percentRange = 0.02;
	
	
	
	private static final int minVal = -80, maxVal = 80;
	public SwitchCase() {

	}

	/**
	 * Function that uses switch-case autonomous to allow the robot to drive
	 * forward at full speed until the driveForwardDistance specified in
	 * Autonomous.java. <b>Should only be called within updateStates()</b> Won't
	 * work otherwise.
	 * 
	 * @param state
	 * @return state
	 */
	public static int driveForward(int state, int distance) {
		switch (state) {
		default:
			break;
		case 0:
			break;
		case 1:
			driveForwardDistance = wheelCircum * distance;
			Robot.drivebase.drive(.75, .75);
			state = 2;
			break;
		case 2:
			encodersDistance = Robot.drivebase.getEncDistance();
			if (encodersDistance[0] > driveForwardDistance || encodersDistance[1] > driveForwardDistance) {
				Robot.drivebase.drive(0.0, 0.0);
				state = 3;
			}
			break;
		case 3:
			state = 0;
			break;
		}
		return state;
	}
	
	public static int driveForwardGyro(int state, double distancetocrosswork){
		switch(state){
		default:
			break;
		case 0:
			break;
		case 1:
			driveForwardDistance = wheelCircum * distancetocrosswork;
			//Robot.gyro.reset();
			Robot.drivebase.drive(-.75, -.75);
			state = 2;
		case 2:
			encodersDistance = Robot.drivebase.getEncDistance();
			if (encodersDistance[0] > driveForwardDistance || encodersDistance[1] > driveForwardDistance) {
				Robot.drivebase.drive(0.0, 0.0);
				state = 3;
			}
			else{
				/*if(Robot.gyro.getAngle() > 1){
					Robot.drivebase.drive(-.76, -.75);
				}
				else if(Robot.gyro.getAngle() < -1){
					Robot.drivebase.drive(-.75, -.76);
				}
				else{
					Robot.drivebase.drive(-.75, -.75);
				}*/
			}
		case 3:
			state = 0;
		}
		return state;
	}

	/**
	 * Function that uses switch-case autonomous to allow the robot to
	 * constantly look for a target to do auto-aim then fires.. <b>Should only
	 * be called within updateStates().</b> Won't work otherwise.
	 * 
	 * @param state
	 * @return state
	 */
	public static int autoAim(int state, int shootSpeed) {
		//SmartDashboard.putNumber("STATE STATE STATE", state);
		SmarterDashboard.putBoolean("AUTO", state > 0);

		switch (state) {
		default:
			break;
		case 0:
			cameraVision.Update();
			inAuto = false;
			//timesCount = 0;
			break;
		case 1:
			////SmartDashboard.putNumber("STATE STATE STATE", state);
			////SmartDashboard.putNumber("COUNTINGDUDE", timesCount);
			////SmartDashboard.putBoolean("PASSED?", false);
			//timesCount += 1;
			// If David's autoAim code says to shoot
			cameraVision.Update();
			pass = true;
			/*
			if (Vision.distance > 80 && Vision.distance < 94) {
				pass = true;
			} else if (Vision.distance < 94) {
				Robot.drivebase.drive(0.7, 0.7);
				state = 1;
				pass = false;
			} else if (Vision.distance > 80) {
				Robot.drivebase.drive(-0.7, -0.7);
				state = 1;
				pass = false;
			} else {
				//SmartDashboard.putNumber("CRAPSTUFF", Vision.manVals[1]);
			} *//*
				 * else if (Vision.manVals[0] == 5) {// || Vision.manVals[1] ==
				 * 5){ } SmarterDashboard.putString("ERROR",
				 * "It's too close and too far"); Robot.drivebase.drive(0, 0);
				 * state = abortAutoAim;
				 * 
				 * }
				 */

			// You get it now, right?
			if (pass) {
				////SmartDashboard.putBoolean("PASSED?", true);

				Robot.drivebase.enableBrakeMode();
				if (Vision.manVals[0] == 0) {
					state = 4;
					/*
					 * if (timesCount > checkAmount) { state = 4;// Change when
					 * you want f/backward } else { state = 1; timesCount += 1;
					 * Timer.delay(0.1); }
					 */
				} else if (Vision.manVals[0] == 1) {
					Robot.drivebase.drive(-moveAmount, moveAmount);
					state = 1;
				} else if (Vision.manVals[0] == 2) {
					Robot.drivebase.drive(moveAmount, -moveAmount);
					state = 1;
				} else if (Vision.manVals[0] == 5) {// || Vision.manVals[1] ==
													// 5){
					SmarterDashboard.putString("ERROR", "It's too close and too far");
					Robot.drivebase.drive(0, 0);
					state = abortAutoAim;

				} else {
					state = abortAutoAim; // manVals[0] should only be 0-2.
											// Nothing else. Somethign is wrong.
				}
			}
			break;
		/*
		 * case 3: cameraVision.Update(); //SmartDashboard.putNumber(
		 * "STATE STATE STATE", state); if(Vision.manVals[1] == 0){
		 * if(Vision.manVals[0] != 0)//Make sure turn left + right is alright
		 * state = 2; else{ state = 4; //autoAimRemoteState = 1;
		 * //SmartDashboard.putNumber("remoteBugIn", autoAimRemoteState); } } else
		 * if(Vision.manVals[1] == 1){ Robot.drivebase.drive(-0.55, -0.55);
		 * state = 3; } else if(Vision.manVals[1] == 2){
		 * Robot.drivebase.drive(.55, .55); state = 3; } else state =
		 * abortAutoAim; break;
		 */
		case 4:
			//Robot.drivebase.disableBrakeMode();
			////SmartDashboard.putNumber("STATE STATE STATE", state);
			// //SmartDashboard.putNumber("remoteBug", autoAimRemoteState);
			// autoAimRemoteState =
			// if(autoAimRemoteState == 4)
			// state = 0;
			inAuto = true;
			state = 5;
			break;
		case 5:
			autoshootTimer = System.currentTimeMillis() + 3000;
			state = 6;
			// have to make this better, made into 4 states:
			// state 5 is to spin the flywheels
			// state 6 checks if the rpm is correct, and if so, go to state 7
			// state 7 intakes it
			// state 8 checks if a second passes, and if so, go to state 0
		case 6:
			//this used to do something, but we removed it, and i dont want to change all the numbers again
			state = 7;
			break;
		case 7:

			// state = 5;
			// shootStates = shoot(shootStates, 0.8);
			// if(shootStates == 0) {state = -1;}
			final double[] currentRPM = Robot.flywheels.getRPM();
			final int[] speeds3 = { shootSpeed, shootSpeed };
			final int marginOfError = (int) (speeds3[0] * percentRange);
			Robot.flywheels.setPIDSpeed(speeds3);
			if (System.currentTimeMillis() >= autoshootTimer && ((currentRPM[0] <= speeds3[0] + marginOfError
					&& currentRPM[0] >= speeds3[0] - marginOfError)
					|| (currentRPM[1] <= speeds3[1] + marginOfError && currentRPM[1] >= speeds3[1] - marginOfError))) {
				state = 8;
			}
			// Teleop.currentShootState=1;

			break;
		case 8:
			autoAimIntakeTimer = System.currentTimeMillis() + 1000;
			state = 9;
			break;
		case 9:
			Robot.flywheels.setIntakeSpeed(1.0);
			//SmartDashboard.putNumber("Auto Aim Timer", autoAimIntakeTimer);
			if (System.currentTimeMillis() >= autoAimIntakeTimer) {
				state = 10;
			}
			break;
		case 10:
			shotTheBall = true;
			Robot.flywheels.setPIDSpeed(off);
			state = -1;
			break;
			
		case 11:
			cameraVision.Update();
			//Robot.flywheels.setPIDSpeed(speedsPIDC);
			state = 12;
			break;
		case 12:
			cameraVision.Update();
			Robot.drivebase.autoAimController.reset();
			Robot.drivebase.autoAimController.enable();
			autoAimTurnTimer = System.currentTimeMillis() + 500;
			state = -12;
			break;
		case -12:
			if(System.currentTimeMillis() >= autoAimTurnTimer){
				state = 13;
			}
			break;
		case 13:
			cameraVision.Update();
			//SmartDashboard.putNumber("fromCenter", Vision.fromCenter);
			//autoAimTurnTimer = System.currentTimeMillis() + 2500;
			
			if(Robot.drivebase.autoAimController.getError() > -Robot.drivebase.aim_kTolerancePixels && Robot.drivebase.autoAimController.getError() < Robot.drivebase.aim_kTolerancePixels) {
			//if((Vision.fromCenter) > 25 && (Vision.fromCenter) < 40) {
				//SmartDashboard.putBoolean("TARGETLOCK", true);
				Robot.drivebase.autoAimController.disable();
				driveBase.drive(0.0, 0.0);
				Robot.flywheels.setIntakeSpeed(1);
				shootTimer = System.currentTimeMillis() + 2500;
				state = 14;
			} else {
				//SmartDashboard.putBoolean("TARGETLOCK", false);
			}
			break;
		case 14:
			////SmartDashboard.putNumber("TIMEAMOUNT", System.currentTimeMillis());
			if(System.currentTimeMillis() >= shootTimer) {
				Robot.flywheels.setIntakeSpeed(0);
				Robot.flywheels.setPIDSpeed(off);
			} else {
				Robot.flywheels.setIntakeSpeed(1);
			}
			Robot.drivebase.drive(0.0, 0.0);
			break;
		case abortAutoAim:
			
			//SmartDashboard.putString("Bug", "Failed to AutoAim");
			state = 1;
			break;
		case -1:
			//Robot.drivebase.disableBrakeMode();
			//SmartDashboard.putNumber("STATE STATE STATE", -1);
			Robot.flywheels.setPIDSpeed(off);
			Robot.flywheels.setIntakeSpeed(0);
			Robot.drivebase.autoAimController.disable();
			state = 0;
			break;
		}
		return state;
	}

	public static int shoot(int state, double shootSpeed) {// shootSpeed is temp
															// since there is no
															// camera at the
															// time of coding
		double toSetSpeed = shootSpeed + SmarterDashboard.getNumber("OVERDRIVE", 0.0);
		cameraVision.Update();
		//SmartDashboard.putNumber("SysTime", System.currentTimeMillis());
		/*
		 * if(Vision.manVals[1] == 1) { SmarterDashboard.addDebugString(
		 * "You're too close"); } else if(Vision.manVals[1] == 2) {
		 * SmarterDashboard.addDebugString("You're too far"); } else {
		 * SmarterDashboard.addDebugString("You're good"); }
		 */
		switch (state) {
		default:
			break;
		case 0:
			break;
		case 1:
			SmarterDashboard.putBoolean("AUTO", true);
			//double[] speeds = { toSetSpeed, toSetSpeed };
			final int toShoot = (int) cameraVision.getRPMS();
			final int[] woav = {toShoot, toShoot};
			Robot.flywheels.setPIDSpeed(woav);
			autoshootTimer = System.currentTimeMillis() + 2500;
			//Robot.flywheels.leftFW.setVoltageRampRate(1);
			//Robot.flywheels.rightFW.setVoltageRampRate(1);

			state = 2;
		case 2:
			//SmartDashboard.putNumber("shootBug", System.currentTimeMillis());
			//SmartDashboard.putNumber("shootBug2", autoshootTimer);

			final double[] curRPM = Robot.flywheels.getRPM();
			final int speedsTwo = (int) cameraVision.getRPMS();
			final int[] speedToo = {speedsTwo, speedsTwo};
			/*
			//SmartDashboard.putString("LEFTRIGHT", String.valueOf(speedsTwo[2]) + ":" + String.valueOf(speedsTwo[3]));
			//SmartDashboard.putString("NEEDEDLEFTRIGHT",
					String.valueOf(speedsTwo[4]) + ":" + String.valueOf(speedsTwo[5]));
			double leftPower = Robot.flywheels.getLeftPower(); // SmarterDashboard.getNumber("OVERDRIVE",
																// 0.0);;
			double rightPower = Robot.flywheels.getRightPower(); // +
																	// SmarterDashboard.getNumber("OVERDRIVE",
																	// 0.0);;
			double newPower[] = { leftPower + (speedsTwo[0] / 3.5), rightPower + (speedsTwo[1] / 3.5) };
			Robot.flywheels.setFlywheelSpeed(newPower);*/
			Robot.flywheels.setPIDSpeed(speedToo);
			if (cameraVision.withIn(curRPM[0], speedsTwo + minVal, speedsTwo + maxVal) && cameraVision.withIn(curRPM[1], speedsTwo + minVal, speedsTwo + maxVal)) {
				/*
				 * if(passedTimes < 4) { Timer.delay(0.1); passedTimes += 1; }
				 * else {
				 */
				// if(System.currentTimeMillis() >= autoshootTimer){]
				/*final double newSpeed[] = { newPower[0] - 0.1, // + 0.009 +
																// (SmarterDashboard.getNumber("OVERDRIVE",
																// 0.0) /10),
						newPower[1] - 0.1 };// + 0.009 +
											// (SmarterDashboard.getNumber("OVERDRIVE",
											// 0.0) /10)};
				Robot.flywheels.setFlywheelSpeed(newSpeed);
				// Timer.delay(0.2);
				// //SmartDashboard.putNumber("autoAimIntakebug",
				// System.currentTimeMillis());*/
				Robot.flywheels.setIntakeSpeed(1);
				Robot.flywheels.setPIDSpeed(speedToo);
				if (inAuto) {
					for (int a = 0; a < 200; a++) {
						Robot.flywheels.setIntakeSpeed(1);
						Timer.delay(0.005);
					}
					shotTheBall = true;
					Robot.flywheels.setPIDSpeed(off);
				}
				autoAimIntakeTimer = System.currentTimeMillis() + 1750;
				//SmartDashboard.putNumber("autoAimIntakeBug2", autoAimIntakeTimer);
				state = 3;
				// }
			} else
				state = 2;
			break;
		case 3:
			if (System.currentTimeMillis() >= autoAimIntakeTimer) {
				SmarterDashboard.putBoolean("AUTO", false);
				Robot.flywheels.setIntakeSpeed(0);
				Robot.flywheels.setPIDSpeed(off);
//				Robot.flywheels.leftFW.setVoltageRampRate(10);
//				Robot.flywheels.rightFW.setVoltageRampRate(10);
				state = 4;
			} else
				state = 3;
			break;
		case 4: // This is to allow remoteStates to know when program is done
			state = 0;
			break;
		case -1:// You are aborting
			Robot.flywheels.setIntakeSpeed(0);
			Robot.flywheels.setPIDSpeed(off);
//			Robot.flywheels.leftFW.setVoltageRampRate(10);
//			Robot.flywheels.rightFW.setVoltageRampRate(10);
			state = 0;
		}
		return state;
	}

	public static int shootManual(int state) {
		switch (state) {
		default:
			break;
		case 0:
			break;
		case 1:
			autoAimManualTimer = System.currentTimeMillis() + 750;
			Robot.flywheels.setIntakeSpeed(1);
			state = 2;
			break;
		case 2:
			//SmartDashboard.putNumber("autoAimManualTimer", autoAimManualTimer);
			if (autoAimManualTimer >= System.currentTimeMillis()) {
				Robot.flywheels.setIntakeSpeed(0);
				state = 3;
			}
			break;
		case 3:// In case you want to know that it is done
			state = 0;
			break;
		case -1:
			Robot.flywheels.setIntakeSpeed(0);
			Robot.flywheels.setPIDSpeed(off);
			state = 0;
			break;
		}
		return state;
	}
	
	public static int autonomous(int state, double angle, int distance, int[] shootSpeed, boolean autoAim){
		
		//int[] shootSpeedMiddle = {3270, 3270};//Power for 1
		switch(state)
		{
		default:
			break;
		case 0:
			break;
		case 1:
			Robot.drivebase.resetDrive();
			
			Robot.drivebase.enablePIDCDrive(-1, 0.001);
			state = -1;
			break;
		case -1:
			driveDistance = Robot.drivebase.getEncDistance();
			if ((driveDistance[0] > (distance) || driveDistance[1] > (distance)))
			{
				Robot.drivebase.driveController.disable();
				driveBase.drive(0, 0);
				//Robot.drivebase.ahrs.getYaw();
				forwardGyro_landTurn = System.currentTimeMillis() + 2000;
				state = -2;
			}
			break;
		case -2:
			if(System.currentTimeMillis() >= forwardGyro_landTurn) {
				
				Robot.drivebase.resetDrive();
				Robot.drivebase.enablePIDCDrive(-0.68, 0.2);
				state = -4;
			}
			break;
		case -4:
			driveDistance = Robot.drivebase.getEncDistance();
			if((driveDistance[0] > (22) || driveDistance[1] > (22)))
			{
				Robot.drivebase.driveController.disable();
				forwardGyro_landTime = System.currentTimeMillis() + 2000;
				Robot.drivebase.drive(0.0, 0.0);
				state = -3;//2 for no autoAim, 6 for autoAim
			} 
			
			break;
		case -3:
			if(System.currentTimeMillis() >= forwardGyro_landTime)
			{
				Robot.drivebase.enablePIDCTurn(angle);
				Robot.flywheels.setPIDSpeed(shootSpeed);
				currAIM = SwitchCase.autoAim(11, 3310);
				FlyWheelTimer = System.currentTimeMillis() + 3000;
				state = 6;
			}
			break;
		case 2:
			Robot.flywheels.setPIDSpeed(shootSpeed);
			FlyWheelTimer = System.currentTimeMillis() + 2500;
			////SmartDashboard.putNumber("VisionManVals", Vision.manVals[0]);
			////SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(11, 3310);
			//FlyWheelTimer = System.currentTimeMillis() + 3000;
			//Robot.flywheels.setFlywheelSpeed(shootSpeed);
			if(autoAim)
				state = 3;
			else
				state = 12;
			break;
			
		case 3:
			//SmartDashboard.putBoolean("INQUE", true);
			//SmartDashboard.putString("CALLEDMAN", "NO");
			////SmartDashboard.putNumber("VisionManVals", Vision.manVals[0]);
			////SmartDashboard.putString("READY READY READY", "Auto aiming");
			//Timer.delay(2);
			if(System.currentTimeMillis() >= FlyWheelTimer) {
				Robot.drivebase.disablePIDC();
				//SmartDashboard.putString("CALLEDMAN", "YES");
				currAIM = SwitchCase.autoAim(currAIM, 3310);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 13;
					//SmartDashboard.putString("CALLEDMAN", "RESET");
				}
				
			}
			
			break;
		case 12:
			if(System.currentTimeMillis() >= FlyWheelTimer){
				Robot.drivebase.disablePIDC();
				double[] currentRPM = Robot.flywheels.getRPM();
				if ((currentRPM[0] <= shootSpeed[0] * 1.02 && currentRPM[0] >= shootSpeed[0] * .98)
						|| (currentRPM[1] <= shootSpeed[1] * 1.02 && currentRPM[1] >= shootSpeed[1] * .98)) {
					forwardGyro_neededTime = System.currentTimeMillis() + 750;
					Robot.flywheels.setIntakeSpeed(1.0);
					state = 4;
				}
			}
			break;
		case 4:
			if (System.currentTimeMillis() >= forwardGyro_neededTime) {
				Robot.flywheels.setIntakeSpeed(0.0);
				Robot.flywheels.setPIDSpeed(off);
				Robot.drivebase.disablePIDC();
				state = 5;
			} 
			break;
		case 5://Dead state
			
			break;
		case 6://autoAim case
			if(System.currentTimeMillis() >= FlyWheelTimer) {
				Robot.drivebase.disablePIDC();
				//SmartDashboard.putString("CALLEDMAN", "YES");
				currAIM = SwitchCase.autoAim(currAIM, 3310);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 13;
					//SmartDashboard.putString("CALLEDMAN", "RESET");
				}
			}
			break;
		case 7:
			Robot.drivebase.disablePIDC();
			//SmartDashboard.putString("CALLEDMAN", "YES");
			currAIM = SwitchCase.autoAim(currAIM, 3260);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 13;
				//SmartDashboard.putString("CALLEDMAN", "RESET");
			}
			break;
		}
		return state;
	}
	
	public static int autonomousLowbar(int state, double angle, int distance, int[] shootSpeed, boolean autoAim){
		//int[] shootSpeedLowbar = {3310, 3310};
		switch(state)
		{
		default:
			break;
		case 0:
			break;
		case 1:
			Robot.drivebase.resetDrive();
			//SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
			//SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
			//SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
			Robot.drivebase.enablePIDCDrive(-0.68, 0.1);
			state = -1;
			break;
		case -1:
			driveDistance = Robot.drivebase.getEncDistance();
			if ((driveDistance[0] > (distance) || driveDistance[1] > (distance)))
			{
				Robot.drivebase.enablePIDCTurn(angle);
				Robot.flywheels.setPIDSpeed(shootSpeed);
				//FlyWheelTimer = System.currentTimeMillis() + 100;
				FlyWheelTimer = System.currentTimeMillis() + 3000;
				if(autoAim)
					state = -2;
				else
					state = 3;
			}
			//SmartDashboard.putBoolean("INQUE", false);
			break;
		case -2:
			//Start autoAim. Nothing else.
			currAIM = SwitchCase.autoAim(11, 2950);
			state = 2;
			break;
		case 2:
			//SmartDashboard.putBoolean("INQUE", true);
			//SmartDashboard.putString("CALLEDMAN", "NO");
			////SmartDashboard.putNumber("VisionManVals", Vision.manVals[0]);
			////SmartDashboard.putString("READY READY READY", "Auto aiming");
			//Timer.delay(2);
			if(System.currentTimeMillis() >= FlyWheelTimer) {
				Robot.drivebase.disablePIDC();
				//SmartDashboard.putString("CALLEDMAN", "YES");
				int flySpeed = cameraVision.getSpeed();
				final int[] speedsPIDC = { 2950, 2950 };
				Robot.flywheels.setPIDSpeed(speedsPIDC);
				currAIM = SwitchCase.autoAim(currAIM, 2950);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 13;
					//SmartDashboard.putString("CALLEDMAN", "RESET");
				}
				
			}
			
			break;
		case 3:
			if(System.currentTimeMillis() >= FlyWheelTimer){
				Robot.drivebase.disablePIDC();
				double[] currentRPM = Robot.flywheels.getRPM();
				if ((currentRPM[0] <= shootSpeed[0] * 1.02 && currentRPM[0] >= shootSpeed[0] * .98)
						|| (currentRPM[1] <= shootSpeed[1] * 1.02 && currentRPM[1] >= shootSpeed[1] * .98)) {
					forwardGyro_neededTime = System.currentTimeMillis() + 750;
					Robot.flywheels.setIntakeSpeed(1.0);
					state = 4;
				}
			}
			break;
		case 4:
			if (System.currentTimeMillis() >= forwardGyro_neededTime) {
				Robot.flywheels.setIntakeSpeed(0.0);
				Robot.flywheels.setPIDSpeed(off);
				Robot.drivebase.disablePIDC();
				state = 5;
			} 
			break;
		case 5://Dead state
			//SmartDashboard.putBoolean("isMoving", Robot.drivebase.ahrs.isMoving());
			//SmartDashboard.putBoolean("isRotating", Robot.drivebase.ahrs.isRotating());
			//SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
			//SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
			//SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
			driveBase.drive(0.0, 0.0);
			break;
		}
		return state;
	}
	
	public static int autonomousPorticullis(int state, double angle, int distance, int[] shootSpeed, boolean autoAim){
		//int[] shootSpeedLowbar = {3310, 3310};
		switch(state)
		{
		default:
			break;
		case 0:
			break;
		case 1:
			Robot.drivebase.chopperDown();
			chopperDownTimer = System.currentTimeMillis() + 2000;
			state = -16;
			break;
		case -16:
			if(System.currentTimeMillis() >= chopperDownTimer)
				state = -17;
			break;
		case -17:
			Robot.drivebase.resetDrive();
			//SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
			//SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
			//SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
			Robot.drivebase.enablePIDCDrive(-0.68, 0.1);
			state = -1;
			break;
		case -1:
			driveDistance = Robot.drivebase.getEncDistance();
			if ((driveDistance[0] > (distance) || driveDistance[1] > (distance)))
			{
				Robot.drivebase.enablePIDCTurn(angle);
				Robot.flywheels.setPIDSpeed(shootSpeed);
				
				FlyWheelTimer = System.currentTimeMillis() + 3000;
				if(autoAim)
					state = -2;
				else
					state = 3;
			}
			//SmartDashboard.putBoolean("INQUE", false);
			break;
		case -2:
			//Start autoAim. Nothing else.
			currAIM = SwitchCase.autoAim(11, 3310);
			state = 2;
			break;
		case 2:
			//SmartDashboard.putBoolean("INQUE", true);
			//SmartDashboard.putString("CALLEDMAN", "NO");
			////SmartDashboard.putNumber("VisionManVals", Vision.manVals[0]);
			////SmartDashboard.putString("READY READY READY", "Auto aiming");
			//Timer.delay(2);
			if(System.currentTimeMillis() >= FlyWheelTimer) {
				Robot.drivebase.disablePIDC();
				int flySpeed = cameraVision.getSpeed();
				final int[] speedsPIDC = { flySpeed, flySpeed };
				Robot.flywheels.setPIDSpeed(speedsPIDC);
				//SmartDashboard.putString("CALLEDMAN", "YES");
				currAIM = SwitchCase.autoAim(currAIM, 3310);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 13;
					//SmartDashboard.putString("CALLEDMAN", "RESET");
				}
				
			}
			break;
		case 3:
			if(System.currentTimeMillis() >= FlyWheelTimer){
				Robot.drivebase.disablePIDC();
				double[] currentRPM = Robot.flywheels.getRPM();
				if ((currentRPM[0] <= shootSpeed[0] * 1.02 && currentRPM[0] >= shootSpeed[0] * .98)
						|| (currentRPM[1] <= shootSpeed[1] * 1.02 && currentRPM[1] >= shootSpeed[1] * .98)) {
					forwardGyro_neededTime = System.currentTimeMillis() + 750;
					Robot.flywheels.setIntakeSpeed(1.0);
					state = 4;
				}
			}
			break;
		case 4:
			if (System.currentTimeMillis() >= forwardGyro_neededTime) {
				Robot.flywheels.setIntakeSpeed(0.0);
				Robot.flywheels.setPIDSpeed(off);
				Robot.drivebase.disablePIDC();
				state = 5;
			} 
			break;
		case 5://Dead state
			//SmartDashboard.putBoolean("isMoving", Robot.drivebase.ahrs.isMoving());
			//SmartDashboard.putBoolean("isRotating", Robot.drivebase.ahrs.isRotating());
			//SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
			//SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
			//SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
			driveBase.drive(0.0, 0.0);
			break;
		}
		return state;
	}
	
	public static int autonomousCheval(int state, double angle, int distance, int[] shootSpeed, boolean autoAim){
		//int[] shootSpeedLowbar = {3310, 3310};
		switch(state)
		{
		default:
			break;
		case 0:
			break;
		case 1:
			Robot.drivebase.resetDrive();
			
			Robot.drivebase.enablePIDCDrive(-0.57, 0.1);
			state = -13;
			break;
		case -13:
			driveDistance = Robot.drivebase.getEncDistance();
			if(driveDistance[0] >= 6 || driveDistance[1] >= 6){
				initRoll = Robot.drivebase.ahrs.getRoll();
				initPitch = Robot.drivebase.ahrs.getPitch();
				state = -14;
			}
			break;
		case -14:
			changePitch = Math.abs(Robot.drivebase.ahrs.getPitch() - initPitch);
			changeRoll = Math.abs(Robot.drivebase.ahrs.getRoll() - initRoll);
			if(changePitch > changePitchMax)
				changePitchMax = changePitch;
			if(changeRollMax > changeRollMax)
				changeRollMax = changeRoll;
			//SmartDashboard.putNumber("changePitch", changePitch);
			//SmartDashboard.putNumber("changeRoll", changeRoll);
			SmartDashboard.putNumber("changePitchMax", changePitchMax);
			SmartDashboard.putNumber("changeRollMax", changeRollMax);
			if(Math.abs(changePitchMax) > 2.5 || Math.abs(changeRollMax) > 2.5){
				Robot.drivebase.driveController.disable();
				Robot.drivebase.drive(0.0, 0.0);
				state = -15;
			}
			break;
		case -15:
			Robot.drivebase.chopperDown();
			chopperDownTimer = System.currentTimeMillis() + 2500;
			state = -16;
			break;
		case -16:
			if(System.currentTimeMillis() >= chopperDownTimer)
				state = -17;
			break;
		case -17:
			Robot.drivebase.resetDrive();
			//SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
			//SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
			//SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
			Robot.drivebase.enablePIDCDrive(-0.7, 0.1);
			state = -1;
			break;
		case -1:
			driveDistance = Robot.drivebase.getEncDistance();
			if ((driveDistance[0] > (distance) || driveDistance[1] > (distance)))
			{
				Robot.drivebase.enablePIDCTurn(angle);
				Robot.flywheels.setPIDSpeed(shootSpeed);
				Robot.drivebase.chopperUp();
				FlyWheelTimer = System.currentTimeMillis() + 2500;
				if(autoAim)
					state = -2;
				else
					state = 3;
			}
			//SmartDashboard.putBoolean("INQUE", false);
			break;
		case -2:
			//Start autoAim. Nothing else.
			currAIM = SwitchCase.autoAim(11, 3310);
			state = 2;
			break;
		case 2:
			//SmartDashboard.putBoolean("INQUE", true);
			//SmartDashboard.putString("CALLEDMAN", "NO");
			////SmartDashboard.putNumber("VisionManVals", Vision.manVals[0]);
			////SmartDashboard.putString("READY READY READY", "Auto aiming");
			//Timer.delay(2);
			if(System.currentTimeMillis() >= FlyWheelTimer) {
				Robot.drivebase.disablePIDC();
				int flySpeed = cameraVision.getSpeed();
				final int[] speedsPIDC = { flySpeed, flySpeed };
				Robot.flywheels.setPIDSpeed(speedsPIDC);
				//SmartDashboard.putString("CALLEDMAN", "YES");
				currAIM = SwitchCase.autoAim(currAIM, 3310);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 13;
					//SmartDashboard.putString("CALLEDMAN", "RESET");
				}
				
			}
			break;
		case 3:
			if(System.currentTimeMillis() >= FlyWheelTimer){
				Robot.drivebase.disablePIDC();
				double[] currentRPM = Robot.flywheels.getRPM();
				if ((currentRPM[0] <= shootSpeed[0] * 1.02 && currentRPM[0] >= shootSpeed[0] * .98)
						|| (currentRPM[1] <= shootSpeed[1] * 1.02 && currentRPM[1] >= shootSpeed[1] * .98)) {
					forwardGyro_neededTime = System.currentTimeMillis() + 750;
					Robot.flywheels.setIntakeSpeed(1.0);
					state = 4;
				}
			}
			break;
		case 4:
			if (System.currentTimeMillis() >= forwardGyro_neededTime) {
				Robot.flywheels.setIntakeSpeed(0.0);
				Robot.flywheels.setPIDSpeed(off);
				Robot.drivebase.disablePIDC();
				state = 5;
			} 
			break;
		case 5://Dead state
			//SmartDashboard.putBoolean("isMoving", Robot.drivebase.ahrs.isMoving());
			//SmartDashboard.putBoolean("isRotating", Robot.drivebase.ahrs.isRotating());
			//SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
			//SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
			//SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
			driveBase.drive(0.0, 0.0);
			break;
		}
		return state;
	}
}