package lift;

public class Lift extends Thread {
	private Monitor mon;
	private LiftView lv;

	public Lift(Monitor mon, LiftView lv) {
		this.mon = mon;
		this.lv = lv;
	}

	public void run() {
		while (true) {
			mon.waitLift();
			int[] loc = mon.getLocation();
			//System.out.println(loc[0]+" " +loc[1]);
			lv.moveLift(loc[0], loc[1]);
			
			
		}
	}

}
