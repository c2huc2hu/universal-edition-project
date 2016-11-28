import lejos.hardware.Button; 

public class Main {
	
	public static void main(String[] args) {
		System.out.println("STARTING MAIN");
		Robot.gyroReset();
		Robot.updateState();
		
		int[] targetDoors = {-3, -2, -1, 1, 2, 3};
		int[] targetPizza = {-1, 1};
		int[] targetSel = {0,0};
		int[] targetSize = {2,6};
		int i = 0;
		
		// Code for initial cmd input
// 		get target door
// 		get target pizza
		while (Button.ENTER.isUp()) {
			if(Button.RIGHT.isDown()) i = Math.mod(i+1,2);
			if(Button.LEFT.isDown()) i = Math.mod(i-1,2);
			if(Button.UP.isDown()) {
				targetSel[i] = Math.mod(targetSel[i]+1,targetSize[i]);
			}
			if(Button.UP.isDown()) {
				targetSel[i] = Math.mod(targetSel[i]-1,targetSize[i]);
			}
			if ( i == 0) System.out.println(targetDoors[targetSel[i]]);
			if ( i == 1) System.out.println(targetPizza[targetSel[i]]);
		
		}
		
		
		Behavior[] behaviours = {
			// insert your behavior instantiation here
			// we should try running combonations of behaviors too at some point to make
			// sure integration works
		  new Avoider(30), // avoider arg is "too close", deliberately un-comment this to look at whether or not delivery will integrate with other things.
//		  new Pathfinder(),
		  new Delivery(50,0.312f) // arg: base speed when travelling on road, black line sonic value, need to put in target door
		}; 


		// Richard might want to put grab pizza here
		while (Button.ESCAPE.isUp()) {
			for(int i=0; i<behaviours.length; i++){
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
		System.out.println(Robot.position); 
	}
	
}
