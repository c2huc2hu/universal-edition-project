public class Utils { 
	public static double angleDiffR(double angle1, double angle2) { 
		double diff1 = floorMod(angle1 - angle2, 2 * Math.PI); 
		double diff2 = floorMod(angle2 - angle1, 2 * Math.PI); 
		return Math.min(diff1, diff2); 
	}
	
	/* Calculates the difference between two angles in degrees */
	public static double angleDiff(double angle1, double angle2) { 
		double diff1 = floorMod(angle1 - angle2, 360); 
		double diff2 = floorMod(angle2 - angle1, 360); 
		return Math.min(diff1, diff2); 
	}
	
	public static double floorMod(double a, double b) {
		return ((a % b) + b) % b;
	}
}