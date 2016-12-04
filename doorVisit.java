/* public class doorVisit implements Behavior {
  public int targetDoor = 0;
  // Left: -1 -2 -3
  // Right: 1 2 3
  
  public Avoider(int targetDoor) {
    this.targetDoor = targetDoor; 
  }
  
  public boolean checkActive() { 
    return isAtRoad && hasPizza; 
  }
  
  public void act() {
    while(getLook()*targetDoor<0) look(90*targetDoor/Math.abs(targetDoor));   // Make sure utrasonic is looking the right direction
	  // follow the path and poll the sonic sensor
    // wait for nth number of spikes as prescribed by targetDoor
    // stop, orientate and drop pizza
    // orientate to go back, looks forward
    // arrive at start of road
    // invoke next task
}
 
 */ 