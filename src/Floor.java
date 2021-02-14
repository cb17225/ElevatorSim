// Owner: Matvey Volkov

// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor.
 */
public class Floor {
	
	/** The up requests. */
	// add queues to track the up requests and down requests...
	private GenericQueue<Passengers> upRequests;

	/** The down requests. */
	private GenericQueue<Passengers> downRequests;

	/**
	 * Instantiates a new floor.
	 *
	 * @param qSize the q size
	 */
	public Floor(int qSize) {
		// add additional initialization here
		upRequests = new GenericQueue<Passengers>(qSize);
		downRequests = new GenericQueue<Passengers>(qSize);
	}

	/**
	 * Board elevator.
	 *
	 * @param state the state
	 * @return true, if successful
	 */
	public boolean boardElevator(int state) {
		// -1 -> going down
		// 1 -> going up
		return (state == 1) ? this.upRequests.poll() != null:this.downRequests.poll() != null;
	}

	/**
	 * Adds the request.
	 *
	 * @param direction the direction
	 * @param group the group
	 * @return true, if successful
	 */
	public boolean addRequest(int direction, Passengers group) {
		// -1 -> down
		// 1 -> up
		return (direction == 1) ? this.upRequests.offer(group):this.downRequests.offer(group);
	}

	/**
	 * Peek at up queue.
	 *
	 * @return the passengers
	 */
	public Passengers peekAtUpQueue() {
		return this.upRequests.peek();
	}

	/**
	 * Peek at down queue.
	 *
	 * @return the passengers
	 */
	public Passengers peekAtDownQueue() {
		return this.downRequests.peek();
	}
	
	/**
	 * Gets the up queue size.
	 *
	 * @return the up queue size
	 */
	public int getUpQueueSize() {
		return this.upRequests.getSize();
	}
	
	/**
	 * Gets the down queue size.
	 *
	 * @return the down queue size
	 */
	public int getDownQueueSize() {
		return this.downRequests.getSize();
	}
	
	/**
	 * Gets the string of down queue.
	 *
	 * @return the string of down queue
	 */
	public String getStringOfDownQueue() {
		return this.downRequests.toString();
	}
	
	/**
	 * Gets the string of up queue.
	 *
	 * @return the string of up queue
	 */
	public String getStringOfUpQueue() {
		return this.upRequests.toString();
	}
	
	/**
	 * Removes the from up queue.
	 *
	 * @return the passengers
	 */
	public Passengers removeFromUpQueue() {
		return this.upRequests.remove();
	}
	
	/**
	 * Removes the from down queue.
	 *
	 * @return the passengers
	 */
	public Passengers removeFromDownQueue() {
		return this.downRequests.remove();
	}

}
