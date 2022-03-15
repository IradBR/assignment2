package bgu.spl.mics.application.services;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TimeUpBroadCast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import java.util.LinkedList;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private LinkedList<Event> modelsToProcess;
    private GPU gpu;
    private boolean TimeUp;


    public GPUService(String name,GPU gpu) {
        super(name);
        this.gpu=gpu;
        this.modelsToProcess=new LinkedList<Event>();
        gpu.updateGpuService(this);
        this.TimeUp=false;
    }


    @Override
    protected void initialize() {
        subscribeBroadcast( TickBroadcast.class, ( TickBroadcast broad)->{
            gpu.increaseTick();
        });

        subscribeEvent(TrainModelEvent.class, (TrainModelEvent event)->{
            if(gpu.getEvent()!=null){
                modelsToProcess.add(event);
            }
            else {
                gpu.trainModel(event);
            }
        });

        subscribeEvent(TestModelEvent.class, (TestModelEvent event)->{
            if(gpu.getEvent()!=null) {
                modelsToProcess.add(event);
            }
            else{
                gpu.testModel(event);
            }
        });

        subscribeBroadcast(TimeUpBroadCast.class, (TimeUpBroadCast broad)->{
            if(gpu.getData()!=null && !gpu.ModelDoneProcessing()) {
                complete(gpu.getEvent(), null);
            }

            while(modelsToProcess.size()!=0){
                complete(modelsToProcess.remove(),null);
            }

            if(TimeUp==false){
                TimeUp=true;
                sendBroadcast(new TimeUpBroadCast());
            }else {
                gpu.updateWorkingTime();
                gpu.updateModelsNamed();
                terminate();
            }
        });

    }

    public void complete(Event event){
        complete(event, true);
    }

    public boolean hasModelsToProcess(){
        return modelsToProcess.size()!=0;
    }

    public void processNextModel(){
        if(hasModelsToProcess()){
            Event event= modelsToProcess.poll();
            if (event.getClass().equals(TrainModelEvent.class)){
                gpu.trainModel((TrainModelEvent)event);
            }
            else  {
                gpu.testModel((TestModelEvent)event);
            }

        }
        else;

    }



}