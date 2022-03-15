package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;
import java.util.Vector;


/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student student;
    private int currTime;
    private Future future;

    public StudentService(String ms_name,Student student) {
        super(ms_name);
        this.student=student;
        this.currTime=1;
        this.future=null;

    }

    @Override
    protected void initialize() {

       subscribeBroadcast(PublishConferenceBroadCast.class, (PublishConferenceBroadCast broad)->{
           LinkedList<Model> publishedModels= broad.getModelsToPublish();
           student.readConferencePublish(publishedModels);
       });

        subscribeBroadcast(Studentactivator.class, (Studentactivator broad)->{
            Student student= broad.getStudent();
            if(student==this.student){
                LinkedList<Model> modelsToProcess= student.getModelsToProcess();
                LinkedList<Model> ProcessedModels= student.getProcessedModels();
                if(modelsToProcess.size()!=0) {
                    Model model = modelsToProcess.remove();
                    Future<Boolean> trainFuture = sendEvent(new TrainModelEvent(model));
                    if (trainFuture != null) {
                        Boolean trainResult = trainFuture.get();
                        if (trainResult != null) {
                            ProcessedModels.add(model);
                            Future<Boolean> testFuture = sendEvent(new TestModelEvent(model));
                            if (testFuture != null) {
                                Boolean testResult = testFuture.get();
                                if (testResult != null) {
                                    Future<Boolean> publishResult = sendEvent(new PublishResultsEvent(model));
                                    if (publishResult != null) {
                                        publishResult.get();
                                    }
                                    sendBroadcast(new Studentactivator(this.student));
                                }
                            }
                        }
                    }

                }
            }
        });

        subscribeBroadcast(TimeUpBroadCast.class, (TimeUpBroadCast broad)->{
            terminate();
        });

        try{
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBroadcast(new Studentactivator(this.student));

    }
}
