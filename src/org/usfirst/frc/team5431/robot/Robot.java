
package org.usfirst.frc.team5431.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team5431.robot.driveBase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.usfirst.frc.team5431.json.Json;
import org.usfirst.frc.team5431.json.JsonObject;
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
	//private static final double gyroSensitiviy=0.001661;

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
	public final static Map<String, Hashy> update = new HashMap<>();
	
	private static final double //distanceToOuterWork = 48, distanceToCrossWork = 135, // 128
			//distanceToCrossRough = 130, distanceToSeeOnlyTower = 12, 
			forwardGyro_barelyCross = 152, forwardGyro_barelyRough = 132, 
			forwardGyro_barelyRock = 150, forwardGyro_chevalCross = 80;// 122
	
	static {
		update.put("ballIn", new Hashy(false));
		update.put("leftFlywheel", new Hashy(0));
		update.put("rightFlywheel", new Hashy(0));
		update.put("intake", new Hashy(0));
		update.put("rDrive", new Hashy(0.0f));
		update.put("lDrive", new Hashy(0.0f));
		update.put("leftDistance", new Hashy(0.0f));
		update.put("rightDistance", new Hashy(0.0f));
		update.put("driveAverage", new Hashy(0.0f));
		update.put("choppers", new Hashy(false));
		update.put("auton", new Hashy(false));
		update.put("teleop", new Hashy(false));
		update.put("enabled", new Hashy(false));
		update.put("gyro", new Hashy((new double[] {0, 0, 0})));
		update.put("accel", new Hashy((new double[] {0, 0, 0})));
		update.put("towerdistance", new Hashy(0.0f));
		update.put("fromcenter", new Hashy(0.0f));
		update.put("battery", new Hashy(0.0f));
		update.put("pdptemp", new Hashy(0.0f));
		update.put("lflytemp", new Hashy(new Object()));
		update.put("rflytemp", new Hashy(new Object()));
	}
	
	final long updatePeriod = 30;
	final String ipAddr = "10.100.72.165";
	final int port = 5830;
	
	
	private String JD(String toPull) {
		return String.valueOf(update.get(toPull).get());
	}
	
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

        final Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
        	//Sender sender = null;
        	ThingWorx worx = new ThingWorx();
        	PowerDistributionPanel pdp = new PowerDistributionPanel();
        	try {
        		Thread.sleep(3000);
        	} catch(Exception ignored) {}
        	while(true) {
        		/*if(sender == null) {
                	try {
        				sender = new Sender(ipAddr, port);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}*/
        		try {
        		double[] gyro = (double[]) update.get("gyro").get();
        		double[] accel = (double[]) update.get("accel").get();
        		JsonObject toSend = Json.object()
        				.add("xangle", String.valueOf(gyro[0]))
        				.add("yangle", String.valueOf(gyro[1]))
        				.add("zangle", String.valueOf(gyro[2]))
        				.add("xaccel", String.valueOf(accel[0]))
        				.add("yaccel", String.valueOf(accel[1]))
        				.add("zaccel", String.valueOf(accel[2]))
        				.add("towerdistance", JD("towerdistance"))
        				.add("teleop", JD("teleop"))
        				.add("rightrpm", JD("rightFlywheel"))
        				.add("rightdrivepower", JD("rDrive"))
        				.add("rdistance", JD("rightDistance"))
        				.add("leftrpm", JD("leftFlywheel"))
        				.add("leftdrivepower", JD("lDrive"))
        				.add("ldistance", JD("leftDistance"))
        				.add("intake", JD("intake"))
        				.add("fromcenter", JD("fromcenter"))
        				.add("enabled", JD("enabled"))
        				.add("choppers", JD("choppers"))
        				.add("ballIn", JD("ballIn"))
        				.add("auton", JD("auton"));
        		try {
        			float batvol = (float) pdp.getVoltage();
        			float pdptemp = (float) pdp.getTemperature();
        			Robot.update.get("battery").set((float) batvol);
        			Robot.update.get("pdptemp").set((float) pdptemp);
        			toSend.add("battery", JD("battery"));
        			toSend.add("pdptemp", JD("pdptemp"));
        		} catch(Exception exc) {}
        		
        		try {
        			float lefttemp = (float)((CANTalon) Robot.update.get("lflytemp").get()).getTemperature();
        			float righttemp = (float)((CANTalon) Robot.update.get("rflytemp").get()).getTemperature();
        			toSend.add("lflytemp", String.valueOf(lefttemp));
        			toSend.add("rflytemp", String.valueOf(righttemp));
        		} catch(Exception exc) {}
        		
        		//sender.put_property("", toSend.toString());
        		worx.put_property(toSend.toString());
        		} catch(Exception err) {
        			err.printStackTrace();
        		}
        		try{Thread.sleep(updatePeriod);}catch(Throwable ignored){}
        	}
        });
        
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
 		driveBase.drive(0.0, 0.0);
 		lowbarAutonState= 0;
 		autonOtherLowbarState = 0;
 		autonPorticullisState = 0;
 		autonChevalState = 0;
		Robot.update.get("enabled").set((boolean) true);
		Robot.update.get("auton").set((boolean) true);
		Robot.update.get("teleop").set((boolean) false);
 		
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
		Robot.update.get("enabled").set((boolean) false);
		Robot.update.get("auton").set((boolean) false);
		Robot.update.get("teleop").set((boolean) false);
    	SmarterDashboard.putBoolean("connection", true);
    	SmarterDashboard.periodic();
    	drivebase.resetDrive();
    	driveBase.drive(0.0, 0.0);
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
    	Robot.update.get("auton").set((boolean) true);
    	SmartDashboard.putNumber("Gyro Angle", drivebase.ahrs.getYaw());
    	double[] gyroset = {drivebase.ahrs.getPitch(), drivebase.ahrs.getRoll(), drivebase.ahrs.getYaw()};
    	double[] accelset = {drivebase.ahrs.getVelocityX(), drivebase.ahrs.getVelocityY(), drivebase.ahrs.getVelocityZ()};
    	Robot.update.get("gyro").set((double[]) gyroset);
    	Robot.update.get("accel").set((double[]) accelset);

    	//Update RPM for fly wheels
        final double[] rpms = flywheels.getRPM();
		SmarterDashboard.putNumber("FLY-LEFT", rpms[0]);
		SmarterDashboard.putNumber("FLY-RIGHT", rpms[1]);
		Robot.update.get("leftFlywheel").set((double) rpms[0]);
		Robot.update.get("rightFlywheel").set((double) rpms[1]);
		//SmartDashboard.putBoolean("NAVX CALIBRATING", drivebase.ahrs.isCalibrating());
		Robot.update.get("towerdistance").set((double) Vision.distance);
		
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
    	Robot.drivebase.ahrs.reset();
		Robot.update.get("enabled").set((boolean) true);
		Robot.update.get("auton").set((boolean) false);
		Robot.update.get("teleop").set((boolean) true);
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
        
    	SmartDashboard.putNumber("Gyro Angle", drivebase.ahrs.getYaw());
    	double[] gyroset = {drivebase.ahrs.getRawGyroX(), drivebase.ahrs.getRawGyroY(), drivebase.ahrs.getRawGyroZ()};
    	double[] accelset = {drivebase.ahrs.getRawAccelX(), drivebase.ahrs.getRawAccelY(), drivebase.ahrs.getRawAccelZ()};
    	Robot.update.get("gyro").set((double[]) gyroset);
    	Robot.update.get("accel").set((double[]) accelset);

    	//Update RPM for fly wheels
        final double[] rpms = flywheels.getRPM();
		SmarterDashboard.putNumber("FLY-LEFT", rpms[0]);
		SmarterDashboard.putNumber("FLY-RIGHT", rpms[1]);
		Robot.update.get("leftFlywheel").set((double) rpms[0]);
		Robot.update.get("rightFlywheel").set((double) rpms[1]);
		//Update drivetrain distance
		final double[] driverpm = drivebase.getEncDistance();
		SmarterDashboard.putNumber("DRIVE-DISTANCE-LEFT", driverpm[0]);
		SmarterDashboard.putNumber("DRIVE-DISTANCE-RIGHT", driverpm[1]);
		Robot.update.get("leftDistance").set((double) driverpm[0]);
		Robot.update.get("rightDistance").set((double) driverpm[1]);
		Robot.update.get("towerdistance").set((double) Vision.distance);
		SmarterDashboard.periodic();
    }

	public void testPeriodic() {
	} // Not used

}
