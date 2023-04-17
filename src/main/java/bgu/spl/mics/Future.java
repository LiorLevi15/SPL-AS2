package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private boolean isDone;
	private T result;
	
	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		isDone = false;
		result = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
	 * @pre: none
     * @post: isDone() == true       
     */
	public synchronized T get() {
		try {
			while(!isDone) {
				wait();
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			return result;
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
	 * @pre: isDone() == false
	 * @post: isDone() == true 
	 * @post: get() == @param result
     */
	public synchronized void resolve (T result) {
		this.result = result;
		isDone = true;
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public synchronized boolean isDone() {
		return isDone;
	}
	

	public synchronized T get(long timeout, TimeUnit unit) {
			if (isDone) return result;
			else {
				try {
					wait(unit.toMillis(timeout));
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				if(isDone) {
					return result;
				} else return null;
			}
	}

}
