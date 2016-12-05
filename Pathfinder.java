class Pathfinder implements Behavior {
	Position[] waypoints = new Position[2];
	float[] angles = new float[2];
	int curWaypoint = 0;

	public Pathfinder(Position roadHead, float angle1, Position origin, float angle2) {
		waypoints[0] = roadHead; 
		waypoints[1] = origin;
		angles[0] = angle1; 
		angles[1] = angle2; 
	}

	public boolean checkActive() {
		// Robot.getPosition() && Robot.deliveredPizza;
		return (Robot.readyToDeliver-Robot.readyToReturn==0);
	}

	public void act(int direction) {
		// calculate a path
		double dx = waypoints[curWaypoint].x - Robot.position.x;
		double dy = waypoints[curWaypoint].y - Robot.position.y;
		
		System.out.println(Robot.gyro);
		
		if (dx*dx+dy*dy < 5) {
			// close enough. we have reached the target
			Robot.tachoReset();
			if(Utils.angleDiffR(Robot.gyro,angles[curWaypoint]) >  Math.PI / 16) {
				Robot.drive(75, -75);
			}
			else {
				curWaypoint = (curWaypoint + 1);				// increment to next waypoint
				Robot.readyToDeliver = 1;
				if (curWaypoint>=waypoints.length) Robot.isDone =1;
			}
		}
		else if (Utils.angleDiffR(Robot.gyroR, Math.atan2(dy, dx)) > Math.PI / 16) {
			// pivot toward target position if we're not at the right orientation
			Robot.tachoReset();
			Robot.drive(100, -100);
		}
		else {
			// drive forward
			Robot.drive(150, 150);
		}
	}
}
