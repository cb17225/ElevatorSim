// Owner: Matvey Volkov

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.lang.IllegalStateException;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericQueue.
 *
 * @param <E> the element type
 */
public class GenericQueue<E> {

	/** The Constant MAX_QUEUE_SIZE. */
	private final int MAX_QUEUE_SIZE;

	/** The queue. */
	private LinkedList<E> queue = new LinkedList<>();

	/**
	 * Instantiates a new generic queue.
	 */
	public GenericQueue() {		
		MAX_QUEUE_SIZE = 20;
	}
	
	/**
	 * Instantiates a new generic queue.
	 *
	 * @param queueSize the queue size
	 */
	public GenericQueue(int queueSize) {
		MAX_QUEUE_SIZE = queueSize;
	}

	/*
	 * Implement the following methods:
	 * 1) boolean isEmpty()
	 * 2) int getSize()
	 * 3) boolean add(E o)
	 * 4) boolean offer(E o)
	 * 5) E remove()
	 * 6) E poll()
	 * 7) E element()
	 * 8) E peek()
	 */

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return (queue.isEmpty());
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return (queue.size());
	}

	/**
	 * Adds the.
	 *
	 * @param o the o
	 * @return true, if successful
	 * @throws IllegalStateException the illegal state exception
	 */
	public boolean add(E o) throws IllegalStateException {
		if (queue.size() == MAX_QUEUE_SIZE) {
			throw new IllegalStateException();
		}
		queue.addLast(o);
		return true;
	}

	/**
	 * Offer.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean offer(E o) {
		if (queue.size() == MAX_QUEUE_SIZE) {
			return false;
		}
		queue.addLast(o);
		return true;
	}

	/**
	 * Removes the.
	 *
	 * @return the e
	 * @throws NoSuchElementException the no such element exception
	 */
	public E remove() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return(queue.removeFirst());
	}

	/**
	 * Poll.
	 *
	 * @return the e
	 */
	public E poll() {
		if (isEmpty()) {
			return null;
		}
		return(queue.removeFirst());
	}

	/**
	 * Element.
	 *
	 * @return the e
	 * @throws NoSuchElementException the no such element exception
	 */
	public E element() throws NoSuchElementException {	
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return(queue.getFirst());
	}

	/**
	 * Peek.
	 *
	 * @return the e
	 */
	public E peek() {	
		if (isEmpty()) {
			return null;
		}
		return(queue.getFirst());
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	// Do NOT TOUCH THIS!!
	@Override
	public String toString() {
		String str = "";
		ListIterator<E> list = queue.listIterator(0);
		if (list != null) {
			while (list.hasNext()) {
				str += ((Passengers) list.next()).getNumber();
				if (list.hasNext()) str += ",";
			}
		}
		return str;
	}
	
	/**
	 * Gets the list iterator.
	 *
	 * @return the list iterator
	 */
	public ListIterator<E> getListIterator() {
		return queue.listIterator(0);
	}

}
