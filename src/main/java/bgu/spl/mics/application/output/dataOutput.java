package bgu.spl.mics.application.output;

public class dataOutput {
    private String type;
    private int size;

    public dataOutput(String type, int size)
    {
        this.type=type;
        this.size=size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
