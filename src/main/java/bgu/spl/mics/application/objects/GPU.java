package bgu.spl.mics.application.objects;;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;
import java.util.LinkedList;


/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}


    private LinkedList<DataBatch> disk;
    private LinkedList<String> modelsNames;
    private Cluster cluster;
    private Type type;
    private int capacity;
    private int currTick;
    private int finishProcessTime;
    private Data data;
    private DataBatch currDataInProcess;
    private boolean inProcess;
    private GPUService gpuService;
    private Event event;
    private int TimeInProcess;

    public GPU(String type){
        if(type.equals("RTX3090")) {
            this.type = Type.RTX3090;
            capacity = 32;
        }
        if(type.equals("RTX2080")) {
            this.type = Type.RTX2080;
            capacity = 16;
        }
        if(type.equals("GTX1080")) {
            this.type = Type.GTX1080;
            capacity = 8;
        }
        this.cluster=Cluster.getInstance();
        this.disk=new LinkedList<DataBatch>();
        this.data=null;
        this.currTick=1;
        this.finishProcessTime=1;
        this.inProcess=false;
        this.currDataInProcess=null;
        this.event=null;
        this.TimeInProcess=0;
        this.modelsNames=new LinkedList<>();
    }

    public void updateGpuService(GPUService gpuService){

        this.gpuService=gpuService;
    }


    public void  trainModel(TrainModelEvent event){
        this.event=event;
        this.data=event.getModel().getData();
        turnDataIntoBatches(data);

        for(int i=0; i<capacity; i++){
            if(disk.size()!=0) {
                cluster.sendDataFromGpuToCluster(disk.removeFirst()); //first sending
            }

        }
    }

    public void turnDataIntoBatches(Data data){
        int size= data.getSize();
        int Ticks=TicksPerBatch(data);
        for(int i=0; i<size; i=i+1000){
            DataBatch toAdd= new DataBatch(i, this, Ticks, data.getType());
            disk.add(toAdd);
        }
    }

    public int TicksPerBatch(Data data){
        String type;
        if(data.getType().equals(Data.Type.Images)) return 4;
        else if(data.getType().equals(Data.Type.Text)) return 2;
        else return 1;
    }


    /**
     * update the timeInProcess filed in each process
     * @pre: None
     * @post: timeToProcessBatch==4||2||1
     */
    public int timeToProcessBatch(){
        if (type.equals(Type.GTX1080)) return 4;
        else if (type.equals(Type.RTX2080)) return 2;
        else return 1;
    }

    public void processData(){
        currDataInProcess= cluster.GPUgetDataFromCluster(this);
        if (currDataInProcess != null) {
            inProcess=true;
            finishProcessTime = currTick + timeToProcessBatch();
            if(disk.size()!=0) {
                cluster.sendDataFromGpuToCluster(disk.removeFirst());
            }
        }
        else {
            inProcess=false;
        }
    }

    /**
     *@PRE currTick>=0
     *@POST currTick= @PRE(currTick) +1;
     *@Post @pre(currDataInProcess)!=currDataInProcess || !isDoneProcessing()
     */
    public void increaseTick(){
        currTick=currTick+1;
        if(inProcess){
            TimeInProcess = TimeInProcess + 1;
            if (DataBatchDoneProcessing()){
                data.increaseProcessData();
                if(ModelDoneProcessing()) {
                    modelsNames.add(((TrainModelEvent)event).getModel().getName());
                    ((TrainModelEvent)event).getModel().setStatus(Model.Status.Trained);
                    complete();
                    inProcess = false;
                    data=null;
                    currDataInProcess=null;
                    event=null;
                    gpuService.processNextModel();
                }
                else processData();
            }
        }
        else {
            processData();
        }
    }

    public boolean ModelDoneProcessing(){
        return data.isDoneProcessing();

    }

    public void complete(){
        gpuService.complete(event);
    }


    public boolean DataBatchDoneProcessing(){
        if(currTick==finishProcessTime){
            return true;
        }
        return false;
    }



    public void testModel(TestModelEvent event){
        this.event= event;
        Model model= event.getModel();
        modelsNames.add(event.getModel().getName());
        Student.Degree degree= model.getStudent().getStatus();
        Model.Result result;
        if(degree.equals(Student.Degree.MSc)){
            result= MScrundom();
        }
        else {
            result=PhDrundom();
        }
        event.getModel().setResult(result);
        event.getModel().setStatus(Model.Status.Tested);
        complete();
        this.event=null;
    }

    public Model.Result MScrundom(){
        int rundNum= 1+ (int)(Math.random()*10);
        if(rundNum<=6) return Model.Result.Good;
        else return Model.Result.Bad;

    }
    public Model.Result PhDrundom(){
        int rundNum= 1+ (int)(Math.random()*10);
        if(rundNum<=8) return Model.Result.Good;
        else return Model.Result.Bad;

    }

    public void updateWorkingTime(){
        cluster.updateGpuWorkingTime(TimeInProcess);
    }

    public void updateModelsNamed(){
        cluster.updateModelsName(modelsNames);
    }

    public Event getEvent(){
        return event;
    }


    //queries
    public boolean isProccecing(){
        return inProcess;
    }

    //queries
    public int diskSize() {
        return disk.size();
    }

    public LinkedList<DataBatch> getDisk() {
        return disk;
    }

    public void setDisk(LinkedList<DataBatch> disk) {
        this.disk = disk;
    }

    public LinkedList<String> getModelsNames() {
        return modelsNames;
    }

    public void setModelsNames(LinkedList<String> modelsNames) {
        this.modelsNames = modelsNames;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrTick() {
        return currTick;
    }

    public void setCurrTick(int currTick) {
        this.currTick = currTick;
    }

    public void setFinishProcessTime(int finishProcessTime) {
        this.finishProcessTime = finishProcessTime;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isInProcess() {
        return inProcess;
    }

    public void setInProcess(boolean inProcess) {
        this.inProcess = inProcess;
    }

    public GPUService getGpuService() {
        return gpuService;
    }

    public void setGpuService(GPUService gpuService) {
        this.gpuService = gpuService;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getTimeInProcess() {
        return TimeInProcess;
    }

    public void setTimeInProcess(int timeInProcess) {
        TimeInProcess = timeInProcess;
    }

    //queries
    public int getFinishProcessTime() {
        return finishProcessTime;
    }

    //queries
    public boolean contains(DataBatch data){
        return true;
    }
    public void setCurrDataInProcess(DataBatch data){
        currDataInProcess=data;
    }

    public DataBatch getCurrDataInProcess(){
        return currDataInProcess;
    }
    public Data getData(){
        return data;
    }


}