package bgu.spl.mics.application.objects;
import java.util.LinkedList;

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

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private LinkedList<Model> modelsToProcess;
    private LinkedList<Model> ProcessedModels;

    public Student (String name,String department, String status, LinkedList <Model> models ){
       this.name=name;
        this.department=department;
        this.publications=0;
        this.papersRead=0;
        if(status.equals("MSc"))
            this.status=Degree.MSc;
        else this.status=Degree.PhD;
        this.modelsToProcess=models;
        this.ProcessedModels=new LinkedList<>();



    }

    public LinkedList<Model> getModelsToProcess(){
        return modelsToProcess;
    }
    public LinkedList<Model> getProcessedModels(){
        return ProcessedModels;
    }

    public void readConferencePublish(LinkedList<Model> models){
        for(Model model: models){
            if(model.getStudent()==this){
                publications=publications+1;
            }
            else papersRead=papersRead+1;
        }
    }

    public String getStringStatus()
    {
        if (status.equals(Degree.MSc))
            return "MSc";
        return "PhD";
    }
    public Degree getStatus(){
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStatus(Degree status) {
        this.status = status;
    }

    public int getPublications() {
        return publications;
    }

    public void setPublications(int publications) {
        this.publications = publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }

}
