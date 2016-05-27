package org.usfirst.frc.team5431.robot;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CANTalon;
import org.usfirst.frc.team5431.robot.driveBase;

public class autonPIDOutput implements PIDOutput{
	
	public double stall = 0.35;
	private static long previousTime = 0;
	private static long currTime = 0;
	//aver = (aver*n + timeDifference) / (n + 1)
	private static long average = 0;
	private static int averageCount = 0;
	
	public void pidWrite(double output){
		SmartDashboard.putNumber("GetError", Robot.drivebase.autoAimController.getError());
		SmartDashboard.putNumber("GetAvgError", Robot.drivebase.autoAimController.getAvgError());
		
		currTime = System.currentTimeMillis();
		if(previousTime != 0)
		{
			average = (average * averageCount + (currTime - previousTime))/(averageCount + 1);
			averageCount++;
		}
		previousTime = currTime;
		
		SmartDashboard.putNumber("averageTime", average);
		if(driveBase.flag == driveBase.pidFlag.driving)
			//driveBase.drive(driveBase.notPIDSideSpeed, output);//Flipped for competition bot
			driveBase.drive(output,  driveBase.notPIDSideSpeed);
		else
		{
			if(output > 0.07 && output < stall)
			{
				driveBase.drive(stall, -stall);
			}
			else if(output < -0.07 && output > -stall)
			{
				driveBase.drive(-stall, stall);
			}
			else
				driveBase.drive(output, -output);
		}
			
		SmartDashboard.putNumber("PIDWriteOutput", output);
	}
}
