public class PizzaGrab {
  public int targetPizza;
  public float s1 = 14;
  public float s2 = 56;
  public PizzaGrab(int _targetPizza, float _s1, float _s2){
    this.targetPizza = _targetPizza;
    this.s1 = _s1;
    this.s2 = _s2;
  }
  
  /*
	 * Uses the gripper to grab the object. Note: this function is blocking
	 * */
	public void moveGrab() {
        float convS = 360f/17.3f; //Conversion factor for going straight
        float convR = 2.05f; //conversion for the rotation
        float s1 = this.s1; //First go straight for 14cm
        float r1 = 90 * this.targetPizza; //Rotate clockwise 90 degrees
        float s2 = this.s2; //Go striaght for 56cm
        float r2 = -90 * this.targetPizza; //Rotate 90 degrees counterclockwise
        float s = 150; //Speed of the robot
		float s_2 = 225; // speed to roatate 90 at. 300 seems to be more accurate... ?

        Robot.rotate(s, (int)(s1*convS), (int)(s1*convS)); //Move up
        Robot.rotate(s_2, (int)(r1*convR), (int)(-r1*convR)); //Rotate 90 degrees
        Robot.rotate(s, (int)(s2*convS), (int)(s2*convS)); //Move to the pizza
        
        //Grab
        Robot.grab(); //Grab the pizza
        
        //Move back 
        Robot.rotate(s, (int)(-s2*convS), (int)(-s2*convS)); //Move back
        Robot.rotate(s_2, (int)(r2*convR), (int)(-r2*convR)); //Rotate to the start pose
	Robot.rotate(s, (int)(-s1*convS), (int)(-s1*convS)); //Back to the origin
        
	}
}
