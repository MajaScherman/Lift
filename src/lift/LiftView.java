package lift;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class LiftView {

	private JFrame view;
	private FixedSizePanel entryPane,shaftPane;
	private FloorExit exitPane;
	private Basket basket;
	private static int FLOOR_HEIGHT = 100;
	private static int ENTRY_WIDTH = 300;
	private static int EXIT_WIDTH = 200;
	private static int SHAFT_WIDTH = 150;
	private static int NO_OF_FLOORS = 7;
	private static int MAX_LOAD = 4;
	private FloorEntry[] floorIn;

	public LiftView() {
		view = new JFrame("LiftView");
		view.getContentPane().setLayout(new BorderLayout());
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		view.addWindowListener(l);
		view.setResizable(false);
		entryPane = new FixedSizePanel(ENTRY_WIDTH,NO_OF_FLOORS*FLOOR_HEIGHT);
		entryPane.setLayout(new GridLayout(NO_OF_FLOORS,1));
		floorIn = new FloorEntry[NO_OF_FLOORS];
		for(int i=0;i<NO_OF_FLOORS;i++) {
			floorIn[NO_OF_FLOORS-i-1] = new FloorEntry(ENTRY_WIDTH,FLOOR_HEIGHT);
			entryPane.add(floorIn[NO_OF_FLOORS-i-1]);
		}
		view.getContentPane().add("West",entryPane);
		shaftPane = new FixedSizePanel(SHAFT_WIDTH,NO_OF_FLOORS*FLOOR_HEIGHT);
		shaftPane.setBackground(Color.LIGHT_GRAY);
		shaftPane.setLayout(null);
		view.getContentPane().add("Center",shaftPane);
		exitPane = new FloorExit(EXIT_WIDTH,NO_OF_FLOORS,FLOOR_HEIGHT);
		view.getContentPane().add("East",exitPane);
		basket = new Basket(SHAFT_WIDTH,NO_OF_FLOORS,FLOOR_HEIGHT,shaftPane);
		view.pack();
		view.setVisible(true);
	}

	public void drawLift(int floor, int load) {
		if (load<0 || load>MAX_LOAD) {
			throw new Error("Illegal load parameter to drawLift.");
		}
		if (floor<0 || floor>=NO_OF_FLOORS) {
			throw new Error("Illegal floor parameter to drawLift");
		}
		boolean animate = basket.getLoad()>load;
		basket.draw(floor,load);
		if (animate) {
			exitPane.animatePerson(floor); 
		}
	}

	public void drawLevel(int floor, int persons) {
		if (floor<0 || floor>=NO_OF_FLOORS) {
			throw new Error("Illegal floor in call to drawLevel.");
		}
		if (persons<0) {
			throw new Error("Negative number of persons in call to drawLevel.");
		}
		Thread.yield();
		floorIn[floor].draw(persons);
		Thread.yield();
	}

	public void moveLift(int here, int next) {
		if (here<0 || here>=NO_OF_FLOORS || next<0 || next>=NO_OF_FLOORS ||
				here==next) {
			throw new Error("Illegal parameters to moveLift.");
		}
		basket.moveBasket(here,next);
		try {
			Thread.sleep(200);
		} catch(InterruptedException e) { }
	}


	public static void main(String[] args) {
		LiftView lv = new LiftView();
		lv.drawLift(0,3);
		lv.drawLevel(5,4);
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) { }
		lv.moveLift(0,1);
		lv.drawLift(1,2);
	}

	private class FixedSizePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Dimension dim;

		public FixedSizePanel(int w,int h) {
			dim = new Dimension(w,h);
			setSize(dim);
		}

		public Dimension getPreferredSize() {
			return dim;
		}
	}

	private class FloorEntry extends FixedSizePanel {
		private static final long serialVersionUID = 1L;
		private int width,height;
		private int waiting;

		public FloorEntry(int w,int h) {
			super(w,h);
			setBackground(Color.WHITE);
			height = h;
			width = w;
			waiting = 0;
		}

		public void draw(int w) {
			waiting = w;
			Thread.yield();
			repaint();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawLine(0,height-1,width,height-1);
			for(int i=0;i<waiting;i++) {
				PersonDrawer.draw(g,ENTRY_WIDTH-(i+1)*35,height-5);
			}
		}
	}

	private class FloorExit extends FixedSizePanel {
		private static final long serialVersionUID = 1L;
		private int width,floorHeight,noOfFloors;
		private int animateX,animateY;

		public FloorExit(int w,int nof,int fh) {
			super(w,nof*fh);
			width = w;
			noOfFloors = nof;
			floorHeight = fh;
			setBackground(Color.WHITE);
			animateX = 0;
			animateY = 0;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for(int i=1;i<noOfFloors;i++) {
				g.drawLine(0,i*floorHeight-1,width,i*floorHeight-1);
			}
			if (animateY!=0) {
				PersonDrawer.draw(g,animateX,animateY);
			}
		}

		public void animatePerson(int floor) {
			animateY = (noOfFloors-floor)*floorHeight-5;
			for(animateX=0;animateX<width;animateX+=20) {
				repaint();
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) { }
			}
			animateX = 0;
			animateY = 0;
			repaint();
		}
	}

	private class Basket extends FixedSizePanel {
		private static final long serialVersionUID = 1L;
		private int width,floorHeight,noOfFloors;
		private int INCREMENT = 2;
		private int load;

		public Basket(int w,int nof,int fh,FixedSizePanel shaft) {
			super(w-4,fh);
			width = w;
			noOfFloors = nof;
			floorHeight = fh;
			load = 0;
			setBackground(Color.YELLOW);
			shaft.add(this);
			setLocation(2,(nof-1)*fh);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawRect(0,0,width-5,floorHeight-1);
			for(int i=0;i<load;i++) {
				PersonDrawer.draw(g,i*35+5,floorHeight-5);
			}
		}

		private int floorOffset(int floor) {
			return (noOfFloors-floor-1)*floorHeight;
		}

		public int getLoad() {
			return load;
		}

		public void moveBasket(int from, int to) {
			int start = floorOffset(from);
			int stop = floorOffset(to);
			if (start<stop) {
				for(int y=start;y<stop;y+=INCREMENT) {
					setLocation(2,y);
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) { }
				}
			} else {
				for(int y=start;y>stop;y-=INCREMENT) {
					setLocation(2,y);
					try {
						Thread.sleep(50);
					} catch(InterruptedException e) { }
				}
			}
			setLocation(2,stop);
		}

		public void draw(int f,int l) {
			load = l;
			setLocation(2,floorOffset(f));
			repaint();
		}
	}

}

class PersonDrawer {

	public static void draw(Graphics g,int x,int y) {
		g.drawLine(x,y,x+12,y-30);
		g.drawLine(x+12,y-30,x+24,y);
		g.drawLine(x+12,y-30,x+12,y-55);
		g.drawLine(x+12,y-55,x,y-35);
		g.drawLine(x+12,y-55,x+24,y-35);
		g.drawOval(x+5,y-70,15,15);
	}

	public static void erase(Graphics g,int x,int y) {
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		draw(g,x,y);
		g.setColor(c);
	}

}
