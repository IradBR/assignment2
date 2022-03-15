package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.Input.Input;
import bgu.spl.mics.application.Input.InputConfrence;
import bgu.spl.mics.application.Input.InputModel;
import bgu.spl.mics.application.Input.InputStudent;
import bgu.spl.mics.application.output.Output;
import bgu.spl.mics.application.messages.Studentactivator;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        Gson gson = new Gson();
        Input input = null;
        try (Reader reader = new FileReader(args[0])) {
            input = gson.fromJson(reader, Input.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input != null;

        //Turn the input to student
        LinkedList<Student> students = new LinkedList<>();
        int studentSize = input.getStudents().length;
        for (int i = 0; i < studentSize; i++) {
            InputStudent inputStudent = input.getStudents()[i];
            LinkedList<Model> models = new LinkedList<>();
            int size = (inputStudent.getModels()).size();
            for (int j = 0; j < size; j++) {
                InputModel inputModel = inputStudent.getModels().get(j);
                Model model = new Model(inputModel.getName(), inputModel.getType(), inputModel.getSize());
                models.add(model);
            }
            Student student = new Student(inputStudent.getName(), inputStudent.getDepartment(), inputStudent.getStatus(), models);
            for(Model m: student.getModelsToProcess()){
                m.setStudent(student);
            }
            students.add(student);
        }

        //Turn the input to GPU
        String[] gpuInput = input.getGPUS();
        LinkedList<GPU> gpus = new LinkedList<>();
        for (int i = 0; i < gpuInput.length; i++) {
            GPU gpu = new GPU(gpuInput[i]);
            gpus.add(gpu);
        }

        //Turn the input to CPU
        int[] cpuInput = input.getCPUS();
        LinkedList<CPU> cpus = new LinkedList<>();
        for (int i = 0; i < cpuInput.length; i++) {
            CPU cpu = new CPU(cpuInput[i]);
            cpus.add(cpu);
        }

        //Turn the input to Conferences
        InputConfrence[] ConferencesInput = input.getConferences();
        LinkedList<ConfrenceInformation> Conferences = new LinkedList<>();
        for (int i = 0; i < ConferencesInput.length; i++) {
            ConfrenceInformation Conference = new ConfrenceInformation(ConferencesInput[i].getName(), ConferencesInput[i].getDate());
            Conferences.add(Conference);
        }



        //Student Threads
        LinkedList<Thread> StudentThreads = new LinkedList<>();
        for (int i = 0; i < students.size(); i++) {
            StudentService studentService = new StudentService("Student Service - " + students.get(i).getName(), students.get(i));
            Thread studentThread = new Thread(studentService);
            StudentThreads.add(studentThread);
        }

        //GPU Threads
        LinkedList<Thread> GPUThreads = new LinkedList<>();
        for (int i = 0; i < gpus.size(); i++) {
            GPUService GPUService = new GPUService("GPU Service " + i, gpus.get(i));
            Thread GPUThread = new Thread(GPUService);
            GPUThreads.add(GPUThread);
        }

        //CPU Threads
        LinkedList<Thread> CPUThreads = new LinkedList<>();
        for (int i = 0; i < cpus.size(); i++) {
            CPUService CPUService = new CPUService("CPU Service " + i, cpus.get(i));
            Thread CPUThread = new Thread(CPUService);
            CPUThreads.add(CPUThread);
        }

        //Conferences Threads
        LinkedList<Thread> ConferencesThreads = new LinkedList<>();
        for (int i = 0; i < Conferences.size(); i++) {
            ConferenceService conferenceService = new ConferenceService("Conference Service " + i, Conferences.get(i));
            Thread ConferencesThread = new Thread(conferenceService);
            ConferencesThreads.add(ConferencesThread);
        }

        Cluster cluster = Cluster.getInstance();
        cluster.setCpus(cpus);
        cluster.setGpus(gpus);

        //Time Thread
        TimeService timeSrevice = new TimeService(input.getTickTime(), input.getDuration());
        Thread TimeThread = new Thread(timeSrevice);

        //start all the threads
        for (int i = 0; i < GPUThreads.size(); i++) {
            GPUThreads.get(i).setName("ThreadGPU " + i);
            GPUThreads.get(i).start();
        }
        for (int i = 0; i < CPUThreads.size(); i++) {
            CPUThreads.get(i).setName("ThreadCPU " + i);
            CPUThreads.get(i).start();
        }
        for (int i = 0; i < ConferencesThreads.size(); i++) {
            ConferencesThreads.get(i).setName("ConferencesThreads " + i);
            ConferencesThreads.get(i).start();
        }
        TimeThread.setName("TimeThread");
        TimeThread.start();

        for (int i = 0; i < StudentThreads.size(); i++) {
            StudentThreads.get(i).setName("StudentThreads " + i);
            StudentThreads.get(i).start();
        }





//        MessageBusImpl messageBus = MessageBusImpl.getInstance();
//        for (int i=0; i<students.size();i++)
//            messageBus.sendBroadcast(new Studentactivator(students.get(i)));

        try {
            //join all the threads
            for (int i = 0; i < StudentThreads.size(); i++)
                StudentThreads.get(i).join();
            for (int i = 0; i < GPUThreads.size(); i++)
                GPUThreads.get(i).join();
            for (int i = 0; i < CPUThreads.size(); i++)
                CPUThreads.get(i).join();
            for (int i = 0; i < ConferencesThreads.size(); i++)
                ConferencesThreads.get(i).join();
            TimeThread.join();
        } catch (InterruptedException ex) {
        }

        int cpusWorkingTime = cluster.getCpusWorkingTime().intValue();
        int gpuWorkingTime = cluster.getGpuWorkingTime().intValue();
        int cpuNumberOfProcessData = cluster.getcpuNumberOfProcessData().intValue();


        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer=new FileWriter("MyOutput.json" )) {
            Output output = new Output(students, Conferences, cpusWorkingTime, gpuWorkingTime, cpuNumberOfProcessData);
            gson2.toJson(output, writer);
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}