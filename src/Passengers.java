// Owner: Jason Curcio

// TODO: Auto-generated Javadoc
/**
 * The Class Passengers.
 */
public class Passengers {
	
	/** The id. */
	private static int ID=0;
	// These will come from the csv file, and should be initialized in the 
	/** The time. */
	// constructor.
	private int time;
	
	/** The number. */
	private int number;
	
	/** The from floor. */
	private int fromFloor;
	
	/** The to floor. */
	private int toFloor;
	
	/** The polite. */
	private boolean polite;
	
	/** The give up time. */
	private int giveUpTime;
	// this will be initialized in the constructor so that it is unique for each
	/** The id. */
	// set of Passengers
	private int id;	
	// These fields will be initialized during run time - boardTime is when the group
	// starts getting on the elevator, timeArrived is when the elevator starts offloading
	/** The board time. */
	// at the desired floor
	private int boardTime;
	
	/** The time arrived. */
	private int timeArrived;

	/**
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param number the number
	 * @param fromFloor the from floor
	 * @param toFloor the to floor
	 * @param polite the polite
	 * @param giveUpTime the give up time
	 */
	public Passengers(int time, int number, int fromFloor, int toFloor, boolean polite, int giveUpTime) {
		this.time = time;
		this.number = number;
		this.fromFloor = fromFloor;
		this.toFloor = toFloor;
		this.polite = polite;
		this.giveUpTime = giveUpTime;
		this.id = ID;
		ID++;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public int getTime() {
		return this.time;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Gets the from floor.
	 *
	 * @return the from floor
	 */
	public int getFromFloor() {
		return this.fromFloor;
	}

	/**
	 * Gets the to floor.
	 *
	 * @return the to floor
	 */
	public int getToFloor() {
		return this.toFloor;
	}

	/**
	 * Gets the give up time.
	 *
	 * @return the give up time
	 */
	public int getGiveUpTime() {
		return this.giveUpTime;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Gets the board time.
	 *
	 * @return the board time
	 */
	public int getBoardTime() {
		return this.boardTime;
	}

	/**
	 * Gets the time arrived.
	 *
	 * @return the time arrived
	 */
	public int getTimeArrived() {
		return this.timeArrived;
	}
	
	/**
	 * Checks if is polite.
	 *
	 * @return true, if is polite
	 */
	public boolean isPolite() {
		return this.polite;
	}
	
	public void setPolite(boolean newValue) {
		polite = newValue;
	}
	
	/**
	 * Sets the time of arrival.
	 *
	 * @param time the new time of arrival
	 */
	public void setTimeOfArrival(int time) {
		this.timeArrived = time;
	}
	
	/**
	 * Sets the board time.
	 *
	 * @param time the new board time
	 */
	public void setBoardTime(int time) {
		this.boardTime = time;
	}
	
	public int getWaitTime() {
		return giveUpTime;
	}
}