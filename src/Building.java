// Owner: Matvey Volkov

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class Building.
 */
public class Building {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());

	/** The fh. */
	private FileHandler fh;

	/** The Constant STOP. */
	// Elevator State Variables
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

	/** The num floors. */
	private final int NUM_FLOORS;

	/** The num elevators. */
	private final int NUM_ELEVATORS;

	/** The prioritized call. */
	private Passengers prioritizedCall;

	/** The floors. */
	private Floor[] floors;

	/** The passengers. */
	private GenericQueue<Passengers> passengers;

	/** The passengers currently in elevator. */
	private List<Passengers> passengersCurrentlyInElevator;

	/** The passengers that arrived. */
	private List<Passengers> passengersThatArrived;

	/** The passengers that gave up. */
	private List<Passengers> passengersThatGaveUp;

	/** The lift. */
	private Elevator lift;

	/** The delay time and number of boarded pass. */
	private int delayTime, numBoarded;

	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param numElevators the num elevators
	 * @param testfile the testfile
	 */
	public Building(int numFloors, int numElevators, String testfile) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		passengers = new GenericQueue<Passengers>(1000);
		passengersCurrentlyInElevator = new ArrayList<Passengers>();
		passengersThatArrived = new ArrayList<Passengers>();
		passengersThatGaveUp = new ArrayList<Passengers>();

		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			String logfile = testfile.replaceAll(".csv", ".log");
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create the floors
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(10); 
		}
	}


	/**
	 * Update elevator.
	 *
	 * @param time the time
	 */
	public void updateElevator(int time) {
		while (passengers.peek() != null && passengers.peek().getTime() == time) {
			if (passengers.peek().getFromFloor() > passengers.peek().getToFloor()) {

				LOGGER.info("Time="+time+" Called="+passengers.peek().getNumber()+" Floor="+
						(passengers.peek().getFromFloor()+1)

						+" Dir="+((checkDirOfPass(passengers.peek()) == 1)?"Up":"Down")+" passID=" + passengers.peek().getID());

				floors[passengers.peek().getFromFloor()].addRequest(-1, passengers.remove());

			} else if (passengers.peek().getFromFloor() < passengers.peek().getToFloor()) {

				LOGGER.info("Time="+time+" Called="+passengers.peek().getNumber()+" Floor="+
						(passengers.peek().getFromFloor()+1)

						+" Dir="+((checkDirOfPass(passengers.peek()) == 1)?"Up":"Down")+" passID=" + passengers.peek().getID());

				floors[passengers.peek().getFromFloor()].addRequest(1, passengers.remove());
			}
		}

		if (lift.isStateChanged() || lift.isFloorChanged()) {
			LOGGER.info("Time="+time+" Prev State: " + printState(lift.getPrevState()) + " Curr State: " + printState(lift.getCurrState())
			+" PrevFloor: "+(lift.getPrevFloor()+1) + " CurrFloor: " + (lift.getCurrFloor()+1));
		}

		switch (lift.getCurrState()) {
		case STOP: lift.updateCurrState(currStateStop(time)); break;
		case MVTOFLR: lift.updateCurrState(currStateMvToFlr(time)); break;
		case OPENDR: lift.updateCurrState(currStateOpenDr(time)); break;
		case OFFLD: lift.updateCurrState(currStateOffLd(time)); break;
		case BOARD: lift.updateCurrState(currStateBoard(time)); break;
		case CLOSEDR: lift.updateCurrState(currStateCloseDr(time)); break;
		case MV1FLR: lift.updateCurrState(currStateMv1Flr(time)); break;
		}
	}

	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	public String printState(int state) {
		switch (state) {
		case MV1FLR: return "MV1FLR";
		case MVTOFLR: return "MVTOFLR";
		case OPENDR: return "OPENDR";
		case OFFLD: return "OFFLD";
		case BOARD: return "BOARD";
		case CLOSEDR: return "CLOSEDR";
		default: return "STOP";
		}
	}

	/**
	 * Curr state stop.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateStop(int time) {
		if (floors[lift.getCurrFloor()].getUpQueueSize() > 0) {
			lift.setDirection(1);
			return OPENDR;
		} else if (floors[lift.getCurrFloor()].getDownQueueSize() > 0) {
			lift.setDirection(-1);
			return OPENDR;
		} else if (hasUpCalls() || hasDownCalls()) {
			prioritizedCall = prioritizeCalls();
			lift.setDirection(determineDirOfElevator(prioritizedCall.getFromFloor()));
			return MVTOFLR;
		} else {
			return STOP;
		}
	}

	/**
	 * Curr state mv to flr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateMvToFlr(int time) {
		lift.moveElevator();
		
		if (lift.getCurrFloor() == prioritizedCall.getFromFloor()) {
			lift.setDirection(checkDirOfPass(prioritizedCall));
			return OPENDR;
		} else {
			return MVTOFLR;
		}
	}

	/**
	 * Curr state open dr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateOpenDr(int time) {
		lift.openDoor();
		if (!lift.isDoorOpen()) {
			return OPENDR;
		} else if (passengersCurrentlyInElevator.size() != 0 && checkIfAnyoneNeedsToExitOnCurrentFloor()) {
			return OFFLD;
		} else if ((lift.getDirection() == 1 && floors[lift.getCurrFloor()].getUpQueueSize() > 0) 
				|| (lift.getDirection() == -1 && floors[lift.getCurrFloor()].getDownQueueSize() > 0)) {
			return BOARD;
		} else {
			return CLOSEDR;
		}
	}

	/**
	 * Curr state off ld.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateOffLd(int time) {

		if (lift.getPrevState() != OFFLD) {
			offloadGroup(time);
		}

		if (lift.getTimeInState() == delayTime) {
			delayTime = 0;
			if ((lift.getDirection() == 1 && floors[lift.getCurrFloor()].getUpQueueSize() > 0)
					|| (lift.getDirection() == -1 && floors[lift.getCurrFloor()].getDownQueueSize() > 0)) {

				return BOARD;
			}
			// no calls (above or below of any type depending on direction) and there are ppl boarding in the opp dir and elevator is empty
			if (passengersCurrentlyInElevator.size() == 0 
					&& !checkForCallsInCurrDir() 
					&& ((lift.getDirection() == 1 && floors[lift.getCurrFloor()].getDownQueueSize() > 0) 
							|| (lift.getDirection() == -1 && floors[lift.getCurrFloor()].getUpQueueSize() > 0))) {

				lift.setDirection(lift.getDirection() * -1);
				return BOARD;
			}

			return CLOSEDR;
		} else {
			return OFFLD;
		}
	}

	/**
	 * Curr state board.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateBoard(int time) {
		
		boardElevator(time);
		
		if (delayTime == 0) {
			return CLOSEDR;
		}
		
		if (lift.getTimeInState() >= delayTime) {
			delayTime = 0;
			return CLOSEDR;
		} else {
			return BOARD;
		}
	}

	/**
	 * Curr state close dr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateCloseDr(int time) {
		numBoarded = 0;
		lift.closeDoor();

		if (lift.getDirection() == 1) {
			Passengers head = floors[lift.getCurrFloor()].peekAtUpQueue();

			if (head != null && !head.isPolite()) {
				head.setPolite(true);
				return OPENDR;
			}
		} else {
			Passengers head = floors[lift.getCurrFloor()].peekAtDownQueue();

			if (head != null && !head.isPolite()) {
				head.setPolite(true);
				return OPENDR;
			}
		}

		if (lift.isDoorClosed()) {
			if (passengersCurrentlyInElevator.size() == 0) {
				if (floors[lift.getCurrFloor()].getDownQueueSize() == 0 
						&& floors[lift.getCurrFloor()].getUpQueueSize() == 0
						&& !hasUpCalls() && !hasDownCalls()) {
					return STOP;
				}

				if (checkForCallsInCurrDir()) {
					return MV1FLR;
				} else {
					lift.setDirection(lift.getDirection() * -1);
					return MV1FLR;
				}
			} else {
				return MV1FLR;
			}
		} else {
			return CLOSEDR;
		}
	}

	/**
	 * Curr state mv 1 flr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateMv1Flr(int time) {
		lift.moveElevator();

		if (lift.changedFloors()) {
			if (checkIfAnyoneNeedsToExitOnCurrentFloor()) {
				return OPENDR;
			}

			if ((lift.getDirection() == 1 && floors[lift.getCurrFloor()].getUpQueueSize() > 0)
					|| (lift.getDirection() == -1 && floors[lift.getCurrFloor()].getDownQueueSize() > 0)) {
				return OPENDR;
			}

			if (passengersCurrentlyInElevator.size() == 0 
					&& !checkForCallsInCurrDir() 
					&& ((lift.getDirection() == 1 && floors[lift.getCurrFloor()].getDownQueueSize() > 0) 
							|| (lift.getDirection() == -1 && floors[lift.getCurrFloor()].getUpQueueSize() > 0))) {

				lift.setDirection(lift.getDirection() * -1);
				return OPENDR;

			}
		}

		return MV1FLR;
	}

	/**
	 * Config elevators.
	 *
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param tickPassengers the tick passengers
	 */
	public void configElevators(int capacity, int floorTicks,
			int doorTicks, int tickPassengers) {

		lift = new Elevator(capacity, floorTicks,
				doorTicks, tickPassengers);
	}
	
	/**
	 * Log elevator config.
	 */
	public void logElevatorConfig() {
		LOGGER.info("CONFIG: Capacity="+lift.getCapacity()+" Ticks-Floor=" 
				+lift.getTicksPerFloor()+" Ticks-Door="+ lift.getTicksPerDoorOpenClose() +" Ticks-Passengers="+lift.getPassPerTick());
	}

	/**
	 * Prioritize calls.
	 *
	 * @return the passengers
	 */
	private Passengers prioritizeCalls() {

		if (numUpCalls() > numDownCalls()) {
			return getLowestPassengersGoingUp();
		}

		if (numUpCalls() < numDownCalls()) {
			return getHighestPassengersGoingDown();
		}

		if (numUpCalls() == numDownCalls()) {
			
			int diffBetweenCurrAndLowestGoingUp = Math.abs(lift.getCurrFloor() - getLowestPassengersGoingUp().getFromFloor());
			int diffBetweenCurrAndHighestGoingDown = Math.abs(lift.getCurrFloor() - getHighestPassengersGoingDown().getFromFloor());
			
			if (diffBetweenCurrAndLowestGoingUp > diffBetweenCurrAndHighestGoingDown) {
				return getHighestPassengersGoingDown();
			}
			
			if (diffBetweenCurrAndLowestGoingUp < diffBetweenCurrAndHighestGoingDown) {
				return getLowestPassengersGoingUp();
			}
		}
		
		return getLowestPassengersGoingUp();
	}

	/**
	 * Num up calls.
	 *
	 * @return the int
	 */
	public int numUpCalls() {
		int count = 0;
		for (int i = 0; i < floors.length; i++) {
			if (floors[i].getUpQueueSize() > 0) {
				count += floors[i].getUpQueueSize();
			}
		}
		return count;
	}

	/**
	 * Num down calls.
	 *
	 * @return the int
	 */
	public int numDownCalls() {
		int count = 0;
		for (int i = 0; i < floors.length; i++) {
			if (floors[i].getDownQueueSize() > 0) {
				count += floors[i].getDownQueueSize();;
			}
		}
		return count;
	}

	/**
	 * Gets the lowest passengers going up.
	 *
	 * @return the lowest passengers going up
	 */
	private Passengers getLowestPassengersGoingUp() {
		Passengers lowestPass = floors[floors.length - 2].peekAtUpQueue();
		for (int i = floors.length - 2; i >= 0; i--) {
			if (floors[i].peekAtUpQueue() != null) {
				lowestPass = floors[i].peekAtUpQueue();
			}
		}
		return lowestPass;
	}

	/**
	 * Gets the highest passengers going down.
	 *
	 * @return the highest passengers going down
	 */
	private Passengers getHighestPassengersGoingDown() {
		Passengers highestPass = floors[1].peekAtDownQueue();
		for (int i = 1; i < floors.length; i++) {
			if (floors[i].peekAtDownQueue() != null) {
				highestPass = floors[i].peekAtDownQueue();
			}
		}
		return highestPass;
	}

	/**
	 * Checks for up calls.
	 *
	 * @return true, if successful
	 */
	private boolean hasUpCalls() {
		for (int i = 0; i < floors.length - 1; i++) {
			if (lift.getCurrFloor() != i && floors[i].getUpQueueSize() > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if is end sim.
	 *
	 * @param time the time
	 * @return true, if is end sim
	 */
	public boolean isEndSim(int time) {
		for (int i = 0; i < floors.length; i++) {
			if (floors[i].getDownQueueSize() > 0 || floors[i].getUpQueueSize() > 0) {
				return false;
			}
		}

		if (passengers.peek() == null && lift.getCurrState() == STOP
				&& delayTime == 0 && passengersCurrentlyInElevator.size() == 0) {
			LOGGER.info("Time="+time+" Prev State: " + printState(lift.getPrevState()) + " Curr State: " + "STOP"
					+" PrevFloor: "+(lift.getPrevFloor()+1) + " CurrFloor: " + (lift.getCurrFloor()+1));
			LOGGER.info("Time="+(time+1)+" Detected End of Simulation");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Process passenger data.
	 *
	 * @param passDataFile the pass data file
	 */
	public void processPassengerData(String passDataFile) {
		
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(passDataFile.replaceAll(".csv", "") + "passData.csv")));
			out.println("ID,Number,From,To,WaitToBoard,TotalTime");
			for (Passengers p : passengersThatArrived) {
				String str = p.getID()+","+p.getNumber()+","+p.getFromFloor()+","+p.getToFloor()+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime());
				out.println(str);
			}
			for (Passengers p : passengersThatGaveUp) {
				String str = p.getID()+","+p.getNumber()+","+p.getFromFloor()+","+p.getToFloor()+","+
				             p.getWaitTime()+",-1";
				out.println(str);
			}
			out.flush();
			out.close();
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}


	/**
	 * Checks for down calls.
	 *
	 * @return true, if successful
	 */
	private boolean hasDownCalls() {
		for (int i = 1; i < floors.length; i++) {
			if (lift.getCurrFloor() != i && floors[i].getDownQueueSize() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Board elevator.
	 *
	 * @param time the time
	 * @return true, if successful
	 */
	private void boardElevator(int time) {
		
		if (lift.getDirection() == 1) {
			while (countNumPass() != lift.getCapacity() && floors[lift.getCurrFloor()].getUpQueueSize() > 0) {
				Passengers head = floors[lift.getCurrFloor()].peekAtUpQueue();

				if (time - head.getTime() > head.getGiveUpTime()) {
					LOGGER.info("Time="+time+" GaveUp="+head.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + head.getID());
					passengersThatGaveUp.add(floors[lift.getCurrFloor()].removeFromUpQueue());
				} else if (countNumPass() + head.getNumber() > lift.getCapacity()) {
					LOGGER.info("Time="+time+" Skip="+head.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + head.getID());
					break;
				} else {
					numBoarded += head.getNumber();
					head.setBoardTime(time);
					LOGGER.info("Time="+time+" Board="+head.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + head.getID());
					passengersCurrentlyInElevator.add(floors[lift.getCurrFloor()].removeFromUpQueue());
				}
			}
		} else {
			while (countNumPass() != lift.getCapacity() && floors[lift.getCurrFloor()].getDownQueueSize() > 0) {
				Passengers head = floors[lift.getCurrFloor()].peekAtDownQueue();

				if (time - head.getTime() > head.getGiveUpTime()) {
					LOGGER.info("Time="+time+" GaveUp="+head.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + head.getID());
					passengersThatGaveUp.add(floors[lift.getCurrFloor()].removeFromDownQueue());
				} else if (countNumPass() + head.getNumber() > lift.getCapacity()) {
					LOGGER.info("Time="+time+" Skip="+head.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + head.getID());
					break;
				} else {
					numBoarded += head.getNumber();
					head.setBoardTime(time);
					LOGGER.info("Time="+time+" Board="+head.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + head.getID());
					passengersCurrentlyInElevator.add(floors[lift.getCurrFloor()].removeFromDownQueue());
				}
			}
		}
		delayTime = lift.calculateDelayTime(numBoarded);
	}

	/**
	 * Count num pass.
	 *
	 * @return the int
	 */
	private int countNumPass() {
		int count = 0;
		for (int i = 0; i < passengersCurrentlyInElevator.size(); i++) {
			count += passengersCurrentlyInElevator.get(i).getNumber();
		}
		return count;
	}

	/**
	 * Check if anyone needs to exit on current floor.
	 *
	 * @return true, if successful
	 */
	private boolean checkIfAnyoneNeedsToExitOnCurrentFloor() {
		for (int i = 0; i < passengersCurrentlyInElevator.size(); i++) {
			if (passengersCurrentlyInElevator.get(i).getToFloor() == lift.getCurrFloor()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check for calls in curr dir.
	 *
	 * @return true, if successful
	 */
	private boolean checkForCallsInCurrDir() {

		if (lift.getDirection() == 1) {
			for (int i = lift.getCurrFloor() + 1; i < floors.length; i++) {
				if (floors[i].getDownQueueSize() > 0 || floors[i].getUpQueueSize() > 0) {
					return true;
				}
			}
		} else {
			for (int i = lift.getCurrFloor() - 1; i >= 0; i--) {
				if (floors[i].getDownQueueSize() > 0 || floors[i].getUpQueueSize() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Offload group.
	 *
	 * @param time the time
	 */
	private void offloadGroup(int time) {
		int numPassExited = 0;
		for (int i = passengersCurrentlyInElevator.size() - 1; i >= 0; i--) {
			if (passengersCurrentlyInElevator.get(i).getToFloor() == lift.getCurrFloor()) {
				numPassExited += passengersCurrentlyInElevator.get(i).getNumber();
				LOGGER.info("Time="+time+" Arrived="+passengersCurrentlyInElevator.get(i).getNumber()+" Floor="+ (lift.getCurrFloor()+1)
						+" passID=" + passengersCurrentlyInElevator.get(i).getID());
				passengersCurrentlyInElevator.get(i).setTimeOfArrival(time);
				passengersThatArrived.add(passengersCurrentlyInElevator.get(i));
				passengersCurrentlyInElevator.remove(i);
			}
		}
		delayTime = lift.calculateDelayTime(numPassExited);
	}

	/**
	 * Determine dir of elevator.
	 *
	 * @param newFloor the new floor
	 * @return the int
	 */
	private int determineDirOfElevator(int newFloor) {
		return (lift.getCurrFloor() > newFloor) ? -1:1;
	}

	/**
	 * Check dir of pass.
	 *
	 * @param p the p
	 * @return the int
	 */
	private int checkDirOfPass(Passengers p) {
		return (p.getFromFloor() > p.getToFloor()) ? -1:1;
	}

	/**
	 * Adds the passengers to queue.
	 *
	 * @param p the p
	 */
	public void addPassengersToQueue(Passengers p) {
		passengers.add(p);
	}

	/**
	 * Gets the passengers.
	 *
	 * @return the passengers
	 */
	public String[][] getPassengers() {
		String[][] queuesToStringArray = new String[floors.length][2];
		for (int i = 0; i < floors.length; i++) {
			queuesToStringArray[i][0] = floors[i].getStringOfUpQueue();
			queuesToStringArray[i][1] = floors[i].getStringOfDownQueue();
		}
		return queuesToStringArray;
	}

	/**
	 * Check passengers queue.
	 *
	 * @return the passengers
	 */
	public Passengers checkPassengersQueue() {
		return passengers.peek();
	}

	/**
	 * Gets the floors.
	 *
	 * @return the floors
	 */
	public Floor[] getFloors() {
		return floors;
	}

	/**
	 * Request curr floor.
	 *
	 * @return the int
	 */
	public int requestCurrFloor() {
		return lift.getCurrFloor();
	}

	/**
	 * Gets the pass string.
	 *
	 * @return the pass string
	 */
	// Come back to allow for encapsulation
	public String getPassString() {
		return passengers.toString();
	}

	/**
	 * Gets the num floors.
	 *
	 * @return the num floors
	 */
	public int getNumFloors() {
		return NUM_FLOORS;
	}

	/**
	 * Gets the num elevators.
	 *
	 * @return the num elevators
	 */
	public int getNumElevators() {
		return NUM_ELEVATORS;
	}

	/**
	 * Request curr state.
	 *
	 * @return the int
	 */
	public int requestCurrState() {
		return lift.getCurrState();
	}

	/**
	 * Gets the pass in elevator.
	 *
	 * @return the pass in elevator
	 */
	public int getPassInElevator() {
		int num = 0;
		for (int i = 0; i < passengersCurrentlyInElevator.size(); i++) {
			num += passengersCurrentlyInElevator.get(i).getNumber();
		}
		return num;
	}

	/**
	 * Enable logging.
	 */
	public void enableLogging() {
		// need to pass this along to both the elevator and floor classes...
		LOGGER.setLevel(Level.INFO);
		this.logElevatorConfig();
	}
	
	/**
	 * Gets the direction of elevator.
	 *
	 * @return the direction of elevator
	 */
	public int getDirectionOfElevator() {
		return lift.getDirection();
	}

	/**
	 * Checks if is floor changed.
	 *
	 * @return true, if is floor changed
	 */
	public boolean isFloorChanged() {
		return lift.isFloorChanged();
	}
}
