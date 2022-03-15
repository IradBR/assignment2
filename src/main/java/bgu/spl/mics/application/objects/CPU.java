package bgu.spl.mics.application.objects;
import java.util.concurrent.BlockingQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 *
 */
public class CPU {

    private int core;
    private Cluster cluster;
    private int finishProcessTime;
    private int currTick;
    private DataBatch currDataInProcess;
    private boolean inProcess;
    private int TimeInProcess;
    private int numOfProcessBatch;

    public CPU(int core){
        this.core=core;
        this.currTick=1;
        this.finishProcessTime=1;
        this.currDataInProcess=null;
        this.inProcess=false;
        this.cluster=Cluster.getInstance();
        this.TimeInProcess=0;
        this.numOfProcessBatch=0;
    }


    /**
     * take unprocessed data from the dataToProcess queue
     *if there is no data to process, the thread will waite
     *the function call to time to process in order to update the time ticks
     * at the and of the process the data will return to cluster
     * change the data filed to process
     * @PRE: inProcess==false
     * @POST: @PRE(currDataInProcess)!=currDataInProcess
     * @POST: inProcess==true
     * @POST: data.ticksToProcess()*(32/core)+ currTick==finishProcessTime

     */
    //process the data at the cpu
    public void processData(){
        currDataInProcess= cluster.CPUgetDataFromCluster(core); //if there is no data returns null
        if(currDataInProcess!=null) { //start a new process
            inProcess=true;
            finishProcessTime= currTick+timeToProcessBatch(currDataInProcess);
        }
        else {
            inProcess = false;  //there is no available data
            currDataInProcess = null;
        }
    }


    /**
     * update the timeInProcess filed in each process
     */
    public int timeToProcessBatch(DataBatch data){
        return data.getTicks()*(32/core);
    }
    /**
     * update the time tick acording to time ticks broadcast
     * if the process is done return the data to cluster and start another one
     *@PRE currTick=>0
     *@POST currTick= @PRE(currTick) +1;
     *@Post @pre(currDataInProcess)!=currDataInProcess || !isDoneProcessing()
     */

    public void increaseTick() {
        currTick = currTick + 1;
        if (inProcess) {
            TimeInProcess = TimeInProcess + 1;
            if (isDoneProcessing()) {
                numOfProcessBatch = numOfProcessBatch + 1;
                cluster.sendDataFromCpuToCluster(currDataInProcess);
                processData();
            }
        }
        else processData();
    }


    /**
     * return true if the cpu ended to process in this tick
     *@PRE: inProcess==true && currDataInProcess!=null
     *@POST: currTick!=finishProcessTime || (inProcess==false)
     */
    public boolean isDoneProcessing(){
        if(currTick==finishProcessTime){
            inProcess=false;
            return true;
        }
        return false;
    }


    public void updateWorkingTime(){

        cluster.updateCpuWorkingTime(TimeInProcess);
    }

    public void updateNmOfProcessedDataBatch(){

        cluster.updateCpuNumberOfProcessData(numOfProcessBatch);
    }


    public DataBatch getProcessData(){

        return currDataInProcess;
    }


    public int getCurrTick(){

        return currTick;
    }

    public int getFinishProcessTime() {
        return
                finishProcessTime;
    }

    public int getCore(){
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void setFinishProcessTime(int finishProcessTime) {
        this.finishProcessTime = finishProcessTime;
    }

    public void setCurrTick(int currTick) {
        this.currTick = currTick;
    }

    public DataBatch getCurrDataInProcess() {
        return currDataInProcess;
    }

    public void setCurrDataInProcess(DataBatch currDataInProcess) {
        this.currDataInProcess = currDataInProcess;
    }

    public boolean isInProcess() {
        return inProcess;
    }

    public void setInProcess(boolean inProcess) {
        this.inProcess = inProcess;
    }

    public int getTimeInProcess() {
        return TimeInProcess;
    }

    public void setTimeInProcess(int timeInProcess) {
        TimeInProcess = timeInProcess;
    }

    public int getNumOfProcessBatch() {
        return numOfProcessBatch;
    }

    public void setNumOfProcessBatch(int numOfProcessBatch) {
        this.numOfProcessBatch = numOfProcessBatch;
    }
    public boolean getInProcess(){
        return inProcess;
    }
}