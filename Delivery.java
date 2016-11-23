public class Lock implements Behavior {
  public int targetDoor = 0;
  // Left: -1 -2 -3
  // Right: 1 2 3
  
  public float drivewayLength = 50f;
  public float roadLength = 150f;
	
  public float e = 0.2; // tolerance
  public float v;	// velocity on road [cm/s] convert to rotation speed
  public float b;	// width of block [cm]
  public float f;	// estimated poll frequency [1/s]
  
  public Lock(int targetDoor,float drivewayLength,float roadLength) {
    this.targetDoor = targetDoor; 
    this.drivewayLength = drivewayLength;	  
    this.roadLength = roadLength;		  
  }
  
  public boolean checkActive() { 
    return isAtRoad && hasPizza; 
  }
  
  // Assumed start:
  // 	Robot is at a stop
  //	on the colored loop
  //	has a pizza to deliver
  // Targeted end:
  //	Robot is at a stop
  // 	on colored loop oriented antiparrallel to road	
  public void act() {
    // Make sure utrasonic is looking the right direction
    while(getLook()*targetDoor<=0) look(90*targetDoor/Math.abs(targetDoor));   
    
    // Lock on road	  
    Robot.turn(20);
    Robot.drive(20,-20);
    while(Math.abs(Robot.pollColor(false)-targetColor)>e) {}
    Robot.drive(0,0);	  
  
    // follow the path and poll the sonic sensor
    // wait for nth number of spikes as prescribed by targetDoor
    float oldSonic = 0;
    float newSonic = 0;
    int count = 0;
    Robot.tachoReset();	  
    Robot.drive(v,v);
    while(pollDist(true)<roadLength || count<Math.abs(this.targetDoor)) {
	oldSonic = newSonic;
        newSonic = pollSonic(false);
	if (newSonic-oldSonic<-drivewayLength)
	    count++; 
    }
    Robot.drive(0,0);
    float dist = Robot.pollDist(false);
// case, no hit???	  
	  
    // stop, orientate and drop pizza
    Robot.turn(90*this.targetDoor/Math.abs(this.targetDoor));
    Robot.drop();
	  
    // orientate to go back, looks forward	  
    if(this.targetDoor<0) Robot.turn(-180*this.targetDoor/Math.abs(this.targetDoor));
    Robot.drive(20,-20);
    while(Math.abs(Robot.pollColor(false)-targetColor)>e) {}
    Robot.drive(0,0);
    Robot.tachoReset();	  
    while(getLook()*targetDoor!=0) look(-90*targetDoor/Math.abs(targetDoor));   
	  

    // arrive at start of road
    Robot.drive(v+150,v+150);
    while(Robot.pollDist<dist) {
    	if (dist-Robot.pollDist<15) Robot.drive(v,v);
    }
    Robot.drive(0,0);	  
	  
    // invoke next task
}
