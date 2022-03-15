package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadCast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TimeUpBroadCast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;

    public CPUService(String ms_name, CPU cpu) {
        super(ms_name);
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast( TickBroadcast.class, (TickBroadcast broad)->{
            cpu.increaseTick();
       });

        subscribeBroadcast(TimeUpBroadCast.class, (TimeUpBroadCast broad)->{
            cpu.updateWorkingTime();
            cpu.updateNmOfProcessedDataBatch();
            terminate();
        });

    }
}