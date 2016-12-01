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
        float s3 = -this.s2; //Backward for 56cm
        float r2 = -90 * this.targetPizza; //Rotate 90 degrees counterclockwise
        float s = 300; //Speed of the robot

    	  Robot.look(0);
        Robot.rotate(s, (int)(s1*convS), (int)(s1*convS));
        Robot.tachoReset();
        Robot.rotate(s, (int)(r1*convR), (int)(-r1*convR));
        Robot.tachoReset();
        Robot.rotate(s, (int)(s2*convS), (int)(s2*convS));
        
        //Grab
        Robot.grab();
        
        //Move back 
        Robot.tachoReset();
        Robot.rotate(s, (int)(s3*convS), (int)(s3*convS));
        Robot.tachoReset();
        Robot.rotate(s, (int)(r2*convR), (int)(-r2*convR));
        
	}
}
