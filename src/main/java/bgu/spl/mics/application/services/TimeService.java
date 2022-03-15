package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TimeUpBroadCast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int TickTime; //milliseconds
	private int Duration; //Ticks
	private int currTime; //Ticks
	private Timer timer;
	private TimerTask task;

	public TimeService(int TickTime, int Duration) {
		super("TimeService");
		this.TickTime=TickTime;
		this.Duration=Duration;
		this.currTime=1;
		this.timer = new Timer();
		this.task = new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast());
				currTime=currTime+1;
				if  (currTime>=Duration) {
					sendBroadcast(new TimeUpBroadCast());
					timer.cancel();
				}
			}
		};

	}

	@Override
	protected void initialize() {
		//don't need to subscribe any event or broadcast
		//need to send Broadcast every Tick

		try{
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timer.scheduleAtFixedRate(task,0,TickTime);

		subscribeBroadcast(TimeUpBroadCast.class, (TimeUpBroadCast broad)->{
			terminate();
		});

	}
}