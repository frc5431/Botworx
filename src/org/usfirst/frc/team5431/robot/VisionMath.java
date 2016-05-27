package org.usfirst.frc.team5431.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionMath {

	// Choose hole options (Total should be 1.0)
	private static final double areaNum = 0.2, distNum = 0.2, solidNum = 0.4, fromNum = 0.2;

	// Distances and resolution values
	public static final double screenHalf = 160, minDistance = 76, maxDistance = 150, leftTrig = 33,//COMP : 29,
			/* This is actually the right */ rightTrig = 35;//COMP : 35; // this is actually the left

	private static final double maxArea = 1500, maxSolidity = 100;

	public static volatile double override = 0.0;

	/**
	 * Calculates the distance of a location
	 * <p>
	 * <b>Make sure to pretest the values</b>
	 * 
	 * @param pixelsFromTop
	 *            Number of pixels from the top
	 * @return The distance from the hole
	 */
	public double DistanceCalc(double pixelsFromTop) {
		return ((44.1401) * Math.pow(1.0068, pixelsFromTop)); // THE NEW BOT
																// (SHOULD BE
																// VERY CLOSE)
	}

	public double SpeedCalc(double distanceFromTower) {
		return (0.66108 * Math.pow(distanceFromTower, 2)) - (165.37 * distanceFromTower) + 13557;// Math.pow((10.9685
																									// *
																									// distanceFromTower),
																									// -0.5264);//
																									// ((3.4028)
																									// -
		// (0.5551 *
		// Math.log(distanceFromTower)))
		// + override;
	}

	/**
	 * Checks the distance of a location from the center of the camera
	 * 
	 * @param half
	 *            Center of the camera, in pixels
	 * @param current
	 *            Location to check
	 * @return Distance from the center of the camera, in pixels. Negative
	 *         values means it's to the left.
	 */
	public double fromCenter(double half, double current) {
		return current - half;
	}

	/*
	public double[] RPMCalc(double distanceCalc, double[] currentRPM, double currentPWR) {
		double[] rpms = { 0, 0 }; // Future RPM needed
		double[] speeds = { 0, 0, 0, 0, 0, 0 }; // Future speed to adjust
		double moveMentLeft = 0.00021481432536; // Amount of difference per RPM
		double moveMentRight = 0.000211551567469; // Amount of difference per
													// RPM

		// LEFT-SIDE // Code (9579.2 * .99048 ^x)
		// rpms[0] = 6099.2873 - (19.4674 * distanceCalc); //6099.2873 -
		// 19.4674x - LEFT FLY (DISTANCE-RPM (NEEDED))
		rpms[0] = 9579.2 * Math.pow(0.99048, distanceCalc) - 500;
		speeds[2] = ((rpms[0] - currentRPM[0]));
		speeds[0] = (speeds[2] * moveMentLeft);

		rpms[1] = rpms[0] + 30; // Orig = 200
		speeds[3] = ((rpms[1] - currentRPM[1]));
		speeds[1] = (speeds[3] * moveMentRight);

		speeds[4] = rpms[0];
		speeds[5] = rpms[1];

		SmarterDashboard.putNumber("AUTO-AIM-SPEED", rpms[0]);

		return speeds;
	}*/

	double largest = 0; // Don't mess
	int current = 0; // Don't mess

	/**
	 * Returns which hole to use based on various info
	 * 
	 * @param areas
	 *            Array with the area of each hole, where each index refers to a
	 *            hole (array[0]=hole 0)
	 * @param distances
	 *            Array with the distance of each hole from the camera, where
	 *            each index refers to a hole (array[0]=hole 0)
	 * @param solidity
	 *            Array with the solidity of each hole, where each index refers
	 *            to a hole (array[0]=hole 0)
	 * @param fromCenter
	 *            Array with the distance of each hole from the center of the
	 *            camera in pixels, where each index refers to a hole
	 *            (array[0]=hole 0.) Negative means to the left.
	 * @return Hole ID based on the parameters. If 666 is returned, no hole was
	 *         found.
	 */
	public int chooseHole(double[] areas, double[] distances, double[] solidity, double[] fromCenter) {
		final int amount = areas.length;

		// try {
		final double holes[] = new double[20]; // Don't mess

		// If any of the values are negative make sure that they are
		// positive
		for (int now = 0; now < amount; now++) {
			areas[now] = Math.abs(areas[now]);
			distances[now] = Math.abs(distances[now]);
			solidity[now] = Math.abs(solidity[now]);
			fromCenter[now] = fromCenter[now] + 32;
			
			holes[now] = fromCenter[now];//(((areas[now] / maxArea) * areaNum) + ((1 - (distances[now] / maxDistance)) * distNum)
					//+ ((solidity[now] / maxSolidity) * solidNum) - ((fromCenter[now] / screenHalf) * fromNum)) / 4;

					
//					if (holes[now] > largest) {
//						largest = holes[now];
//						current = now;
//					}
			if (holes[now] < largest) {
				largest = holes[now];
				current = now;
			}

		}
		return current;
		// } catch (Exception ignored) {
		// return 666; // Return 666 which means none found
		// }
	}

	// See if number is within two other numbers
	/**
	 * Checks to see if a number is within two other numbers
	 * 
	 * @param num
	 *            Number to compare
	 * @param lower
	 *            Lower bound
	 * @param upper
	 *            Upper bound
	 * @return Whether num is within lower and upper bounds.
	 */
	public boolean withIn(double num, double lower, double upper) {
		return ((num >= lower) && (num <= upper));
	}

}
