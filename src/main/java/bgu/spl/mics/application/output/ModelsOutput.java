package bgu.spl.mics.application.output;

public class ModelsOutput {
    private String name;
    private dataOutput data;
    private String status;
    private String results;

    public ModelsOutput (String name, dataOutput data, String status, String results)
    {
        this.name=name;
        this.data=data;
        this.status=status;
        this.results=results;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public dataOutput getData() {
        return data;
    }

    public void setData(dataOutput data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
