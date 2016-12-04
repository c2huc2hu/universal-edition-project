import lejos.hardware.Button;

public class Main {

	public static void main(String[] args) {
		Behavior[] behaviours = {
			// insert your behavior instantiation here
			// we should try running combonations of behaviors too at some point to make
			// sure integration works
		  new Avoider(30), // avoider arg is "too close"
		  new Pathfinder(),
 		  new Delivery(50,0.312f)
		};

		System.out.println("STARTING MAIN on delivery branch");
		Button.waitForAnyPress();
		Robot.gyroReset();
		Robot.updateState();

		// Code for initial cmd input

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

			if (Button.UP.isDown()) {
				Robot.stop(); 
				System.out.println(Robot.position);
			}
		}
		System.out.println(Robot.position);
	}

}
