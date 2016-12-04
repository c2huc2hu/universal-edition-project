
class Pathfinder implements Behavior {
	Position[] waypoints = {new Position(200, 200), new Position(0, 0)};
	float[] angles = {90, 120};
	int curWaypoint = 0;

	public Pathfinder() {
		// idk if it needs a constructor
	}

	public boolean checkActive() {
		// Robot.getPosition() && Robot.deliveredPizza;
		return true; // default mode if nothing else works.
	}

	public void act(int direction) {
		// calculate a path
		double dx = waypoints[curWaypoint].x - Robot.position.x, dy = waypoints[curWaypoint].y - Robot.position.y;
		if (dx*dx+dy*dy < 5) {
			System.out.println("Close enough"); 
			// close enough. we have reached the target
			Robot.tachoReset();
			if((Robot.gyro - angles[curWaypoint]) < 15) {
				Robot.drive(200, -200);
			}
			else if((Robot.gyro - angles[curWaypoint]) > 15) {
				Robot.drive(-200, 200);
			}
			else {
				curWaypoint++;
			}
		}
		else if (Utils.angleDiffR(Robot.gyroR, Math.atan2(dy, dx)) < Math.PI / 8) {
			// pivot toward target position if we're not at the right orientation
			Robot.ticksSinceLastObstacle++;
			Robot.tachoReset();
			System.out.println("Turning toward target position"); 
			if ((Math.atan2(dy, dx) - Robot.gyroR) % (2*Math.PI) > 0)
				Robot.drive(200, -200);
			else
				Robot.drive(-200, 200);
		}
		else {
			// drive forward
			System.out.println("Driving forward"); 
			Robot.drive(200, 200);
		}
	}
}
