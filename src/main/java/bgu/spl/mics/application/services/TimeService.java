package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinishTrain;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import java.util.Timer;
import java.util.TimerTask;

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

	private long tickTime;
	private long duration;
	private long ticksPassed;
	private long time;

	public TimeService(int tickTime,int duration) {
		super("TimeService");
		this.tickTime=tickTime;
		this.duration=duration;
		ticksPassed = 0;
		time=0;
	}

	@Override
	protected void initialize() {
		Timer t = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				if(ticksPassed <= duration){
					ticksPassed++;
					sendBroadcast(new TickBroadcast());
					//if(ticksPassed%1000==0) System.out.println("tick: " + ticksPassed);

				}
				if(ticksPassed>=duration){
					sendBroadcast(new TerminateBroadcast());
					cancel();
					terminate();
				}
			};
		};
		t.scheduleAtFixedRate(tt,tickTime,tickTime);

		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast b)->{ terminate();});


	}

}


