package org.usfirst.frc.team5431.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

/**
 * Class to control the flywheels and intake of the robot. Also does climber.
 * 
 * @author Usaid Malik
 */
public class Shooter {
	DigitalInput intakeLimit;
	private CANTalon rightFW, leftFW, intakeMotor, winchMotor;
	private Talon intake;
	static int rpmdelay = 0;
	static double rpmbooster = 0;
	public static final double[] power = { 0, 0 };

	/**
	 * Constructor for the Shooter() class. Assigns motors, encoders, and sets
	 * settings for them. Encoders are reset after all the motors/encoders are
	 * assigned. Do not reset encoders by lm,] ' g\calling this multiple times.
	 * If you do so, you are an idiot. Period. Not Marie's period though
	 */
	public Shooter() {
		intakeLimit = new DigitalInput(RobotMap.intakeLim);
		rightFW = new CANTalon(RobotMap.rightFlyWheel);
		leftFW = new CANTalon(RobotMap.leftFlyWheel);
		intakeMotor = new CANTalon(RobotMap.intake);
		intake = new Talon(RobotMap.mecanumIntake);
		intake.setInverted(true);
		leftFW.reverseOutput(true);
		// rightFW.setInverted(true);
		// leftFW.setInverted(false);
		leftFW.enableBrakeMode(false);
		rightFW.enableBrakeMode(false);
		leftFW.changeControlMode(TalonControlMode.Speed);
		rightFW.changeControlMode(TalonControlMode.Speed);
		leftFW.setPID(0.2, 0.00021, 0, 0, 0, 12, 0);
		rightFW.setPID(0.2, 0.00021, 0, 0, 0, 12, 0);
		//leftFW.setPID(0.3, 0.00021, 0.011, 0, 0, 12, 0);
		//rightFW.setPID(0.3, 0.00021, 0.011, 0, 0, 12, 0);
		rightFW.setFeedbackDevice(FeedbackDevice.EncRising);
		leftFW.setFeedbackDevice(FeedbackDevice.EncRising);
		leftFW.configEncoderCodesPerRev(1024);
		rightFW.configEncoderCodesPerRev(1024);
	}

	/**
	 * Gets the RPM of the flywheels.
	 * 
	 * @return double array where index 0 is the RPM of the right, and 1 is the
	 *         RPM of the left.
	 */
	public double[] getRPM() {
		double returnVals[] = { 0, 0 };
		// the 600 is multiplied for 2 reasons:
		// 1 - getEncVelocity() returns it as RPS (rotations per second), so you
		// multiply it by 60
		// 2 - getEncVelocity() returns it as 1/10th as the actual RPS, so you
		// multiply it by 10
		// thus, 60*10 = 600
		returnVals[0] = ((600 * leftFW.getEncVelocity()) / 1024);
		returnVals[1] = ((600 * rightFW.getEncVelocity()) / 1024);
		return returnVals;
	}
	
	public void lowgoal(){
		setFlywheelSpeed(new double[]{500,500});
	}

	public void setFlywheelSpeed(double[] speeds) {
		rightFW.set(speeds[0]);
		leftFW.set(speeds[1]);
		power[0] = speeds[0];
		power[1] = speeds[1];
		SmarterDashboard.putBoolean("turret", ((speeds[0] > 0.1) || (speeds[1] > 0.1)));
	}

	public double getFlywheelSpeed() {
		return leftFW.get();
	}

	public double getLeftPower() {
		return power[0];
	}

	public double getRightPower() {
		return power[1];
	}

	public void setIntakeSpeed(double speed) {
		intake.set(-speed);
		intakeMotor.set(speed);
		SmarterDashboard.putBoolean("intake", speed != 0);
		SmarterDashboard.putBoolean("INTAKE-REVERSE", speed < 0);
	}

	public double getIntakeSpeed() {
		return intakeMotor.get();
	}

	public void setPIDSpeed(int[] speeds) {
		leftFW.set(speeds[0]);
		rightFW.set(speeds[1]);
	}

}
