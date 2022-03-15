package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private int TicksPerData;
    private GPU gpu;
    private int start_index;
    private Data.Type type;

    public DataBatch(int start, GPU gpu, int Ticks, Data.Type type){
        this.start_index=start;
        this.gpu= gpu;
        this.TicksPerData=Ticks;
        this.type=type;
    }


    public GPU getGpu(){
        return gpu;
    }

    public int getTicks(){
        return TicksPerData;
    }

    public Data.Type getType(){
        return type;
    }

}

