public class Delivery implements Behavior {
  // Left: -1 -2 -3
  // Right: 1 2 3
  public int targetDoor;
  public float targetColor;
  public float drivewayLength = 50f;
  public float roadLength = 150f;

  public float e = 0.2f; // tolerance
  public float v;	// velocity on road [cm/s] convert to rotation speed
  public float b;	// width of block [cm]
  public float f;	// estimated poll frequency [1/s]

  private float prevSonic = -10000; // the last value of the sonic reading
  private float nthHouse = 0; // the current house we're at
  private float distToHouse; // the distance to the desired house. need to backtrack this (or we could just look for a coloured loop)
  private float initialOrientation = 0;

  private enum State {
    START,
    FINDING_LINE,
    LINE_FOLLOWING,
    DELIVERING,
    TURNING_BACK,
    RETURNING,
    DONE
  }
  public State state = State.START;		// change this line to State.START for full run. to test sequences, change this line

  // Constructor with default values
  public Delivery(float v, float targetColor, int targetDoor) {
    this.targetDoor = targetDoor;
    this.targetColor = targetColor;
    this.v = v;	  
    this.drivewayLength = 30f;
    this.roadLength = 140f;
  }

  // detailed constructor
  public Delivery(float v, float targetColor, int targetDoor, float drivewayLength, float roadLength) {
    this.v = v;
    this.targetDoor = targetDoor;
    this.targetColor = targetColor;
    this.drivewayLength = drivewayLength;
    this.roadLength = roadLength;
  }

  public boolean checkActive() {
    return Robot.readyToDeliver==1 && this.state != State.TURNING_BACK; // active when on line and not finished
  }

  // Assumed start:
  // 	Robot is at a stop
  //	on the colored loop
  //	has a pizza to deliver
  // Targeted end:
  //	Robot is at a stop
  // 	on colored loop oriented antiparrallel to road
  public void act(int dummy) {
    // Make sure ultrasonic is looking the right direction, i.e. toward the side
	  System.out.println(this.state); // debug print
    switch(this.state) {
      case START: // look toward the correct side
        Robot.look((int)(-90*Math.signum(this.targetDoor))); //POS IS TO LOOK RIGHT
        this.initialOrientation = Robot.gyro;
        this.state = State.FINDING_LINE;
        Robot.rotateDeg(200, 80); //turns in cw
        break;
      case FINDING_LINE: // Find the line to follow
        Robot.drive(-20,20); // turns in ccw
        if (Math.abs(Robot.color - this.targetColor) < e) {
          this.state = State.LINE_FOLLOWING;
          Robot.stop();
          Robot.tachoReset();
        }
        break;
      case LINE_FOLLOWING: // we need to test if we're on the line as well
    	while(Robot.dist < roadLength) {
	    	System.out.println(Robot.sonic);
	    	Robot.updateState(); 
	        Robot.lineFollow(this.v,100,30,150,this.targetColor);  //v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
	        if (Robot.sonic < 40) {
	        	this.nthHouse++;
	        	if (this.nthHouse >= 600) {
	        		this.state = State.DELIVERING; // found the right house!
	        		Robot.stop();
	        		this.distToHouse = Robot.dist;
	        		break;
	        	}
	        }
	        this.prevSonic = Robot.sonic;
    	}
//        if (Robot.dist > roadLength)
//          // report failure
//          System.out.println("reached end of road, didnt find anything :(");
//        else if (Robot.sonic - this.prevSonic < -e*10) // what are you trying to test here? you're testing for posedge(sonic), but why drivewayLength?
//          this.nthHouse++;
//          if (this.nthHouse == Math.abs(this.targetDoor)+1) {
//            this.state = State.DELIVERING; // found the right house!
//            Robot.stop();
//            this.distToHouse = Robot.dist;
//          }
//        this.prevSonic = Robot.sonic;
        break;
      case DELIVERING:
        Robot.rotateDeg(50,(int) (90 * Math.signum(this.targetDoor)));
        Robot.drop(); // can do a blocking statement here because we don't care
        this.state = State.TURNING_BACK;
        break;
      case TURNING_BACK:
        Robot.turn(-90 * Math.signum(this.targetDoor));  // should do a 180 degree turn
        Robot.turn(-10);
        Robot.drive(20,-20);
        if (Math.abs(Robot.color - this.targetColor) < e) {
          this.state = State.LINE_FOLLOWING;
          Robot.stop();
          Robot.tachoReset();
          this.state = State.RETURNING;
        }
//         if (Math.abs(Robot.gyro - this.initialOrientation -180) <= 5) {
//           Robot.tachoReset();
//           this.state = RETURNING;
//         }
        break;
      case RETURNING:
        Robot.lineFollow(this.v,350,30,500,this.targetColor); // drive back to starting position. yolo because we don't care about following the line
        if (Robot.dist >= this.distToHouse) { // could also be implemented with the colour sensor
          this.state = State.DONE;
	  Robot.readyToReturn = 1;
        }
        break;
      default:
        System.out.println("this shouldn't happen: default state");
    }
  }
 }
