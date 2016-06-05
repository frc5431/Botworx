package org.usfirst.frc.team5431.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team5431.robot.autonPIDOutput;

/**
 * This class declares variables/functions related specifically to the DriveBase
 * (and only the DriveBase).
 * 
 * @author Usaid Malik
 */
public class driveBase {
	public static final int wheelDiameter = 10; // In inches. Used to calculate
												// distancePerPulse
	private static final double distancePerPulse = wheelDiameter * Math.PI / 360;// Calculates
																					// distancePerPulse
																					// in
																					// inches
	private static final int samplesToAverage = 1; // How many pulses to do
													// before averaging them
													// (smoothes encoder count)
	private static final double minEncRate = 20.0; // Minimum Encoder Rate before
													// hardware thinks encoders
													// are stopped
	static final double kP = 0.05;
	static final double kI = 0.00002;
	static final double kD = 0.11;
	static final double kF = 0.04;
	static final double kToleranceDegrees = 0.5f;
	
	static final double turn_kP = 0.02;
	static final double turn_kI = 0.000015;
	static final double turn_kD = 0.015;
	static final double turn_kF = 0;
	static final double turn_kToleranceDegrees = 0.5f;
	
	static final double aim_kP = 0.0;//0.018;
	static final double aim_kI = 0.031;//00005
	static final double aim_kD = 0.061945;
	static final double aim_kF = 0.0001;
	final double aim_kTolerancePixels = 2;
	
	public static CANTalon frontright; // Declaration
	public static CANTalon frontleft;
	static enum pidFlag{driving, turning};
	static pidFlag flag;
	public static CANTalon rearright;
	public static CANTalon rearleft;
	Encoder rightBaseEncoder, leftBaseEncoder; // Declaration of encoders used
												// in the drivebase
	static RobotDrive tankDriveBase;
	static double notPIDSideSpeed = -0.4;
	
	public CANTalon chopper;
	public PIDController driveController;
	public PIDController autoAimController;
	private autonPIDOutput pidOutput;
	private autonPIDOutput autoAimOutput;
	private autoAimPIDInput autoAimInput;
	
	AHRS ahrs;
	/**
	 * Constructor for the driveBase class. This specifies whether the CANTalons
	 * are/aren't in brake mode.
	 * 
	 * @param mode
	 *            A brakeMode enum. Can be brakeMode.Brake or brakeMode.NoBrake
	 */
	public driveBase(boolean mode) { // Constructor used if brakeMode is
										// specified (should be but probably
										// won't be used){
		frontright = new CANTalon(RobotMap.frontright); // Instantiates
														// CANTalons based on
														// mapping in RobotMap
		frontleft = new CANTalon(RobotMap.frontleft);
		rearright = new CANTalon(RobotMap.rearright);
		rearleft = new CANTalon(RobotMap.rearleft); // If in BrakeMode, set
													// CANTalons to brakeMode,
													// otherwise, don't

		frontright.enableBrakeMode(mode);
		frontleft.enableBrakeMode(mode);
		rearright.enableBrakeMode(mode);
		rearleft.enableBrakeMode(mode);

		tankDriveBase = new RobotDrive(frontleft, rearleft, frontright, rearright);// Initializes
																					// RobotDrive
																					// to
																					// use
																					// tankDrive()
		rightBaseEncoder = new Encoder(RobotMap.rightBaseEnc1, RobotMap.rightBaseEnc2, false, EncodingType.k4X);// Using
																												// 4X
																												// encoding
																												// for
																												// encoders
		leftBaseEncoder = new Encoder(RobotMap.leftBaseEnc1, RobotMap.leftBaseEnc2, false, EncodingType.k4X);
		rightBaseEncoder.setDistancePerPulse(distancePerPulse); // Sets distance
																// robot would
																// travel every
																// encoder pulse
		leftBaseEncoder.setDistancePerPulse(distancePerPulse);
		rightBaseEncoder.setSamplesToAverage(samplesToAverage); // Averages
																// encoder count
																// rate every
																// samplesToAverage
																// pulses
		leftBaseEncoder.setSamplesToAverage(samplesToAverage);
		rightBaseEncoder.setReverseDirection(false); // Reverses encoder
														// direction based on
														// position on robot
		leftBaseEncoder.setReverseDirection(true);
		rightBaseEncoder.setMinRate(minEncRate); // Sets minimum rate for
													// encoder before hardware
													// thinks it is stopped
		leftBaseEncoder.setMinRate(minEncRate);

		chopper = new CANTalon(RobotMap.chopper);
		chopper.setSafetyEnabled(false);
		chopper.enableBrakeMode(true);
		
		ahrs = new AHRS(SPI.Port.kMXP);
		pidOutput = new autonPIDOutput();
		autoAimOutput = new autonPIDOutput();
		autoAimOutput.stall = 0.47;
		autoAimInput = new autoAimPIDInput();
		autoAimController = new PIDController(aim_kP, aim_kI, aim_kD, aim_kF, autoAimInput, pidOutput);
		autoAimController.setInputRange(-180.0f,  180.0f);
		autoAimController.setAbsoluteTolerance(aim_kTolerancePixels);
		autoAimController.setOutputRange(-.8f, .8f);
		autoAimController.setToleranceBuffer(12);
		
		driveController = new PIDController(kP, kI, kD, kF, ahrs, pidOutput, 0.005);
	    driveController.setInputRange(-180.0f,  180.0f);
	    //driveController.setOutputRange(0.2f, 0.7f);
	    driveController.setAbsoluteTolerance(kToleranceDegrees);
	    //driveController.setPercentTolerance(0.5);
	    driveController.setContinuous(true);
	}

