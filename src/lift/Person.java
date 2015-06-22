package lift;

public class Person extends Thread {
	private int currentFloor;
	private int nextFloor;
	private Monitor mon;
	private int delay;

	public Person(Monitor mon) {
		currentFloor = (int) (Math.random() * 7);
		nextFloor = (int) (Math.random() * 7);
		while (currentFloor == nextFloor) {
			nextFloor = (int) (Math.random() * 7);
		}
		this.mon = mon;
	}

	public void run() {
		try {
			while (true) {
				delay = 1000 * ((int) (Math.random() * 46.0));
				sleep(delay);
				mon.enterFloor(currentFloor);

				mon.enterLift(currentFloor, nextFloor);

				mon.exitLift(nextFloor);

				currentFloor = nextFloor;
				nextFloor = (int) (Math.random() * 7);
				while (currentFloor == nextFloor) {
					nextFloor = (int) (Math.random() * 7);
				}

			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
