package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

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
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		isDone=false;
		result=null;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 * @pre: None
	 * @post this.isDone()==true
	 */
	//method with wait/notify have to be synchronized
	public synchronized T get() {
		while (!isDone)
		{
			try{
				this.wait();
			}catch (InterruptedException ignored) {}
		}
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 * @pre: this.isDone()==false
	 * @pre: this.result==null
	 * @post: this.isDone()==true
	 * @post: this.result==result
	 */
	//method with wait/notify have to be synchronized
	public synchronized void resolve (T result) {
		isDone=true;
		this.result=result;
		this.notifyAll();
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	//queries
	public boolean isDone() {
		return isDone;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 * @pre: None
	 * @post this.isDone()==true
	 */
	//method with wait/notify have to be synchronized
	public synchronized T get(long timeout, TimeUnit unit) {
		try {
			if (!isDone) {
				wait(unit.toMillis(timeout));
			} else  // isDone - No need to wait
				return result;
		} catch(Exception ignored){
		}
		return result;
	}
}