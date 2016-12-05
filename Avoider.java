public class Avoider implements Behavior {
  private enum State { 
	NOT_AVOIDING, TURNING, PASSING
  }
  public float tooClose = 10;
  private int memory = 0;  // keep going for 100 ticks
  private State state = State.NOT_AVOIDING; 

  public Avoider(float tooClose) {
    this.tooClose = tooClose;
  }

  public boolean checkActive() {
	// when we first see an obstacle, start turning, 
	// and keep going until we no longer see it. 
	
	switch(state) { 
		case NOT_AVOIDING: 
			if (Robot.sonic < this.tooClose) {
				this.state = State.TURNING; 
				return true; 
			}
			return false; 
		case TURNING: 
			if (Robot.sonic > this.tooClose) {
				this.state = State.PASSING; 
				this.memory = 100; 
				Robot.look(-90); 
			}
			return true; 
		case PASSING: 
			// memory--; 
			if (Robot.dist > 20) { 
				Robot.look(0); 
				this.state = State.NOT_AVOIDING; 
			}
			return true; 
	}
	return false; 
  }

  public void act(int direction) {
	switch(this.state) {
		case NOT_AVOIDING:
			Robot.tachoReset(); 
			Robot.drive(-200, 200); 
			break; 
		case TURNING: 
			Robot.tachoReset(); 
			Robot.drive(-200, 200); 
			break; 
		case PASSING: 
			Robot.drive(200, 200); 
			break; 
	}
  }
}
