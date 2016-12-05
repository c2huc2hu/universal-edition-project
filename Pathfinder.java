class Pathfinder implements Behavior {
	Position[] waypoints = {new Position(200, 0), new Position(0, 0)};
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
		double dx = waypoints[curWaypoint].x - Robot.position.x;
		double dy = waypoints[curWaypoint].y - Robot.position.y;
		if (dx*dx+dy*dy < 5) {
			// close enough. we have reached the target
			Robot.tachoReset();
			if(Utils.angleDiffR(Robot.gyro,angles[curWaypoint]) >  Math.PI / 16) {
				Robot.drive(200, -200);
			}
			else {
				curWaypoint = (curWaypoint + 1) % waypoints.length;
				System.out.println("cur waypoint" + curWaypoint); 
			}
		}
		else if (Utils.angleDiffR(Robot.gyroR, Math.atan2(dy, dx)) > Math.PI / 16) {
			// pivot toward target position if we're not at the right orientation
			Robot.tachoReset();
			Robot.drive(200, -200);
		}
		else {
			// drive forward
			Robot.drive(200, 200);
		}
	}
}
