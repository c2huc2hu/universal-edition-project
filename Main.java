import lejos.hardware.Button; 

public class Main {
	
	public static void main(String[] args) {
		System.out.println("STARTING MAIN");
		Robot.gyroReset();
		Robot.updateState();
		
		int[] targetRoad = {1, 2, 3};
		int[] targetDoors = {-3, -2, -1, 1, 2, 3};
		int[] targetPizza = {-1, 1};
		int[] targetSel = {20,60,30};  // off set these so that we can have some clicks until we get into the negative numbers. 
		int[] targetSize = {2,6,3};
		int i = 30;
		
		// Code for initial cmd input
// 		get target door
// 		get target pizza
		while (Button.ENTER.isUp()) {
			if(Button.RIGHT.isDown()) i = (i+1)%3;
			if(Button.LEFT.isDown()) i = (i-1)%3; // mode of neg num isn't well defined
			if(Button.UP.isDown()) {
				targetSel[i] = (targetSel[i]+1)%targetSize[i];
			}
			if(Button.DOWN.isDown()) {
				targetSel[i] = (targetSel[i]-1)%targetSize[i]; // apparently mod of neg num isn't well defined.. :(
			}
			if ( i == 0) System.out.println("pizza: "+targetPizza[targetSel[i]]);
			if ( i == 1) System.out.println("door: "+targetDoors[targetSel[i]]);
			if ( i == 2) System.out.println("road: "+targetRoad[targetSel[i]]);
		}
		
		
		Behavior[] behaviours = {
			// insert your behavior instantiation here
			// we should try running combonations of behaviors too at some point to make
			// sure integration works
		  new Avoider(30), // avoider arg is "too close", deliberately un-comment this to look at whether or not delivery will integrate with other things.
//		  new Pathfinder(),
		  new Delivery(50,0.312f,targetDoors[targetSel[1]]) // arg: base speed when travelling on road, black line sonic value, need to put in target door
		}; 


		// Richard might want to put grab pizza here
		while (Button.ESCAPE.isUp()) {
			for(i=0; i<behaviours.length; i++){
				if(behaviours[i].checkActive()) {
					behaviours[i].act(-1);
					break;
				}
			}
			Robot.updateState(); 
			// System.out.println(Robot.position);
			
			if (Button.UP.isDown()) 
				System.out.println(Robot.position);
		}
//		System.out.println(Robot.position); 
	}
	
}
