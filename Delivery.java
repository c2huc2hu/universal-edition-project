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
  public State state = State.START;

  // Constructor with default values
  public Delivery(int targetDoor, float targetColor) {
    this.targetDoor = targetDoor;
    this.targetColor = targetColor;
    this.drivewayLength = 30f;
    this.roadLength = 140f;
  }

  // detailed constructor
  public Delivery(int targetDoor, float targetColor, float drivewayLength, float roadLength) {
    this.targetDoor = targetDoor;
    this.targetColor = targetColor;
    this.drivewayLength = drivewayLength;
    this.roadLength = roadLength;
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
    // Make sure ultrasonic is looking the right direction, i.e. toward the side
	  System.out.println(this.state);
    switch(this.state) {
      case START: // look toward the correct side
        Robot.look((int)(90*Math.signum(this.targetDoor)));
        this.initialOrientation = Robot.gyro;
        this.state = State.FINDING_LINE;
        break;
      case FINDING_LINE: // Find the line to follow
        Robot.turn(20);
        Robot.drive(20,-20);
        if (Math.abs(Robot.color - this.targetColor) < e) {
          this.state = State.LINE_FOLLOWING;
          Robot.stop();
          Robot.tachoReset();
        }
        break;
<<<<<<< HEAD
      case LINE_FOLLOWING: // we need to test if we're on the line as well
        Robot.lineFollow(this.v,350,30,500,this.targetColor);   //v = 250 p = 350 i = 30 d= 500 tar = 0.312
        if (Robot.dist > roadLength)
          // report failure
          System.out.println("reached end of road, didnt find anything :(");
        else if (Robot.sonic - this.prevSonic < -e*10) // what are you trying to test here? you're testing for posedge(sonic), but why drivewayLength?
          this.nthHouse++;
          if (this.nthHouse == Math.abs(this.targetDoor)+1) {
            this.state = State.DELIVERING; // found the right house!
            Robot.stop();
            this.distToHouse = Robot.dist;
          }
        this.prevSonic = Robot.sonic;
=======
		    
      case LINE_FOLLOWING: 
	// when locked on line invoke pid
	// note we call updateState to poll necessary sensors (color)
	// do line follow until:
	//	the sonic sensor sees the nth house, if house is on right
	// 	we go to the end of the road, do a U-ee, and sees the nth house on way back, if the house is on left
	// completely blocking!!! non blocking doesn't work. :\
		    
	// travel down road first if house is one left		    
	if(Math.signum(this.targetDoor)<0) {
		while(Robot.dist < this.roadLength) {
			System.out.println(Robot.dist);
			Robot.updateState(); 
			Robot.lineFollow(this.v,100,30,150,this.targetColor);  		//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
		}
		Robot.stop();
		Robot.rotateDeg(200,300);		// over shoot on doing the U-ee
		Robot.drive(-20,20); 			// turns in ccw if (-,+) to lock on to path
		while(1==1) {
			Robot.updateState();
			if (Math.abs(Robot.color - this.targetColor) < e) {
				Robot.stop();
				break;
			}
		}
	}
	
	// reset the Robot.dist variable
	Robot.tachoReset();				
	Robot.updateState();
		    
	// Go now and count the houses
	this.nthHouse = 0;
	float sum = 0; float currAvg = 0; float histAvg = 0; int i =0;
	int passing = 0;
    	while(Robot.dist < this.roadLength) {
		// idea is we take a average of 5 samples, around 20 samples ago.
		// and check the average of the prev 5 samples.
		// if the current average is below a threshold and 
		// the current average - history average <-10 (d/dt<-10, a major decreasing function)
		// then we have just arrived at a house, posedge.
		// do the same for neg edge which allows us to "pass by the house"
		// if houses are too small, we can have hist and curr be closer.
		// 5, 5 samples averages is exactly 10cm IRL
			System.out.println(passing+" ; "+Robot.dist);
			
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
		
	        Robot.lineFollow(this.v,100,30,150,this.targetColor);  			//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
	        
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
>>>>>>> c45959d... only thing left in delivery is that it doesn't stop it self after it is done
        break;
      case DELIVERING:
<<<<<<< HEAD
        Robot.turn(90 * Math.signum(this.targetDoor));
        if (Math.abs(Robot.gyro - this.initialOrientation) >= 90) {
          Robot.stop();
          Robot.drop(); // can do a blocking statement here because we don't care
          this.state = State.TURNING_BACK;
        }
=======
		// Deliver the pizza by turning to the right and droping the pizza in the yard
        Robot.rotateDeg(50,90);			// turns slowly in cw dir 90 deg. 2nd arg is +
        Robot.drop();				// drops
		Robot.stop();
        this.state = State.TURNING_BACK;	// switch state
>>>>>>> c45959d... only thing left in delivery is that it doesn't stop it self after it is done
        break;
      case TURNING_BACK:
<<<<<<< HEAD
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
=======
		// Aim self to go back.
		//	do a U-ee and go backtrack distToHouse if house on right
		//	go additional roadLenght - distToHouse if house on left
		if(Math.signum(this.targetDoor)>0) {
			// lock on to the road, pointing in the anti parallel dir by going cw
			Robot.rotateDeg(100,120);				// cw 120, 2nd arg is +
			Robot.drive(-20,20); 					// turns in cw if (+,-)
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
>>>>>>> c45959d... only thing left in delivery is that it doesn't stop it self after it is done
        break;
      case RETURNING:
<<<<<<< HEAD
        Robot.lineFollow(this.v,350,30,500,this.targetColor); // drive back to starting position. yolo because we don't care about following the line
        if (Robot.dist >= this.distToHouse) { // could also be implemented with the colour sensor
          this.state = State.DONE;
        }
        break;
=======
		// Go back to head of road.
		//	travel distToHouse if house on right
		//	travel roadLength - distToHouse if house on left
		if(Math.signum(this.targetDoor)>0) {
			while(Robot.dist < this.distToHouse) {
				System.out.println(Robot.dist);
				Robot.updateState(); 
				Robot.lineFollow(this.v,100,30,150,this.targetColor);  		//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
			}
		}
		if(Math.signum(this.targetDoor)<0) {
			while(Robot.dist < (this.roadLength - this.distToHouse)) {
				System.out.println(Robot.dist);
				Robot.updateState(); 
				Robot.lineFollow(this.v,100,30,150,this.targetColor);  		//v =100 pid=100 30 150 //v = 250 p = 350 i = 30 d= 500 tar = 0.312
			}
		}
		Robot.stop();
		Robot.tachoReset();
		Robot.updateState();
		Robot.readyToReturn = 1;		    
		this.state = State.DONE;		    
		break;	   
		    
>>>>>>> c45959d... only thing left in delivery is that it doesn't stop it self after it is done
      default:
        System.out.println("this shouldn't happen: default state");
    }
  }
 }
