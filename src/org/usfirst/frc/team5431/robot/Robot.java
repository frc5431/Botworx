
package org.usfirst.frc.team5431.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team5431.robot.driveBase;
import org.usfirst.frc.team5431.robot.Shooter;
/**
 * This is the second version of competition code (sans David as per request). This competition code is intended to be a functional
 * version without the bells and whistles that are added to the first version of the competition code. 
 */
public class Robot extends IterativeRobot {
    SendableChooser chooser;
    static driveBase drivebase;
    static Shooter flywheels;
    static Teleop teleop;
    static Autonomous auton;
    static OI oiInput;    
	enum AutoTask{ Lowbar,Rockwall,RoughTerrain,Cheval,Porticullis,Reach,None};
	static AutoTask currentAuto;
	static AnalogGyro gyro;
	private static final double gyroSensitiviy=0.001661;

	public double angleToTurnTo = 0;
	public int distanceToGoTo = 0;
	public int[] shootSpeed = {0,0};
	public int autonOtherLowbarState = 0;
	public int autonPorticullisState = 0;
	public int lowbarAutonState = 0;
	public int autonChevalState = 0;
	public int station = 0;
	public boolean autoaim = false;
	public static final boolean brakeMode = true;    
	public static double startGyroAngle;
	
	private static final double distanceToOuterWork = 48, distanceToCrossWork = 135, // 128
			distanceToCrossRough = 130, distanceToSeeOnlyTower = 12, forwardGyro_barelyCross = 152, forwardGyro_barelyRough = 132, forwardGyro_barelyRock = 150, forwardGyro_chevalCross = 80;// 122
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        
        drivebase = new driveBase(brakeMode);
        flywheels = new Shooter();
        teleop = new Teleop();
        auton = new Autonomous();
        oiInput = new OI(0, 1);
        //gyro = new AnalogGyro(0);
        drivebase.ahrs.reset();
        //gyro.initGyro();
        //gyro.setSensitivity(gyroSensitiviy);
        //gyro.setSensitivity(.0016594);
        //gyro.calibrate();
        
        
        //SmarterDashboard.addDebugString("Robot started");
        
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java //SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	
    	drivebase.enableBrakeMode();
    	SmarterDashboard.putBoolean("AUTO", true);
    	currentAuto = AutoTask.valueOf(SmarterDashboard.getString("AUTO-SELECTED", "Lowbar"));
 		//SmartDashboard.putString("Auto Selected: ", currentAuto.toString());
 		drivebase.resetDrive();
 		drivebase.drive(0.0, 0.0);
 		lowbarAutonState= 0;
 		autonOtherLowbarState = 0;
 		autonPorticullisState = 0;
 		autonChevalState = 0;

 		//drivebase.ahrs.zeroYaw();
 		