	/**
	 * Drive function to control motors. Control driveBase motors through this
	 * function.
	 * 
	 * @param left
	 *            Power to right motor. From -1 to 1.
	 * @param right
	 *            Power to left motor. From -1 to 1.
	 */
	public static void drive(double left, double right) {
		tankDriveBase.tankDrive(left, right);
		SmarterDashboard.putNumber("LEFT-DRIVE", left);
		SmarterDashboard.putNumber("RIGHT-DRIVE", right);
		
	}

	/**
	 * Gets distance traveled by driveBase encoders since the last encoder
	 * reset.
	 * 
	 * @return double array where index 0 is left distance and 1 is right
	 *         distance.
	 */
	public double[] getEncDistance() {
		final double returnVals[] = { 0, 0 };
		returnVals[0] = -leftBaseEncoder.getDistance();// For some dumb reason,
														// the left never
														// inverses.
		returnVals[1] = rightBaseEncoder.getDistance();
		SmartDashboard.putNumber("LEFTENCODING", returnVals[0]);
		SmartDashboard.putNumber("RIGHTENCODING", returnVals[1]);
		return returnVals;
	}
	public void chopperUp() {
		chopper.set(1);
	}

	public void chopperDown() {
		chopper.set(-1);
	}

	public void resetDrive() {
		leftBaseEncoder.reset();
		rightBaseEncoder.reset();
	}
	
	public void setLeft(double speed) {
		frontleft.set(speed);
		rearleft.set(speed);
	}
	
	public void setRight(double speed) {
		frontright.set(speed);
		rearright.set(speed);
	}
	
	public void setRampRate(double rate){
		frontright.setVoltageRampRate(rate);
		rearright.setVoltageRampRate(rate);
		frontleft.setVoltageRampRate(rate);
		rearleft.setVoltageRampRate(rate);
	}

	public void enableBrakeMode() {
		frontright.enableBrakeMode(true);
		rearright.enableBrakeMode(true);
		frontleft.enableBrakeMode(true);
		rearleft.enableBrakeMode(true);
	}
	
	public void disableBrakeMode() {
		frontright.enableBrakeMode(false);
		rearright.enableBrakeMode(false);
		frontleft.enableBrakeMode(false);
		rearleft.enableBrakeMode(false);
	}
	
	public void enablePIDCDrive(double speed, double range)
	{
		SmartDashboard.putBoolean("isCalibrating", ahrs.isCalibrating());
		driveController.reset();
		driveController.setPID(kP, kI, kD, kF);
		driveController.setOutputRange(speed - range, speed + range);
		double angle = ahrs.getYaw();
		SmartDashboard.putNumber("AHRS angle", angle);
		driveController.setSetpoint(angle);
		drive(speed + 0.03, speed);
		driveController.enable();
		flag = pidFlag.driving;
		notPIDSideSpeed = speed;
	}
	public void enablePIDCTurn(double angle)
	{
		driveController.reset();
		driveController.setPID(turn_kP, turn_kI, turn_kD, turn_kF);
		driveController.setOutputRange(-0.85f, 0.85f);
		driveController.setSetpoint(angle);
		driveController.enable();
		flag = pidFlag.turning;
		
	}
	public void disablePIDC()
	{
		driveController.reset();
	}
}
