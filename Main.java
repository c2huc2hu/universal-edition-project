import lejos.hardware.Button; 

public class Main {
	
	public static void main(String[] args) {

		System.out.println("STARTING MAIN");
		Robot.gyroReset();
		
		int targetPizza = 1;
		float s1 = 14;
		float s2 = 56;
		
		while (Button.ESCAPE.isUp()) {
			int sel = 0;
			while(Button.ENTER.isUp()){
			    if(Button.RIGHT.isDown()) sel += 1;
			    if(Button.LEFT.isDown())  sel -= 1;
			    if(sel > 2) sel=0;
			    if(sel < 0) sel =2;
			    swtich(sel){
				case(0):
				if(Button.UP.isDown()) targetPizza+=1;
				if(Button.DOWN.isDown()) targetPizza -=1;
				System.out.print("targetpizza: ");
				System.out.println(targetPizza);
				case(1):
				if(Button.UP.isDown()) s1+=1f;
				if(Button.DOWN.isDown()) s1-=1f;
				System.out.print("s1: ");
				System.out.println(s1);
				case(2):
				if(Button.UP.isDown()) s2+=1f;
				if(Button.DOWN.isDown()) s2-=1f;
				System.out.print("s2: ");
				System.out.println(s2);
            			}
        		}
			// Code for initial cmd input
			PizzaGrab pizzaGrab = new PizzaGrab(targetPizza,s1,s2);
			// Richard might want to put grab pizza here
			pizzaGrab.moveGrab();	
		}
	}
	
}
