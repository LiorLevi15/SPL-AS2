package bgu.spl.mics.application.objects;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private AtomicInteger processed;
    private AtomicInteger nextBatchIndex;
    private int size;
    private GPU gpu;
    
    public Data(Type type, int size){
        this.type=type;
        this.size=size;
        processed = new AtomicInteger(0);
        nextBatchIndex = new AtomicInteger(0);
    }

    public int getProcessed() {
        return processed.get();
    }

    public GPU getGpu() {
        return gpu;
    }
    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }

    public Type getType() {return this.type;}
    public int getSize() {return this.size;}
    public void incNextBatchIndex() {
        int oldVal;
        int newVal;
        do {
            oldVal = nextBatchIndex.get();
            newVal = oldVal+1;
        } while (!nextBatchIndex.compareAndSet(oldVal, newVal));
    }
    public int getNextBatchIndex() {
        return nextBatchIndex.get();
    }

    public void setProcessed(int amount) {
        int oldVal;
        int newVal;
        do {
            oldVal = processed.get();
            newVal = oldVal+amount;
        } while (!processed.compareAndSet(oldVal,newVal));
    }
}


