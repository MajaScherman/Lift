package lift;

public class Monitor {

	private int currentFloor;
	private int nextFloor;
	private boolean up;
	private int[] waitEntry;
	private int[] waitExit;
	private int load;
	private static final int LOAD_CAPACITY = 4;
	private LiftView lv;
	private int waiting;

	public Monitor(LiftView lv) {
		load = 0;
		this.lv = lv;
		waitEntry = new int[7];
		waitExit = new int[7];
		up = false;
		currentFloor = -1;
		nextFloor = 0;
		waiting = 0;
	}

	public static void main(String[] args) {
		LiftView lv = new LiftView();
		Monitor mon = new Monitor(lv);
		Lift lift = new Lift(mon, lv);
		for (int i = 0; i < 20; i++) {
			Person per = new Person(mon);
			per.start();
		}
		lift.start();

	}

	public synchronized void waitLift() {
		currentFloor = nextFloor;
		notifyAll();
		while (waitExit[currentFloor] != 0
				|| (load != LOAD_CAPACITY && waitEntry[currentFloor] > 0)
				|| waiting == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		changeAttributes();

		notifyAll();
	}

	public synchronized int[] getLocation() {
		int[] loc = new int[2];
		loc[0] = currentFloor;
		loc[1] = nextFloor;
		return loc;
	}

	private synchronized void changeAttributes() {
		if (nextFloor == 6 || nextFloor == 0) {
			up = !up;
		}

		if (up) {
			nextFloor++;

		} else {
			nextFloor--;

		}

		notifyAll();
	}

	public synchronized void enterFloor(int here) {
		waitEntry[here]++;
		waiting++;
		lv.drawLevel(here, waitEntry[here]);
		notifyAll();
	}

	public synchronized void enterLift(int here, int dest) {

		while (here != currentFloor || load >= LOAD_CAPACITY
				|| currentFloor != nextFloor) {

			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		load++;

		waitEntry[here]--;
		waitExit[dest]++;
		lv.drawLevel(here, waitEntry[here]);
		lv.drawLift(here, load);

		notifyAll();
	}

	public synchronized void exitLift(int here) {

		while (here != currentFloor || currentFloor!=nextFloor) {

			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		load--;
		waitExit[currentFloor]--;
		lv.drawLift(currentFloor, load);
		notifyAll();

	}
}
