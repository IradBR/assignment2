package bgu.spl.mics;

import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {

	//each key in the map represented by different microservice and the value is a queue of the messages that this microservice can handle with.
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> register;
	//each key in the map represented by the class of an event or broadcast and the value is a queue of the microservices that subscribed to it.
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> message_MS;
	// each key in the map represented by event and the value is the Future object that bond to it.
	private ConcurrentHashMap<Event, Future> event_future;

	//singleton
	private static class MessageBusImplHolder{
		private static MessageBusImpl instance= new MessageBusImpl();
	}

	private MessageBusImpl(){
		register = new ConcurrentHashMap<>();
		message_MS = new ConcurrentHashMap<>();
		event_future = new ConcurrentHashMap<>();
	}

	public void cleanBus()
	{
		register = new ConcurrentHashMap<>();
		message_MS = new ConcurrentHashMap<>();
		event_future = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}


	/**
	 @pre: isRegister(m)==true
	 @post: isSubscribeEvent(type,m)==true
	 */

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
				message_MS.putIfAbsent(type, new ConcurrentLinkedQueue<>());
				message_MS.get(type).add(m);
		}


	/**
	 @pre: isRegister(m)==true
	 @post: isSubscribeBroadcast(type,m)==true
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
			message_MS.putIfAbsent(type, new ConcurrentLinkedQueue<>());
			message_MS.get(type).add(m);
	}
	/**
	 @pre: isComplete==false
	 @post: isComplete(e,result)==true
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		if(event_future.containsKey(e)) {
			event_future.get(e).resolve(result);
		}
	}

	/**
	 @pre: None
	 @post: SentBroadcast(b)==true
	 */

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (message_MS) {
			if (message_MS.get(b.getClass()) != null) {
				ConcurrentLinkedQueue<MicroService> MsToSend = message_MS.get(b.getClass());
				for (MicroService m : MsToSend) {
					register.get(m).add(b);
				}
			}
		}
	}

	/**
	 @pre: None
	 @post: SentEvent(e)==true
	 */

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (message_MS){
			if(message_MS.containsKey(e.getClass())){
				Future<T> future=new Future<T>();
				event_future.putIfAbsent(e,future);
				MicroService m= message_MS.get(e.getClass()).poll();
				if(m!=null) {
					register.get(m).add(e);
					message_MS.get(e.getClass()).add(m);
					return future;
				}
			}
		}
		return null;
	}

	/**
	 @pre: None
	 @post: isRegister(m)==true
	 */
	@Override
	public void register(MicroService m) {
		register.putIfAbsent(m,new LinkedBlockingQueue<>());
	}


	/**
	 @pre: isRegister(m)==true
	 @post: isUnRegister(m)==true
	 */
	@Override

	public void unregister(MicroService m) {
		if(isRegister(m)){
			synchronized (message_MS){
				for(Class<? extends Message> message: message_MS.keySet()){
					if(message_MS.get(message).contains(m)){
						message_MS.get(message).remove(m);
					}
				}
			}
			register.remove(m);
		}
	}

	/**
	 @pre: isRegister==true
	 @post: return message!=null
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (isUnRegister(m))
			throw new IllegalStateException("The Micro Service need to be register");
		Message message=null;
		try{
			message= register.get(m).take();
		}catch (IllegalStateException e){
			e.printStackTrace();}

		return message;
	}


	//queries
	public boolean isRegister (MicroService m)	{
		return register.containsKey(m);
	}
	//queries
	public boolean isUnRegister (MicroService m){
		return !register.containsKey(m);
	}

	//queries
	public <T> boolean isSubscribeEvent (Class<? extends Event<T>> type, MicroService m) {
		if(isRegister(m) && message_MS.containsKey(type)) {
			return message_MS.get(type).contains(m);
		}
		return false;
	}

	//queries
	public <T> boolean isSubscribeBroadcast (Class<? extends Broadcast> type, MicroService m) {
		if(isRegister(m) && message_MS.containsKey(type)) {
			return message_MS.get(type).contains(m);
		}
		return false;
	}

	//queries
	public boolean isSentBroadcast (Broadcast b) {
		if(message_MS.containsKey(b.getClass())){
			ConcurrentLinkedQueue<MicroService> MsToSend= message_MS.get(b.getClass());
			for (MicroService m:  MsToSend){
				if(!register.get(m).contains(b)){
					return false;
				}
			}
			return true;
		}
		else
			return false;
	}

	//queries
	public <T> boolean isComplete (Event<T> e, T result) {
		if(event_future.containsKey(e)){
			return (event_future.get(e)).isDone();
		}
		return false;
	}

	//queries

	public <T> boolean isSentEvent (Event<T> e) {
		return event_future.containsKey(e);



	}
}