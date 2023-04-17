package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Vector;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private long ticksPassed;
    private Model currModel;
    private long timeUnitsUsed;
    private Vector<Model> ModelsWaitingForTest;
    private Vector<Model> ModelsWaitingFOrTrain;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu=gpu;
        currModel = null;
        ticksPassed = 0;
        timeUnitsUsed = 0;
        ModelsWaitingForTest = new Vector<>();
        ModelsWaitingFOrTrain = new Vector<>();
    }

    public GPU getGPU(){return gpu;}

    public long getTimeUnitsUsed() {
        return gpu.getTotalTickPassed();
    }
    public int getTotalBatchesProcessed() {
        return this.gpu.getTotalBatchesProcessed();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast b)-> { terminate(); });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast b)-> {
            if (currModel == null) {
                while (!ModelsWaitingForTest.isEmpty()) {
                    currModel = ModelsWaitingForTest.remove(0);
                    gpu.testModel(currModel);
                    sendBroadcast(new FinishTest(currModel));
                }
                currModel = null;
                if (!ModelsWaitingFOrTrain.isEmpty()) {
                    currModel = ModelsWaitingFOrTrain.remove(0);
                    gpu.setModelForTraining(currModel);
                }
            }
            else {
                if (gpu.trainModel()) {
                    sendBroadcast(new FinishTrain(gpu.getModel()));
                    currModel = null;
                    gpu.setModel(null);
                }
            }
        });
        subscribeEvent(TrainModelEvent.class, (TrainModelEvent e)-> {
            if (currModel == null) {
                currModel = e.getModel();
                gpu.setModelForTraining(currModel);
            }
            else {
                ModelsWaitingFOrTrain.add(e.getModel());
            }

        });
        subscribeEvent(TestModelEvent.class, (TestModelEvent e)-> {
            if (currModel == null) {
                Model.Result result = gpu.testModel(e.getModel());
                if(result == Model.Result.Good){
                    sendBroadcast(new FinishTest(e.getModel()));
                }
            }
            else {
                ModelsWaitingForTest.add(e.getModel());
            }
        });
    }
}
