package bgu.spl.mics;

import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//TODO check: why the comp doesnt like sync a non-final object?
	private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> MSMsgs;
	private final ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventsToMs;
	private final ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastsToMs;
	private final ConcurrentHashMap<Message, Future> msgAndFuture;
	private final Object lockBroadcast = new Object();
	private final Object lockEvent = new Object();
	private final Object lockSendEvent = new Object();


	private static class MessageBusImplHolder{
		private static MessageBusImpl instance=new MessageBusImpl();
	}

	private MessageBusImpl(){
		MSMsgs = new ConcurrentHashMap<>();
		eventsToMs = new ConcurrentHashMap<>();
		broadcastsToMs = new ConcurrentHashMap<>();
		msgAndFuture = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return MessageBusImplHolder.instance;
	}

	/**
	 * @pre: none
     * @post: isSubscribedToEvent(@param type, @param m) == true     
     */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (lockEvent) {
			if(!eventsToMs.containsKey(type))
				eventsToMs.put(type,new LinkedBlockingQueue<MicroService>());

		}

		try {
			eventsToMs.get(type).put(m);
		} catch (InterruptedException ie) {
		}

	}

	@Override
	/**
	 * @pre: none
	 * @post: isSubscribedToBroadcast(@param type, @param m) == true
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (lockBroadcast) {
			if(broadcastsToMs.get(type)==null)
				broadcastsToMs.put(type,new LinkedBlockingQueue<MicroService>());
		}
		try {
			broadcastsToMs.get(type).put(m);
		} catch (InterruptedException ie) {}
	}
	
	@Override
	/**
	 * @pre: none
	 * @post: getFuture(@param e).get() == @param result
	 */
	public <T> void complete(Event<T> e, T result) {
		msgAndFuture.get(e).resolve(result);
		msgAndFuture.remove(e);
	}

	@Override
	/**
	 * @pre: none
	 * @post: for each microServise m (isSubscribedToBroadcast(@param b.getClass(), m) == true)
	 *        b.equals(awaitMessage(m))
	 */
	public void sendBroadcast(Broadcast b) {
		LinkedBlockingQueue<MicroService> queue;
		synchronized (lockBroadcast) {
			queue = broadcastsToMs.get(b.getClass());
			if (queue != null && !queue.isEmpty()) {
				for( MicroService microService : queue){
					if (MSMsgs.containsKey(microService)) MSMsgs.get(microService).add(b);
				}
			}
		}


	}

	
	@Override
	/**
	 * @pre: none
	 * @post: for each microServise m (isSubscribedToEvent(@param e.getClass(), m) == true)
	 *         e.equals(awaitMessage(m))
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		LinkedBlockingQueue<MicroService> queue;
		MicroService m = null;
		synchronized (lockSendEvent) {
			queue = eventsToMs.get(e.getClass());
			if (queue != null) {
				m = queue.poll();
				if (m != null) {
					try {
						queue.put(m);
					} catch (InterruptedException ie) {}
					MSMsgs.get(m).add(e);
					Future<T> future=new Future<T>();
					msgAndFuture.put(e,future);
					return future;
				}
			}
		}
		return null;
	}

	@Override
	/**
	 * @pre: isRegistered(@param m) == false
	 * @post: isRegistered(@param m) == true
	 */
	public void register(MicroService m) {
		MSMsgs.put(m,new LinkedBlockingQueue<Message>());
	}

	@Override
	/**
	 * @pre: isRegistered(@ param m) == true
	 * @post: isRegistered(@param m) == false
	 */
	public void unregister(MicroService m) {
		MSMsgs.remove(m);
		synchronized (lockBroadcast) {
			synchronized (lockEvent) {
				broadcastsToMs.forEach((className, MSqueue) ->{
					if(MSqueue.contains(m)) MSqueue.remove(m);
				});
				eventsToMs.forEach((className, MSqueue) ->{
					if(MSqueue.contains(m)) MSqueue.remove(m);
				});
			}
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		LinkedBlockingQueue<Message> queue = MSMsgs.get(m);
		return queue.take();
	}


	public  <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
		return eventsToMs.get(type).contains(m);
	}
	public boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return broadcastsToMs.get(type).contains(m);
	}

	public <T> Future<T> getFuture(Event<T> e) {
		return msgAndFuture.get(e);
	}

	public boolean isRegistered(MicroService m) {
		return MSMsgs.containsKey(m);
	}


}
