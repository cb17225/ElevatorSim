// Owner: Matvey Volkov

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimController.
 */
public class ElevatorSimController {
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */
	private Building building;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/** The end sim. */
	private boolean endSim = false;
	
	/** The pass file name. */
	private String passFileName;
	
	/**
	 * Instantiates a new elevator sim controller.
	 *
	 * @param gui the gui
	 */
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		configSimulation("ElevatorSimConfig3.csv");
	}

	/**
	 * Config simulation.
	 *
	 * @param filename the filename
	 */
	private void configSimulation(String filename) {
		int numFloors = 0, numElevators = 0, capacity = 0, floorTicks = 0, doorTicks = 0, tickPassengers = 0;
		String passCSV = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");
				if (values[0].equals("numFloors")) {
					numFloors = Integer.parseInt(values[1]);
				} else if (values[0].equals("numElevators")) {
					numElevators = Integer.parseInt(values[1]);
				} else if (values[0].equals("capacity")) {
					capacity = Integer.parseInt(values[1]);
				} else if (values[0].equals("floorTicks")) {
					floorTicks = Integer.parseInt(values[1]);
				} else if (values[0].equals("doorTicks")) {
					doorTicks = Integer.parseInt(values[1]);
				} else if (values[0].equals("tickPassengers")) {
					tickPassengers = Integer.parseInt(values[1]);
				} else {
					passCSV = values[1];
				}
			}
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
		
		building = new Building(numFloors, numElevators, passCSV);
		building.configElevators(capacity, floorTicks, doorTicks, tickPassengers);
		initializePassengerData(passCSV);
		
		passFileName = passCSV;
	}
	
	/**
	 * Initialize passenger data.
	 *
	 * @param filename the filename
	 */
	private void initializePassengerData(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			int iteration = 0;
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");
				if (iteration++ == 0) {
					continue;
				} else {
					building.addPassengersToQueue(new Passengers(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
							  Integer.parseInt(values[2]) - 1, Integer.parseInt(values[3]) - 1, 
							  Boolean.parseBoolean(values[4]), Integer.parseInt(values[5])));
				}
			}
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Step sim.
	 */
	public void stepSim() {
		stepCnt++;
		// need to check to see if passengers should show up on floors
		// then updateElevator...
		// and update the GUI...
		
		endSim = building.isEndSim(stepCnt);
		
		if (!endSim) {
			building.updateElevator(stepCnt);
		} else {
			building.processPassengerData(passFileName);
		}
		
		gui.updateSim(building.getPassInElevator(),
					  building.getPassengers(), 
				      building.requestCurrFloor(),
				      building.requestCurrState());
	} 

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public int getTime() {
		return stepCnt;
	}

	/**
	 * Checks if is end sim.
	 *
	 * @return true, if is end sim
	 */
	public boolean isEndSim() {
		return endSim;
	}

	/**
	 * Gets the num floors.
	 *
	 * @return the num floors
	 */
	public int getNumFloors() {
		return building.getNumFloors();
	}

	/**
	 * Gets the num elevators.
	 *
	 * @return the num elevators
	 */
	public int getNumElevators() {
		return building.getNumElevators();
	}
	
	/**
	 * Enable logging.
	 */
	public void enableLogging() {
		building.enableLogging();
	}
	
	/**
	 * Request direction of elevator.
	 *
	 * @return the int
	 */
	public int requestDirectionOfElevator() {
		return building.getDirectionOfElevator();
	}
	
	/**
	 * Request is floor changed.
	 *
	 * @return true, if successful
	 */
	public boolean requestIsFloorChanged() {
		return building.isFloorChanged();
	}
}
