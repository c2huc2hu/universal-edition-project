public class Delivery implements Behavior {
  // Left: -3 -2 -1
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
    // look to the right.
    // if house is on right, drive down road until we see the nth house. stop and drop
    // if house is on left, drive down road, do a U turn, drive up road until nth house. stop and drop

    System.out.println(this.state); // debug print
	  
    switch(this.state) {
      case START:
	// look to right
	// offset self by 120 deg		    
	// and go to the finding line state		  
        Robot.look(-90);			// neg = to right
        Robot.rotateDeg(200, 120); 		// turns in cw if 2nd arg is pos
	this.state = State.FINDING_LINE;		    
        break;
		    
      case FINDING_LINE: 
	// Find the line to follow by slowly turning ccw untill we stop ontop of the line
	// when locked, stop self, reset odometers and 
	// go to line following state		    
        Robot.drive(-20,20); 			// turns in ccw if (-,+)
        if (Math.abs(Robot.color - this.targetColor) < e) {
          Robot.stop();
          Robot.tachoReset();
          this.state = State.LINE_FOLLOWING;
        }
        break;
		    
      case LINE_FOLLOWING: 
	// when locked on line invoke pid
	// note we call updateState to poll necessary sensors (color)
	// do line follow until:
	//	the sonic sensor sees the nth house, if house is on right
	// 	we go to the end of the road, do a U-ee, and sees the nth house on way back, if the house is on left
	// completely blocking!!! non blocking doesn't work. :\
		    
	// travel down road first if house is one left		    
	if(Math.signum(this.targetDoor)<0) {
		while(Robot.dist < roadLength) {
			System.out.println(Robot.sonic);
			Robot.updateState(); 
			Robot.lineFollow(this.v,100,30,150,this.targetColor);  		//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
		}
		Robot.tachoReset();
	}
		    
	// Go now and count the houses	    
    	while(Robot.dist < roadLength) {
	    	System.out.println(Robot.sonic);
	    	Robot.updateState(); 
	        Robot.lineFollow(this.v,100,30,150,this.targetColor);  			//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
	        if (Robot.sonic < 40) {							// find the posedge
	        	this.nthHouse++;						// increment nth house
	        	if (this.nthHouse >= Math.abs(this.targetDoor)) {		// check if nth house is the correct house
	        		Robot.stop();
	        		this.distToHouse = Robot.dist;				// remember how ar it is from start of line to house
				this.state = State.DELIVERING;
	        		break;							// stop following line, go to next state
	        	}
	        }
	        this.prevSonic = Robot.sonic;
    	}
        break;
		    		    
      case DELIVERING:
	// Deliver the pizza by turning to the right and droping the pizza in the yard
        Robot.rotateDeg(50,90);			// turns slowly in cw dir 90 deg. 2nd arg is +
        Robot.drop();				// drops
        this.state = State.TURNING_BACK;	// switch state
        break;
		    		    
      case TURNING_BACK:
	// Aim self to go back.
	//	do a U-ee and go backtrack distToHouse if house on right
	//	go additional roadLenght - distToHouse if house on left
	if(Math.signum(this.targetDoor)>0) {
		// lock on to the road, pointing in the anti parallel dir by going cw
		Robot.drive(20,-20); 					// turns in cw if (+,-)
		if (Math.abs(Robot.color - this.targetColor) < e) {
		  Robot.stop();
		  Robot.tachoReset();
		  this.state = State.RETURNING;
		}
        }
		
	if(Math.signum(this.targetDoor)<0) {
		// lock on to the road, pointing in the same dir by going ccw
		Robot.drive(-20,20); 					// turns in ccw if (-,+)
		if (Math.abs(Robot.color - this.targetColor) < e) {
		  Robot.stop();
		  Robot.tachoReset();
		  this.state = State.RETURNING;
		}
        }
        break;
		    
      case RETURNING:
	// Go back to head of road.
	//	travel distToHouse if house on right
	//	travel roadLength - distToHouse if house on left
	if(Math.signum(this.targetDoor)>0) {
		while(Robot.dist < this.distToHouse) {
			System.out.println(Robot.sonic);
			Robot.updateState(); 
			Robot.lineFollow(this.v,100,30,150,this.targetColor);  		//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
		}
	}
	if(Math.signum(this.targetDoor)<0) {
		while(Robot.dist < this.roadLength - this.distToHouse) {
			System.out.println(Robot.sonic);
			Robot.updateState(); 
			Robot.lineFollow(this.v,100,30,150,this.targetColor);  		//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
		}
	}
	Robot.tachoReset();
	Robot.readyToReturn = 1;		    
	this.state = DONE;		    
	break;	   
		    
      default:
        System.out.println("this shouldn't happen: default state");
    }
  }
 }
