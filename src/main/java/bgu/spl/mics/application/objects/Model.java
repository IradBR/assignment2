package bgu.spl.mics.application.objects;



/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum Status {
        PreTrained, Trained, Tested
    }

    public enum Result {
        None, Good, Bad
    }

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;


    public Model(String name, String type, int size){
        this.name=name;
        this.data= new Data(type,size);
        this.student=null;
        this.status=Status.PreTrained;
        this.result=Result.None;
    }


    public Status getStatus(){return status;}

    public void setStatus(Status status) {
       this.status=status;
    }

    public Student getStudent() {return student; }

    public void setStudent(Student student) { this.student = student;}

    public Data getData() { return data;}
    public void setData(Data data) {this.data = data;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}


    public void setResult(Result result) {
         this.result = result;
    }
    public Result getResult(){
        return result;
    }

    public String getStringStatus(){
        if (status.equals(Model.Status.PreTrained))
            return "PreTrained";
        else if (status.equals(Model.Status.Trained))
            return "Trained";
        else
            return "Tested";
    }

    public String getStringResult(){
        if (result.equals(Result.Good))
            return "Good";
        if (result.equals(Result.Bad))
            return "Bad";
        else
            return "None";
    }

}