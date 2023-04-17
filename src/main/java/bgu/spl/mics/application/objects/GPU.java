package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import java.util.Math;
/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private BlockingQueue<DataBatch> procesed;
    private int totalTickPassed;
    private int capacity;
    private long processingTime;
    private long ticksPassed;
    private int totalBatchesProcessed;

    public GPU (Type type, Cluster cluster) {
        this.type = type;
        this.cluster = Cluster.getInstance();
        this.model = null;
        ticksPassed = 0;
        totalTickPassed=0;
        totalBatchesProcessed = 0;
        if (type == Type.RTX3090) { capacity = 32; processingTime = 1; }
        else if (type == Type.RTX2080) { capacity = 16; processingTime = 2; }
        else { capacity = 8; processingTime = 4; }
        procesed = new ArrayBlockingQueue<>(capacity);
    }


    public Model getModel() {
        return this.model;
    }

    public Type getType() {
        return this.type;
    }

    public int getTotalTickPassed(){ return totalTickPassed;}
    public long getProcessingTime() {
        return this.processingTime;
    }

    public int getTotalBatchesProcessed() {
        return totalBatchesProcessed;
    }

    public void sendForProcess(DataBatch batch) {
        cluster.sendForProcess(batch);
    }

    public void addBatch(DataBatch batch) {
        while(!procesed.contains(batch)) {
            try {
                procesed.put(batch);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                return;
            }
        }
    }

    public boolean trainModel() {
        if (!procesed.isEmpty()) {
            ticksPassed++;
            totalTickPassed++;
            if (ticksPassed == processingTime) {
                procesed.poll();
                model.getData().setProcessed(1);
                ticksPassed = 0;
                totalBatchesProcessed += 1;
                if ((model.getData().getNextBatchIndex())*1000 < (model.getData().getSize())) {
                    sendForProcess(new DataBatch(model.getData().getNextBatchIndex(), model.getData()));
                    model.getData().incNextBatchIndex();
                }
            }
            if (model.getData().getProcessed()*1000 == model.getData().getSize()) {
                model.setStatus(Model.Degree.Trained);
                return true;
            }
        }
        return false;
    }
    public void setModelForTraining(Model model) {
        this.model=model;
        this.model.setStatus(Model.Degree.Training);
        this.model.getData().setGpu(this);
        while (model.getData().getNextBatchIndex() < capacity-1) {
            DataBatch batch = new DataBatch(model.getData().getNextBatchIndex(), model.getData());
            sendForProcess(batch);
            model.getData().incNextBatchIndex();
        }
    }
    public void setModel(Model model) {
        this.model = model;
    }


    public Model.Result testModel(Model model){
        this.model=model;
        if(model.getStudent().getStatus()== Student.Degree.PhD){
            if(Math.random()>=0.2) this.model.setResult(Model.Result.Good);
            else this.model.setResult(Model.Result.Bad);
        } else {
            if(Math.random()>=0.4) this.model.setResult(Model.Result.Good);
            else this.model.setResult(Model.Result.Bad);
        }
        this.model.setStatus(Model.Degree.Tested);
        return model.getResult();

    }

}
