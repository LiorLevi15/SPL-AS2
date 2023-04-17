package bgu.spl.mics.application.objects;
import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private long tickCounter;
    private int totalTick;
    private DataBatch currentDB;
    private Vector<DataBatch> batches;
    private Cluster cluster;
    private long timeToProcess;
    private int totalBatchesProcessed;

    public CPU(int numOfCores) {
        this.cores=numOfCores;
        this.tickCounter=0;
        this.currentDB=null;
        batches=new Vector<DataBatch>();
        cluster = Cluster.getInstance();
        timeToProcess = 0;
        totalBatchesProcessed = 0;
        totalTick=0;
    }

    public void addBatch(DataBatch batch){
        batches.add(batch);
        if(batch.getData().getType() == Data.Type.Images)
            timeToProcess += 32/this.cores *4;
        else if (batch.getData().getType() == Data.Type.Text)
            timeToProcess += 32/this.cores *2;
        else
            timeToProcess += 32/this.cores;
    }
    public int getCores() {return this.cores; }
    public long getTimeToProcess(){
        return timeToProcess;
    }
    public int getTotalBatchesProcessed(){ return totalBatchesProcessed; }
    public int getTotalTick(){ return totalTick;}


    public boolean process() {
        if (currentDB == null & batches.isEmpty()) return false;
        currentDB = currentDB != null ? currentDB : batches.remove(0);
        tickCounter+=1;
        totalTick+=1;
        Data.Type type = currentDB.getData().getType();
        if ((type.equals(Data.Type.Images) && tickCounter == (32 / this.cores * 4)) |
                (type.equals(Data.Type.Text) && tickCounter == (32 / this.cores * 2)) |
                (type.equals(Data.Type.Tabular) && tickCounter == (32 / this.cores))) {

            cluster.addProcessed(currentDB);
            timeToProcess -= tickCounter;
            currentDB = batches.isEmpty() ? null : batches.remove(0);
            tickCounter = 0;
            totalBatchesProcessed += 1;
            return true;
        }

        return false;
    }

}
