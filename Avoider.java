public class Avoider implements Behavior {
  public float tooClose = 10;
  private boolean isAvoiding = false;

  public Avoider(float tooClose) {
    this.tooClose = tooClose;
  }

  public boolean checkActive() {
    if (isAvoiding) {
      if (Robot.sonic >= this.tooClose) {
        // we have passed the obstacle.
        Robot.look(0);
        this.isAvoiding = false;
      }
      return true;
    }

    if (Robot.sonic < this.tooClose) {
      Robot.drive(200, -200);
      Robot.look(90);
      Robot.tachoReset();
      this.isAvoiding = true;
    }
    return Robot.sonic < this.tooClose;
  }

  public void act(int direction) {
    Robot.drive(200, 200)
  }
}
