package bgu.spl.mics.application.output;
import bgu.spl.mics.application.objects.*;
import java.util.LinkedList;

public class Output {
    private LinkedList<StudentOutput> studentsOutput;
    private LinkedList<conferencesOutput> conferences;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    public Output (LinkedList<Student>students, LinkedList<ConfrenceInformation>confrences, int cpuTimeUsed,int gpuTimeUsed,int batchesProcessed) {
        this.studentsOutput = new LinkedList<>();
        for (int i = 0; i < students.size(); i++) {
            Student currStudent = students.get(i);
            LinkedList<Model> models = currStudent.getProcessedModels();
            LinkedList<ModelsOutput> modelsTrained = new LinkedList<>();
            for (int j = 0; j < models.size(); j++) {
                if (models.get(j).getStatus().equals(Model.Status.Trained)||models.get(j).getStatus().equals(Model.Status.Tested))
                    modelsTrained.add(toModelOutput(models.get(j)));
            }
            StudentOutput studentOutput = new StudentOutput(currStudent.getName(), currStudent.getDepartment(), currStudent.getStringStatus(), currStudent.getPublications(), currStudent.getPapersRead(), modelsTrained);
            studentsOutput.add(studentOutput);
        }

        this.conferences = new LinkedList<>();
        for (int i = 0; i < confrences.size(); i++) {
            ConfrenceInformation currconf = confrences.get(i);
            LinkedList<Model> models = currconf.getModels();
            LinkedList<ModelsOutput> modelsOutput = new LinkedList<>();
            for (int j = 0; j < models.size(); j++) {
                modelsOutput.add(toModelOutput(models.get(j)));
            }
            conferencesOutput conferencesOutput = new conferencesOutput(currconf.getName(), currconf.getDate(),modelsOutput);
            this.conferences.add(conferencesOutput);
        }
        this.cpuTimeUsed=cpuTimeUsed;
        this.gpuTimeUsed=gpuTimeUsed;
        this.batchesProcessed=batchesProcessed;
    }

    public ModelsOutput toModelOutput(Model currModel)
    {
        dataOutput currData = new dataOutput(currModel.getData().getStringType(),currModel.getData().getSize());
        ModelsOutput modelsOutput = new ModelsOutput(currModel.getName(),currData,currModel.getStringStatus(),currModel.getStringResult());
        return modelsOutput;
    }

    public LinkedList<StudentOutput> getStudentsOutput() {
        return studentsOutput;
    }

    public void setStudentsOutput(LinkedList<StudentOutput> studentsOutput) {
        this.studentsOutput = studentsOutput;
    }

    public LinkedList<conferencesOutput> getConferences() {
        return conferences;
    }

    public void setConferences(LinkedList<conferencesOutput> conferences) {
        this.conferences = conferences;
    }

    public int getCpuTimeUsed() {
        return cpuTimeUsed;
    }

    public void setCpuTimeUsed(int cpuTimeUsed) {
        this.cpuTimeUsed = cpuTimeUsed;
    }

    public int getGpuTimeUsed() {
        return gpuTimeUsed;
    }

    public void setGpuTimeUsed(int gpuTimeUsed) {
        this.gpuTimeUsed = gpuTimeUsed;
    }

    public int getBatchesProcessed() {
        return batchesProcessed;
    }

    public void setBatchesProcessed(int batchesProcessed) {
        this.batchesProcessed = batchesProcessed;
    }
}
