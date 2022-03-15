package bgu.spl.mics.application.objects;

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
    private int size;
    private int processed;


    public Data(String type, int size){
        if((type.equals("Images"))|(type.equals("images")))
            this.type=Type.Images;
        else if(type.equals("Tabular")||type.equals("tabular"))
            this.type=Type.Tabular;
        else this.type=Type.Text;
        this.size=size;
        this.processed=0;
    }

    public void increaseProcessData(){
        processed=processed+1000;

    }

    public boolean isDoneProcessing(){
        return processed>=size;
    }

    public int numOfTicks(){
        if( type==Type.Images) return 4;
        else if( type==Type.Text) return 2;
        else return 1;

    }
    public String getStringType() {
        if(type==Type.Images) return "Images";
        else if( type==Type.Text) return "Text";
        else return "Tabular";
    }
    public int getProcessed(){
        return processed;
    }

    public int getSize(){
        return  size;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setSize(int size) {
        this.size = size;
    }
}