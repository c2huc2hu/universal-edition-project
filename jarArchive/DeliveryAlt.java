import lejos.hardware.Button;
public class DeliveryAlt implements Behavior {
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

  private float[] prevSonics = {10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000}; // the last value of the sonic reading
  private float nthHouse = 0; // the current house we're at
  private float distToHouse; // the distance to the desired house. need to backtrack this (or we could just look for a coloured loop)
  private float initialOrientation = 0;
  private float sign;
  private float err;

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
  public DeliveryAlt(float v, float targetColor, int targetDoor) {
    this.targetDoor = targetDoor;
    this.targetColor = targetColor;
    this.v = v;	  
    this.drivewayLength = 30f;
    this.roadLength = 140f;
	this.sign = Math.signum(this.targetDoor);
  }

  // detailed constructor
  public DeliveryAlt(float v, float targetColor, int targetDoor, float drivewayLength, float roadLength) {
    this.v = v;
    this.targetDoor = targetDoor;
    this.targetColor = targetColor;
    this.drivewayLength = drivewayLength;
    this.roadLength = roadLength;
	this.sign = Math.signum(this.targetDoor);
  }

  public boolean checkActive() {
    return Robot.readyToDeliver==1 && this.state != State.DONE; // active when on line and not finished
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

    //System.out.println(this.state); // debug print
	  
    switch(this.state) {
      case START:
	  System.out.println(this.state);
	// look to right
	// rotate self either parallel or antiparrallel
        Robot.look(-90);			// neg = to right
        if (this.sign<0) Robot.rotateDeg(200, 180); 		// turns in cw if 2nd arg is pos
		this.state = State.LINE_FOLLOWING;
        break;
		    
      case FINDING_LINE: 	  
	// we won't need to find line.
		break;
		
      case LINE_FOLLOWING: 	  
	// we go forwards or backwards based on side of door
	// stop at the right door and note the dist travelled
	
	// reset the Robot.dist variable
	Robot.tachoReset();				
	Robot.updateState();
		    
	// Go now and count the houses
	this.nthHouse = 0;
	float sum = 0; float currAvg = 0; float histAvg = 0; int i =0;
	int passing = 0; float err = 0;
    	while(Robot.dist < this.roadLength) {
		// idea is we take a average of 5 samples, around 20 samples ago.
		// and check the average of the prev 5 samples.
		// if the current average is below a threshold and 
		// the current average - history average <-10 (d/dt<-10, a major decreasing function)
		// then we have just arrived at a house, posedge.
		// do the same for neg edge which allows us to "pass by the house"
		// if houses are too small, we can have hist and curr be closer.
		// 5, 5 samples averages is exactly 10cm IRL
		
			//System.out.println(passing+" ; "+Robot.dist);
			
			// update sonic history
			Robot.updateState();
			for(i = 0; i<24;i++) {this.prevSonics[i] = this.prevSonics[i+1];}
			if (Robot.sonic==Float.POSITIVE_INFINITY||Robot.sonic>50) this.prevSonics[24] = 10000;
			else this.prevSonics[24] = Robot.sonic;
			
			// get avgs
			sum = 0;
			for(i = 20; i<25;i++)	{sum = sum + this.prevSonics[i];}
			currAvg = sum/5;
			sum = 0;
			for(i = 0; i<5;i++)	{sum = sum + this.prevSonics[i];}
			histAvg = sum/5;
			
			Robot.drive(this.sign*100,this.sign*100);
			err = (float)(Utils.floorMod(Robot.gyro,360) - Utils.floorMod(this.sign*Robot.roadAng,360));
			if (err <-3) {
				// pivot toward target position if we're not at the right orientation
				Robot.tachoReset();
				Robot.drive(35,-35);					
			}
			if (err >3) {
				Robot.tachoReset();
				Robot.drive(-35,35);						
			}
	        
			if (currAvg-histAvg<-10 && passing==0){
				if (currAvg < 35) {							// find the posedge
					this.nthHouse++;						// increment nth house
					passing = 1;
					if (this.nthHouse >= Math.abs(this.targetDoor)) {		// check if nth house is the correct house
						Robot.stop();
						this.distToHouse = Robot.dist;				// remember how ar it is from start of line to house
						this.state = State.DELIVERING;
						break;							// stop following line, go to next state
					}
				}
			}
			if(currAvg-histAvg>10) passing = 0;
    	}
		Robot.stop();
		this.state = State.DELIVERING; 			// force code to continue even if no house found
        break;
		    		    
      case DELIVERING:	  
	  System.out.println(this.state);
		// Deliver the pizza by turning to the right and droping the pizza in the yard
        Robot.rotateDeg(50,90);			// turns slowly in cw dir 90 deg. 2nd arg is +
        Robot.drop();				// drops
		Robot.stop();
        this.state = State.RETURNING;	// switch state
        break;
		    		    
      case TURNING_BACK:
		// we don't need to aim.. lol
		break;
		    
      case RETURNING:	  
	// we go forwards or backwards based on side of door
	// stop at the right door and note the dist travelled
	
	// reset the Robot.dist variable
	Robot.tachoReset();				
	Robot.updateState();
		    
	// Go now and count the houses
	this.nthHouse = 0;
		err = 0;
    	while(Robot.dist < this.roadLength) {
			Robot.drive(this.sign*-100,this.sign*-100);
			err = (float)(Utils.floorMod(Robot.gyro,360) - Utils.floorMod(this.sign*Robot.roadAng,360));
			if (err <-3) {
				// pivot toward target position if we're not at the right orientation
				Robot.tachoReset();
				Robot.drive(35,-35);					
			}
			if (err >3) {
				Robot.tachoReset();
				Robot.drive(-35,35);						
			}

    	}

		Robot.stop();
		Robot.look(0);
		Robot.tachoReset();
		Robot.updateState();
		//Robot.position = Robot.roadHead;
		Robot.readyToReturn = 1;		    
		this.state = State.DONE;	
		System.out.println(this.state);	    
		break;	   
		    
      default:
        System.out.println("this shouldn't happen: default state");
    }
  }
 }
