import lejos.hardware.Button;

public class Main {

	public static void main(String[] args) {
		System.out.println("STARTING MAIN");
		Robot.gyroReset();
		Robot.updateState();
		
		int[] targetRoad = {1, 2, 3};
		int[] targetDoors = {-4,-3, -2, -1, 1, 2, 3,4};		// have up to 4 because we don't know if there will be road markers at demo
		int[] targetPizza = {-1, 1};
		int[] targetSel = {0,0,0};  						// off set these so that we can have some clicks until we get into the negative numbers. 
		int[] targetSize = {2,8,3};
		int i = 0;
		
		// Code for initial cmd input
		while (Button.ENTER.isUp()) {
			if(Button.RIGHT.isDown()) i = (i+1)%3;
			if(Button.LEFT.isDown()) { if ((i-1) < 0) i = 2; else i = i-1;}
			if(Button.UP.isDown()) {
				targetSel[i] = (targetSel[i]+1)%targetSize[i];
			}
			if(Button.DOWN.isDown()) {
				if ((targetSel[i]-1)<0) targetSel[i] = targetSize[i]-1;
				else targetSel[i] = (targetSel[i]-1); 
			}
			if ( i == 0) System.out.println("pizza: "+targetPizza[targetSel[i]]);
			if ( i == 1) System.out.println("door: "+targetDoors[targetSel[i]]);
			if ( i == 2) System.out.println("road: "+targetRoad[targetSel[i]]);			
			Button.waitForAnyPress();
		}
		
		switch(targetRoad[targetSel[2]]){
			case(1):
				Robot.roadHead = new Position(220,40);
				Robot.roadAng = 30;
				break;
			case(2):
				Robot.roadHead = new Position(220,0);
				Robot.roadAng = 0;
				break;
			case(3):
				Robot.roadHead = new Position(220,-40);
				Robot.roadAng = -30;
				break;
		}

		//====================================================================		
		
		Behavior[] behaviours = {
			// insert your behavior instantiation here
			// we should try running combonations of behaviors too at some point to make
			// sure integration works
		  new Avoider(20), // avoider arg is "too close"
		  new Pathfinder(Robot.roadHead, Robot.roadAng, new Position(0,0), 0),
		  new Delivery(100,0.312f,targetDoors[targetSel[1]]) // arg: base speed when travelling on road, black line sonic value, need to put in target door
		  //new DeliveryAlt(100,0.312f,targetDoors[targetSel[1]])
		};
		
		//====================================================================		
		// Richard grabs pizza
		// initialize params
		float s1 = 28,s2 = 53;
		if (targetPizza[targetSel[0]] == -1){
			s1 = 28;
			s2 = 53;
		}
		if (targetPizza[targetSel[0]] == 1){
			s1 = 16;
			s2 = 40;
		}
		// Allow dynamic adjustments
		int sel = 0;
		while(Button.ENTER.isUp()){
			    if(Button.RIGHT.isDown()) sel += 1;
			    if(Button.LEFT.isDown())  sel -= 1;
				if(sel > 1) sel=0;
			    if(sel < 0) sel =1; //Rotates the selection menue
				
			    switch(sel){
				case(0):
					if(Button.UP.isDown()) s1+=1f;
					if(Button.DOWN.isDown()) s1-=1f;
					System.out.print("s1: ");
					System.out.println(s1);
					break;
				case(1):
					if(Button.UP.isDown()) s2+=1f;
					if(Button.DOWN.isDown()) s2-=1f;
					System.out.print("s2: ");
					System.out.println(s2);
					break;
				}
				Button.waitForAnyPress();
		}
		System.out.println("GETTING pizza!! :D");
		// initialize pizzaGrab obj
		PizzaGrab pizzaGrab = new PizzaGrab(targetPizza[targetSel[0]],s1,s2);
		// Do moveGrab function: robot is at the origin at the end of this
		pizzaGrab.moveGrab();
		
		//====================================================================
		// enter the obstacle and delivery codes.
		System.out.println("Behaviors begin");

		while (Button.ESCAPE.isUp()||Robot.isDone == 0) {
			
			for(i=0; i<behaviours.length; i++){
				if(behaviours[i].checkActive()) {
					behaviours[i].act(-1);
					break;
				}
			}
			
			Robot.updateState();

			if (Button.UP.isDown()) {
				Robot.stop(); 
				System.out.println(Robot.position);
			}
			
			if(Button.ESCAPE.isDown()) {
				break;
			}
		}
	}
}
