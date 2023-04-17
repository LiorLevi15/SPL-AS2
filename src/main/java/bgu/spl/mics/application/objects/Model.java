package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    private String name;
    private Data data;
    private Student student;
    public enum Degree {
        PreTrained, Training, Trained, Tested, Posted
    }
    public Degree status;
    public enum Result {
        None, Good, Bad
    }
    public Result result;

    public Model(String name, Data data, Student student){
        this.name=name;
        this.data=data;
        this.student=student;
        this.status=Degree.PreTrained;
        this.result=Result.None;

    }

    public String getName() {return this.name;}
    public Data getData() {return this.data;}
    public Student getStudent() {return this.student;}

    public Degree getStatus() {
        synchronized(this.status){
            return status;
        }
        
    } 

    public Result getResult() {
        synchronized(this.result){
            return this.result;
        }
        
    } 

    public void setStatus(Degree status) {
        synchronized(this.status){
            this.status=status;
        }
        
    } 
    public void setResult(Result result) {
        synchronized(this.result){
            this.result=result;
        }
        
    } 

}