 		startGyroAngle=drivebase.ahrs.getYaw();
 		drivebase.ahrs.reset();
 		//SmartDashboard.putNumber("Init Pitch", drivebase.ahrs.getPitch());
 		//SmartDashboard.putNumber("Init Roll", drivebase.ahrs.getRoll());
 		//drivebase.setRampRate(12);
 		station = (int) SmarterDashboard.getNumber("STATION", 1);//What station the driver selected
		autoaim = (boolean) SmarterDashboard.getBoolean("AIM-ON", false);//Whether or not to autoAim
		//Set angle and speed to shoot at. Note that station 1 is just 'in case' - should use
		//the lowbar function instead.
		switch(station){
		case 1:
			angleToTurnTo = 39;
			shootSpeed[0] = 3100;
			shootSpeed[1] = 3100;
			break;
		case 2:
			angleToTurnTo = 25;
			shootSpeed[0] = 3370;
			shootSpeed[1] = 3370;
			break;
		case 3:
			angleToTurnTo = 16.5;
			shootSpeed[0] = 3370;
			shootSpeed[1] = 3370;
			break;
		case 4:
			angleToTurnTo = -10;
			shootSpeed[0] = 3370;
			shootSpeed[1] = 3370;
			break;
		case 5:
			angleToTurnTo = -20;
			shootSpeed[0] = 3370;
			shootSpeed[1] = 3370;
			break;
		default:
			break;
		}
		/*
		 * Sets the distance to cross based on the defense, and enables autonomous.
		 */
		switch (currentAuto) {
		case None:
			//crossForward();
			break;
		case Cheval:
			// moatForward();
			//moatForwardState = 1;
			distanceToGoTo = (int)forwardGyro_chevalCross;
			autonOtherLowbarState = 0;
			lowbarAutonState = 0;
			autonPorticullisState = 0;
			autonChevalState = 1;
			break;
		case RoughTerrain:
			distanceToGoTo = (int)forwardGyro_barelyRough;
			autonOtherLowbarState = 1;
			lowbarAutonState = 0;
			autonPorticullisState = 0;
			autonChevalState = 0;
			break;
		case Lowbar:
			distanceToGoTo = (int)forwardGyro_barelyCross;
			autonOtherLowbarState = 0;
			autonPorticullisState = 0;
			autonChevalState = 0;
			lowbarAutonState = 1;
			break;
			
		case Rockwall:	//Position 4 - -25
									//Position 3 - -10
			//shootRockWall();		//Position 1 - 25
			//gyroTurnAngle = -25;   //Position 2 - 16.5
			distanceToGoTo = (int)forwardGyro_barelyRock;
			autonOtherLowbarState = 1;
			lowbarAutonState = 0;
			autonPorticullisState = 0;
			autonChevalState = 0;
			break;
		case Porticullis:
			//PorticShoot();
			distanceToGoTo = (int)forwardGyro_barelyCross;
			autonPorticullisState = 1;
			autonOtherLowbarState = 0;
			lowbarAutonState = 0;
			autonChevalState = 0;
			break;
		case Reach:
			Timer.delay(0.1);
			break;
		default:
			Timer.delay(0.1);
			break;
		}
    }
    
    public void disabledPeriodic(){
    	drivebase.enableBrakeMode();
    	SmarterDashboard.putBoolean("AUTO", false);
    	SmarterDashboard.putBoolean("ENABLED", false);
    	SmarterDashboard.putBoolean("connection", true);
    	SmarterDashboard.periodic();
    	drivebase.resetDrive();
    	drivebase.drive(0.0, 0.0);
    	//SwitchCase.moveAmount = 0.468;
//    	Autonomous.autoAIMState = false;
//    	Autonomous.currAIM = 0;
//    	Autonomous.driveForwardState = 0;
    	SmartDashboard.putNumber("Gyro Angle", drivebase.ahrs.getYaw());
    	
    	//SmartDashboard.putNumber("NavX X", drivebase.ahrs.getRawGyroX());
    	SmartDashboard.putNumber("NavX Y", drivebase.ahrs.getPitch());
    	SmartDashboard.putNumber("NavX Z", drivebase.ahrs.getRoll());
    	//SmartDashboard.putBoolean("NAVX CALIBRATING", drivebase.ahrs.isCalibrating());
    	Timer.delay(0.1); //Sleep a little for little overhead time
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	//auton.updateStates(currentAuto);
    	SmarterDashboard.putBoolean("connection", true);
    	SmarterDashboard.putBoolean("AUTO", true);
    	SmartDashboard.putNumber("Gyro Angle", drivebase.ahrs.getYaw());
    	//Update RPM for fly wheels
        final double[] rpms = flywheels.getRPM();
		SmarterDashboard.putNumber("FLY-LEFT", rpms[0]);
		SmarterDashboard.putNumber("FLY-RIGHT", rpms[1]);
		//SmartDashboard.putBoolean("NAVX CALIBRATING", drivebase.ahrs.isCalibrating());
		
		autonOtherLowbarState = SwitchCase.autonomous(autonOtherLowbarState, angleToTurnTo, distanceToGoTo, shootSpeed, autoaim);
		lowbarAutonState = SwitchCase.autonomousLowbar(lowbarAutonState, angleToTurnTo, distanceToGoTo, shootSpeed, autoaim);
		autonPorticullisState = SwitchCase.autonomousPorticullis(autonPorticullisState, angleToTurnTo, distanceToGoTo, shootSpeed, autoaim);
		autonChevalState = SwitchCase.autonomousCheval(autonChevalState, angleToTurnTo, distanceToGoTo, shootSpeed, autoaim);
		//Timer.delay(0.005); // Wait 50 Hz
    	//SmarterDashboard.periodic();
    	//SmartDashboard.putNumber("autonOtherLowbar", autonOtherLowbarState);
    	//SmartDashboard.putNumber("lowbarAutonState", lowbarAutonState);
    	//SmartDashboard.putNumber("autonCheval", autonChevalState);
    	
    	//SmartDashboard.putNumber("NavX Pitch", drivebase.ahrs.getPitch());
    	//SmartDashboard.putNumber("NavX Roll", drivebase.ahrs.getRoll());
    }
    public void teleopInit(){
    	drivebase.enableBrakeMode();
    	drivebase.resetDrive();
    	drivebase.setRampRate(0);
    	Robot.drivebase.disablePIDC();
    	Robot.drivebase.enableBrakeMode();
    	Robot.drivebase.ahrs.reset();;
    }
    /**
     * This function is called periodically during operator control.
     * Calls the update functions for the OI and the Teleop classes.
     */
    public void teleopPeriodic() {
    	//SwitchCase.moveAmount = 0.468;
        oiInput.updateVals();
        teleop.Update(oiInput);
        //SmartDashboard.putNumber("Gyro Angle", Robot.drivebase.ahrs.getYaw());
        
        //Update connection
        SmarterDashboard.putBoolean("ENABLED", true);
        SmarterDashboard.putBoolean("connection", true);
        
        //Update RPM for fly wheels
        final double[] rpms = flywheels.getRPM();
		SmarterDashboard.putNumber("FLY-LEFT", rpms[0]);
		SmarterDashboard.putNumber("FLY-RIGHT", rpms[1]);
		
		//Update drivetrain distance
		final double[] driverpm = drivebase.getEncDistance();
		SmarterDashboard.putNumber("DRIVE-DISTANCE-LEFT", driverpm[0]);
		SmarterDashboard.putNumber("DRIVE-DISTANCE-RIGHT", driverpm[1]);
    	SmarterDashboard.periodic();
    }

	public void testPeriodic() {
	} // Not used

}
