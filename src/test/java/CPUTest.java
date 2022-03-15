import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class CPUTest {

    private Cluster cluster;
    private CPU cpu;
    private GPU gpu;
    private DataBatch DataBatch1;
    private DataBatch DataBatch2;
    private DataBatch DataBatch3;
    private MessageBusImpl message_bus;



    @Before
    public void setUp() throws Exception {
        cluster= Cluster.getInstance();
        cpu=new CPU(8 );
        gpu=new GPU("RTX3090");
        LinkedList<GPU> gpus=new LinkedList<>();
        gpus.add(gpu);
        cluster.setGpus(gpus);
        DataBatch1= new DataBatch(0,gpu,1,Data.Type.Images);
        DataBatch2= new DataBatch(0,gpu,1, Data.Type.Images);
        DataBatch3= new DataBatch(0,gpu,1, Data.Type.Images);
        message_bus = MessageBusImpl.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        message_bus.cleanBus();;
    }



    @Test
    public void processData() {
        cpu.processData();
        assertFalse(cpu.getInProcess());
        assertEquals(cpu.getCurrDataInProcess(), null);
        cluster.sendDataFromGpuToCluster(DataBatch1);
        cpu.processData();
        assertEquals(cpu.getCurrDataInProcess(), DataBatch1);
        assertEquals(cpu.getInProcess(), true);
        assertEquals(cpu.getFinishProcessTime(), 5);
    }

    @Test
    public void increaseTick() {
        assertEquals(cpu.getCurrTick(), 1);
        cpu.increaseTick();
        assertEquals(cpu.getCurrTick(), 2);
        cluster.sendDataFromGpuToCluster(DataBatch1);
        cpu.processData();
        assertNotNull(cpu.getCurrDataInProcess());
        cpu.increaseTick();
        cpu.increaseTick();
        cpu.increaseTick();
        cpu.increaseTick();
        assertEquals(cpu.getNumOfProcessBatch(),1);
        assertTrue(cluster.containsProcessData(DataBatch1));
        assertNull(cpu.getCurrDataInProcess());
    }

    @Test
    public void isDoneProcessing() {
        cluster.sendDataFromGpuToCluster(DataBatch1);
        cpu.processData();
        cpu.increaseTick();
        cpu.increaseTick();
        cpu.increaseTick();
        cpu.increaseTick();
        assertTrue(cpu.isDoneProcessing());
        assertFalse(cpu.getInProcess());
        System.out.println();
    }


}