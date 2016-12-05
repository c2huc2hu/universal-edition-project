import lejos.hardware.Button;

class Pathfinder implements Behavior {
	Position[] waypoints = new Position[2];
	float[] angles = new float[2];
	int curWaypoint = 0;
	float sign =1 ;

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
				
		Robot.isPathTurning =0;
		
		if (dx*dx+dy*dy < 5) {
			// close enough. we have reached the target
			Robot.tachoReset();
			if(Utils.angleDiff(Robot.gyro,angles[curWaypoint]) >  12) {
				Robot.isPathTurning=1;
				sign = Math.signum((int)(Utils.floorMod(Robot.gyro - angles[curWaypoint], 360)));
				Robot.drive(35*sign, -35*sign);					// go cw if curr heading > target
			}
			else {				
				Robot.stop();
				System.out.println("got to pose");
				curWaypoint = (curWaypoint + 1);				// increment to next waypoint
				Robot.readyToDeliver = 1;
				if (curWaypoint>=waypoints.length) Robot.isDone =1;
			}
		}
		else if (Utils.angleDiff(Robot.gyro, (Math.atan2(dy, dx))*180/Math.PI) > 11&&(dx*dx+dy*dy)>12) {		// when we get close to the point, there is no point to adj heading more
			// pivot toward target position if we're not at the right orientation
			Robot.isPathTurning =1;
			Robot.tachoReset();
			sign = Math.signum((int)(Utils.floorMod(Robot.gyro - Math.atan2(dy, dx)*180/Math.PI, 360)));
			Robot.drive(-35*sign, 35*sign);						// go ccw if curr heading > target
		}
		else {
			// drive forward
			Robot.drive(125, 125);
		}
	}
}
