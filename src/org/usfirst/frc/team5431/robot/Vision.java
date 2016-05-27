package org.usfirst.frc.team5431.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class does Vision processing.
 * 
 * @author David Smerkous (code writer)
 * @author Liav Turkia (code commenter)
 *
 */
public class Vision {
	public static VisionMath math;
	public static Grip grip;
	public static final double screenHalf = 120, offVal = 0;

	private static double[] areas = new double[20], distances = new double[20], fromCenters = new double[20], holeSolids = new double[20];

	public static double area = 0, distance = 0, fromCenter = 0, holeSolid = 0;

	/**
	 * Contains information about position of robot/actions needed to be taken
	 * based on vision processing. Note that this only updates if the Update()
	 * function is called repeatedly. Index and what they stand for:
	 * <ol>
	 * 0 - Whether or not to turn to shoot. 0 is SHOOT, 1 is for Turn Left, 2 is
	 * for Turn Right
	 * </ol>
	 * <ol>
	 * 1 - Whether or not to move forward/back. 0 is SHOOT, 1 is for Drive Back,
	 * 2 is for Drive Forward
	 * </ol>
	 */
	public static final double[] manVals = { 0, 0, 0 };

	/**
	 * Constructor for the Vision() class.
	 */
	public Vision() {
		try {
			math = new VisionMath();
			grip = new Grip();
		} catch (Throwable initError) {
			SmarterDashboard.putString("ERROR", "Error initializing grip");
			//SmartDashboard.putString("stringBug", "Error initializing grip");
		}
	}

	/**
	 * Update function for the Vision() class. <b>This function must be called
	 * repeatedly for the Vision() class to work</b>
	 */
	public void Update() {
		this.updateVals();
		try {
			this.calcVals();
		} catch (Throwable error) {
		}
	}

	void updateVals() {
		areas = grip.area();
		distances = grip.distance(math);
		fromCenters = grip.fromCenter(screenHalf, math);
		holeSolids = grip.solidity();
	}

	private void calcVals() {
		final int toShoot = math.chooseHole(areas, distances, holeSolids, fromCenters); // Chooses
		// an
		// object
		// to
		// shoot
		// at(Method
		// below)
		SmarterDashboard.putNumber("HOLE-NUM", toShoot); // Display to dashboard
															// what
		// to shoot at
		if (toShoot != 666) {// Don't shoot at nothing (THE DEVIL)
			SmartDashboard.putString("DEVIL", "IS UP");
			SmartDashboard.putNumber("INDEXER", toShoot);
			double tempCenter = grip.fromCenter(screenHalf, math)[toShoot]; // Temp
													// center
						
			//SmartDashboard.putNumber("FROMCENTER", (tempCenter));
			// values
			// Display values to SmartDashboard!
			SmarterDashboard.putNumber("HOLE-AREA", areas[toShoot]);
			SmarterDashboard.putNumber("HOLE-DISTANCE", distances[toShoot]);
			SmarterDashboard.putNumber("HOLE-CENTER", tempCenter);
			SmartDashboard.putNumber("DISTANCE_FROM_CENTER", tempCenter);
			SmarterDashboard.putNumber("HOLE-SOLITIY", holeSolids[toShoot]);
			area = areas[toShoot];
			distance = distances[toShoot];
			fromCenter = tempCenter;
			manVals[1] = (math.withIn(distances[toShoot], VisionMath.minDistance, VisionMath.maxDistance)) ? 0
					: (distances[toShoot] < VisionMath.minDistance) ? 1 : 2; // Get
																				// which
																				// direction
																				// to
																				// drive

			manVals[0] = (math.withIn(tempCenter, VisionMath.leftTrig, VisionMath.rightTrig)) ? 0
					: (tempCenter < VisionMath.leftTrig) ? 1 : 2; // Amount to
																	// turn the
																	// turrent

			if ((manVals[1] == 0) && (manVals[0] == 0)) {
				SmarterDashboard.putString("FIRE", "F");
				SmarterDashboard.putString("PULL", "F");
				// Robot.led.wholeStripRGB(255, 0, 0);
				manVals[1] = 0;
				manVals[2] = 0;
			} else {
				SmarterDashboard.putString("PULL", "NA");
				SmarterDashboard.putString("FIRE", "NA");

				if (manVals[1] == 1) {
					SmarterDashboard.putString("PULL", "DB");
					// Robot.led.backwards(0, 0, 255, 60);
				} else if (manVals[1] == 2) {
					SmarterDashboard.putString("PULL", "DF");
					// Robot.led.forwards(0, 255, 255, 60);
				}

				if (manVals[0] == 1) {
					SmarterDashboard.putString("FIRE", "TL");
					// Robot.led.turnLeft(255, 135, 0, 65);
				} else if (manVals[0] == 2) {
					SmarterDashboard.putString("FIRE", "TR");
					// Robot.led.turnRight(255, 135, 0, 65);
				}
			}
		} else {
			SmartDashboard.putString("DEVIL", "IS DOWN");
			SmarterDashboard.putString("FIRE", "NA");
			SmarterDashboard.putString("PULL", "NA");
			// Robot.led.wholeStripRGB(120, 140, 120);
			manVals[0] = 5;
			manVals[1] = 5;
		}
	}

	public double getRPMS() {
		//SmartDashboard.putNumber("Current DISTANCE:", getDistance());
		return math.SpeedCalc(getDistance());
	}

	public int getSpeed() {
		return (int) math.SpeedCalc(getDistance());
	}

	public double getDistance() {
		return distance;
	}

	public double getFromCenter() {
		return fromCenter;
	}

	public double getLeftRight() {
		return manVals[0];
	}

	public double getForwardBackward() {
		return manVals[1];
	}

	public boolean withIn(double num, double lower, double upper) {
		return math.withIn(num, lower, upper);
	}
}
