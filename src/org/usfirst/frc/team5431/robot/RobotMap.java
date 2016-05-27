package org.usfirst.frc.team5431.robot;
/**
 * Class that stores the mapping/ports of motors and encoders.
 * @author Usaid Malik
 *
 */
public class RobotMap {
	public static final int 
		frontright = 4,		//Mapping for driveBase motors
		frontleft = 6,
		rearright = 3,
		rearleft = 7,
		
		
		rightFlyWheel = 8,	//Mapping for Flywheel motors
		leftFlyWheel = 2,
		intake = 5,
		chopper = 9,
		climberExtend1 = 0,
		climberExtend2 = 7,
		climberRaise1 = 1,
		climberRaise2 = 6,
		choppers1=4, //Changed from 2
		choppers2 = 5,
		
		rightBaseEnc1 = 0,	//Mapping for encoders' DIO ports
		rightBaseEnc2 = 1,
		leftBaseEnc1 = 2,
		leftBaseEnc2 = 3,
		rightFWEnc1 = 14,
		rightFWEnc2 = 15,
		leftFWEnc1 = 16,
		leftFWEnc2 = 17,
		intakeLim = 4,

		mecanumIntake=0,
		
		pcm = 0;//PCM NEEDS TO BE 0. NOT ANYTHING ELSE.

}
