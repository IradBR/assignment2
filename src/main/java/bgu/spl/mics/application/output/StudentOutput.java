package bgu.spl.mics.application.output;

import java.util.LinkedList;

public class StudentOutput {
    private String name;
    private String department;
    private String status;
    private int publications;
    private int papersRead;
    private LinkedList<ModelsOutput> trainedModels;

    public StudentOutput (String name, String department, String status, int publications ,int papersRead, LinkedList<ModelsOutput> trainedModels)
    {
        this.name=name;
        this.department=department;
        this.status=status;
        this.publications=publications;
        this.papersRead=papersRead;
        this.trainedModels=trainedModels;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public LinkedList<ModelsOutput> getTrainedModels() {
        return trainedModels;
    }

    public void setTrainedModels(LinkedList<ModelsOutput> trainedModels) {
        this.trainedModels = trainedModels;
    }
}
