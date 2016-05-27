package org.usfirst.frc.team5431.robot;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class autoAimPIDInput implements PIDSource{
	PIDSourceType filler = PIDSourceType.kDisplacement;
	@Override
	public double pidGet() {
		SwitchCase.cameraVision.Update();
		// TODO Auto-generated method stub
		SmartDashboard.putNumber("VisionFromCenter", -Vision.fromCenter + 40);
		//SmartDashboard.putNumber("VisionFromCenterAdjusted", -Vision.fromCenter);
		//SmartDashboard.putNumber("Indicator", System.currentTimeMillis());
		return -Vision.fromCenter + 40;//44
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		filler = pidSource;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return filler;
	}

}
