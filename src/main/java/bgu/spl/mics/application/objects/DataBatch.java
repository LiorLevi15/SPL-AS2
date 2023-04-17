package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    
    private int start_index;
    private Data data;

    public DataBatch(int start_index, Data data){
        this.start_index=start_index;
        this.data=data;
    }

    public Data getData() {
        return data;
    }

    public int start_index(){
        return start_index;
    }
}
