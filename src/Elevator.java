// Owner: Jason Curcio

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Elevator.
 */

/**
 * @author ScottM
 * This class will represent an elevator,and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targetting each floor...
 */
public class Elevator {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Elevator.class.getName());
	
	/** The Constant UNDEF. */
	// Elevator State Variables
	private final static int UNDEF = -1;
	
	/** The Constant STOP. */
	private final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	private final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	private final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	private final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	private final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	private final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	private final static int MV1FLR = 6;

	/** The capacity. */
	// Configuration parameters
	private int capacity = 15;
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  
	
	/** The pass per tick. */
	private int passPerTick = 3; // pass == passengers
	
	//State Variables
	/** The curr state. */
	// track the elevator state
	private int currState;
	
	/** The prev state. */
	private int prevState;
	
	/** The prev floor. */
	// track what floor you are on, and where you came from
	private int prevFloor;
	
	/** The curr floor. */
	private int currFloor;
	
	/** The direction. */
	// direction 1 = up, -1 = down
	private int direction;
	// timeInState is reset on state entry, used to determine if state is finished
	/** The time in state. */
	// or if floor has changed...
	private int timeInState;
	
	/** The door state. */
	// used to track where the the door is in OPENDR and CLOSEDR states 
	private int doorState; // 2 = open, 0 = closed
	
	/** The passengers. */
	// number of passengers on the elevator
	private int passengers;
	// when exiting the stop state, the floor to moveTo and the direction to go in once you
	/** The move to floor. */
	// get there...
	private int moveToFloor;
	
	/** The move to floor dir. */
	private int moveToFloorDir;

	
	/**
	 * Instantiates a new elevator.
	 *
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param tickPassengers the tick passengers
	 */
	public Elevator(int capacity, int floorTicks,
					int doorTicks, int tickPassengers) {		
		prevState = STOP;
		currState = STOP;
		direction = 1;
		this.capacity = 15;
		ticksPerFloor = 5;
		ticksDoorOpenClose = 2;
		passPerTick = 3;
		
		LOGGER.setLevel(Level.OFF);
	}
	
	
	/**
	 * Move elevator.
	 */
	public void moveElevator() {
		prevFloor = currFloor;
		if ((timeInState % ticksPerFloor) == 0) {
			currFloor = currFloor + direction;
		}
	}
	
	
	/**
	 * Close door.
	 */
	public void closeDoor() {
		doorState--;
	}
	
	
	/**
	 * Open door.
	 */
	public void openDoor() {
		prevFloor = currFloor;
		doorState++;
	}
	
	
	/**
	 * Checks if is door closed.
	 *
	 * @return true, if is door closed
	 */
	public boolean isDoorClosed() {
		return doorState == 0;
	}
	
	
	/**
	 * Checks if is door open.
	 *
	 * @return true, if is door open
	 */
	public boolean isDoorOpen() {
		return doorState == 2;
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		if (passengers == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is moving.
	 *
	 * @return true, if is moving
	 */
	public boolean isMoving() {
		return (timeInState % ticksPerFloor) != 0;
	}

	
	/**
	 * Update curr state.
	 *
	 * @param newCurrState the new curr state
	 */
	public void updateCurrState(int newCurrState) {
		timeInState++;
		prevState = currState;
		if (newCurrState != currState) {
			currState = newCurrState;
			timeInState = 1;
		}
	}
	
	/**
	 * Gets the time in state.
	 *
	 * @return the time in state
	 */
	public int getTimeInState() {
		return timeInState;
	}
	
	/**
	 * Increment time in state.
	 */
	public void incrementTimeInState() {
		timeInState++;
	}
	
	/**
	 * Gets the curr state.
	 *
	 * @return the curr state
	 */
	public int getCurrState() {
		return currState;
	}
	
	/**
	 * Gets the prev state.
	 *
	 * @return the prev state
	 */
	public int getPrevState() {
		return prevState;
	}
	
	/**
	 * Gets the ticks per floor.
	 *
	 * @return the ticks per floor
	 */
	public int getTicksPerFloor() {
		return ticksPerFloor;
	}
	
	/**
	 * Gets the curr floor.
	 *
	 * @return the curr floor
	 */
	public int getCurrFloor() {
		return currFloor;
	}
	
	/**
	 * Gets the prev floor.
	 *
	 * @return the prev floor
	 */
	public int getPrevFloor() {
		return prevFloor;
	}
	
	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * Sets the direction.
	 *
	 * @param dir the new direction
	 */
	public void setDirection(int dir) {
		direction = dir;
	}
	
	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 */
	public int getPassPerTick() {
		return passPerTick;
	}
	
	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}


	/**
	 * Sets the to floor.
	 *
	 * @param toFloor the new to floor
	 */
	public void setToFloor(int toFloor) {
		moveToFloor = toFloor;
		
	}
	
	/**
	 * Changed floors.
	 *
	 * @return true, if successful
	 */
	public boolean changedFloors() {
		return currFloor != prevFloor;
	}
	
	/**
	 * Calculate delay time.
	 *
	 * @param numPass the num pass
	 * @return the int
	 */
	public int calculateDelayTime(int numPass) {
		return (numPass % passPerTick == 0) ? (numPass / passPerTick):(numPass / passPerTick + 1);
	}
	
	/**
	 * Gets the ticks per door open close.
	 *
	 * @return the ticks per door open close
	 */
	public int getTicksPerDoorOpenClose() {
		return ticksDoorOpenClose;
	}
	
	/**
	 * Checks if is state changed.
	 *
	 * @return true, if is state changed
	 */
	public boolean isStateChanged() {
		return prevState != currState;
	}
	
	public boolean isFloorChanged() {
		return prevFloor != currFloor;
	}
}