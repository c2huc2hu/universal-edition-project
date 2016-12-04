import lejos.hardware.Button; 

public class Main {
	
	public static void main(String[] args) {

		System.out.println("STARTING MAIN");
		Robot.gyroReset();
		
		int targetPizza = 1; //User input for location of the pizza
		float s1 = 0;
		float s2 = 0;
		
		while (Button.ESCAPE.isUp()) {
			
			int sel = 0; //Selects between targetPizza, s1, and s2 (distance to the pizza)
			while(Button.ENTER.isUp()){
			    if(Button.RIGHT.isDown()) sel += 1;
			    if(Button.LEFT.isDown())  sel -= 1;
			    if(sel > 2) sel=0;
			    if(sel < 0) sel =2; //Rotates the selection menue
			    switch(sel){
				case(0):
					if(Button.UP.isDown()) targetPizza+=1;
					if(Button.DOWN.isDown()) targetPizza -=1;
					System.out.print("targetpizza: ");
					System.out.println(targetPizza);
					break; 
				case(1):
					if (targetPizza == -1){ //Initializes the s1 and s2 values based on targetPizza
						s1 = 28;
						s2 = 53;
					}
					if (targetPizza == 1){
						s1 = 16;
						s2 = 40;
					}
					if(Button.UP.isDown()) s1+=1f;
					if(Button.DOWN.isDown()) s1-=1f;
					System.out.print("s1: ");
					System.out.println(s1);
					break;
				case(2):
					if (targetPizza == -1){
						s1 = 28;
						s2 = 53;
					}
					if (targetPizza == 1){
						s1 = 16;
						s2 = 40;
					}
					if(Button.UP.isDown()) s2+=1f;
					if(Button.DOWN.isDown()) s2-=1f;
					System.out.print("s2: ");
					System.out.println(s2);
					break; 
    				}
			}
		
			// Code for initial cmd input
			PizzaGrab pizzaGrab = new PizzaGrab(targetPizza,s1,s2);
			//moveGrab function: robot is at the origin at the end of this
			pizzaGrab.moveGrab(); 
			//drop the pizza for retry if needed: these next two lines need to be deleted for actual run
			Robot.drop();
			Button.waitForAnyPress();	
			
		}
	}
	
}
