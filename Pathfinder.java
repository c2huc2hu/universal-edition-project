
class Pathfinder implements Behavior {
	Position[] waypoints = {new Position(200, 200), new Position(0, 0)};
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
			// close enough. we have reached the target
			curWaypoint++;
			Robot.tachoReset();
		}
		else if (Math.abs(Robot.gyroR - Math.atan2(dy, dx)) % (Math.PI * 2) < Math.PI / 4) {
			// pivot toward target position if we're not at the right orientation
			Robot.ticksSinceLastObstacle++;
			if ((Math.atan2(dy, dx) - Robot.gyroR) % (2*Math.PI) > 0)
				Robot.drive(200, -200);
			else
				Robot.drive(-200, 200);
			Robot.tachoReset();
		}
		else {
			// drive forward
			Robot.drive(200, 200);
		}
	}
}
