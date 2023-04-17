package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling the {@link }.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private final CPU cpu;
    public CPUService(String name,CPU cpu) {
        super(name);
        this.cpu=cpu;
    }

    public CPU getCPU(){return cpu;}
    public long getTotalTime(){return cpu.getTotalTick();}
    public int getTotalBatchesProcessed() {
        return this.cpu.getTotalBatchesProcessed();
    }
    @Override
    protected void initialize() {

        Callback<TickBroadcast> tickBroadcastCallback= (TickBroadcast c)->{cpu.process();};
        Callback<TerminateBroadcast> terminateBroadCastCallback = (TerminateBroadcast c)->{
            Thread.currentThread().interrupt();
            this.terminate();
            Thread.currentThread().interrupt();
        };

        subscribeBroadcast(TickBroadcast.class,tickBroadcastCallback);
        subscribeBroadcast(TerminateBroadcast.class,terminateBroadCastCallback);

    }
}

