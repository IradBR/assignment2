package bgu.spl.mics.application.Input;

import bgu.spl.mics.application.Input.InputModel;

import java.util.LinkedList;

public class InputStudent {
   private String name;
   private String department;
   private String status;
   private LinkedList<InputModel> models;

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

    public LinkedList<InputModel> getModels() {
        return models;
    }

    public void setModels(LinkedList<InputModel> models) {
        this.models = models;
    }
}
