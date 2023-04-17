package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private final String name;
    private final String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private Vector<Model> modeles;
    private Vector<Model> readConff;

    public Student(String name, String department, Degree status){
        this.name=name;
        this.department=department;
        this.status=status;
        this.publications=0;
        this.papersRead=0;
        readConff=new Vector<Model>();
        modeles=new Vector<Model>();
    }

    public void addModle(Model modle){
        this.modeles.add(modle);
    }
    public Vector<Model> getModels(){return modeles;}
    public String getName() {return this.name;}
    public String getdDepartment() {return this.department;}
    public Degree getStatus() {return this.status;}
    public int getPublications() {return this.publications;}
    public void addPublications() {publications=publications+1;}
    public int getPapersRead() {return this.readConff.size();}
    public void addReadConff(Model model){readConff.add(model);}
    public Vector<Model> getReadConff(){return readConff;}


}
