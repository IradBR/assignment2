import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.GPUService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;


import static org.junit.Assert.*;

public class GPUTest {
    private Cluster cluster;
    private GPU gpu;
    private DataBatch dataBatch;
    private Data data;
    private TestModelEvent TestModelEvent;
    private TrainModelEvent TrainModelEvent;
    private Model model;
    private GPUService gpuService;
    private MessageBusImpl message_bus;

    enum Type {RTX3090, RTX2080, GTX1080}

    @Before
    public void setUp() throws Exception {
        this.cluster= Cluster.getInstance();
        this.gpu=new GPU("GTX1080" );
        LinkedList<GPU> gpus=new LinkedList<>();
        gpus.add(gpu);
        this.cluster.setGpus(gpus);
        this.dataBatch= new DataBatch(0,gpu,1, Data.Type.Images);
        this.model=new Model("TestGpu", "images", 2000);
        LinkedList<Model> models=new LinkedList<Model>();
        models.add(model);
        model.setStudent(new Student("zazu","computer science", "MSc",models ));
        this.data=model.getData();
        this.TestModelEvent=new TestModelEvent(model);
        this.TrainModelEvent=new TrainModelEvent(model);
        this.gpuService=new GPUService("gpuService",gpu);
        gpu.updateGpuService(gpuService);
        message_bus = MessageBusImpl.getInstance();

    }

    @After
    public void tearDown() throws Exception {
        message_bus.cleanBus();;
    }

    @Test
    public void  trainModel(){
        assertNull(gpu.getEvent());
        assertNull(gpu.getData());
        assertEquals(gpu.diskSize(),0);
        gpu.trainModel(TrainModelEvent);
        assertEquals(gpu.getEvent(), TrainModelEvent);
        assertNotNull(gpu.getData());
        assertEquals(cluster.getUnProcessDataBatch().get(Data.Type.Images).size(),2);

    }


    @Test
    public void turnDataIntoBatches() {
        assertEquals(gpu.diskSize(),0);
        gpu.turnDataIntoBatches(data);
        assertEquals(gpu.diskSize(),2);
    }


    @Test
    public void processData() {
        gpu.processData();
        assertNull(gpu.getCurrDataInProcess());
        assertFalse(gpu.isProccecing());
        cluster.sendDataFromCpuToCluster(dataBatch);
        gpu.processData();
        assertTrue(gpu.isProccecing());
        assertNotNull(gpu.getCurrDataInProcess());
    }


    @Test
    public void increaseTick() {
        assertEquals(gpu.getCurrTick(), 1);
        gpu.increaseTick();
        assertEquals(gpu.getCurrTick(), 2);
        cluster.sendDataFromCpuToCluster(dataBatch);
        gpu.trainModel(TrainModelEvent);
        gpu.processData();
        assertNotNull(gpu.getCurrDataInProcess());
        assertEquals(gpu.getFinishProcessTime(), 6);
        gpu.increaseTick();
        gpu.increaseTick();
        gpu.increaseTick();
        gpu.increaseTick();
        assertEquals(gpu.getData().getProcessed(),1000);
        assertFalse(gpu.ModelDoneProcessing());
    }

    @Test
    public void testModel(){
        gpu.testModel(TestModelEvent);
        assertNull(gpu.getEvent());
        assertEquals(TestModelEvent.getModel().getStatus(),Model.Status.Tested);
        assertNotEquals(TestModelEvent.getModel().getResult(),Model.Result.None );

    }


}