package bgu.spl.mics.application.objects;


import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	//private LinkedBlockingQueue<DataBatch> unProcessDataBatch;
	private LinkedList<CPU> cpus;
	private ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> gpus;
	private ConcurrentHashMap<Data.Type, LinkedBlockingQueue<DataBatch>> unProcessDataBatch;
	private AtomicInteger cpusWorkingTime;
	private AtomicInteger cpuNumberOfProcessData;
	private AtomicInteger gpuWorkingTime;
	private LinkedBlockingQueue<String> models;



	private static class ClusterHolder{
		private static Cluster instance= new Cluster();
	}

	private Cluster(){

		this.gpus=new ConcurrentHashMap();
		this.cpus=new LinkedList<CPU>();
		this.cpusWorkingTime=cpusWorkingTime=new AtomicInteger(0);
		this.cpuNumberOfProcessData=new AtomicInteger(0);
		this.gpuWorkingTime=new AtomicInteger(0);
		this.models=new LinkedBlockingQueue<>();
		this.unProcessDataBatch=new ConcurrentHashMap();
		unProcessDataBatch.putIfAbsent(Data.Type.Images, new LinkedBlockingQueue<DataBatch>());
		unProcessDataBatch.putIfAbsent(Data.Type.Text, new LinkedBlockingQueue<DataBatch>());
		unProcessDataBatch.putIfAbsent(Data.Type.Tabular, new LinkedBlockingQueue<DataBatch>());
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	public void setGpus(LinkedList<GPU> gpus){
		while(gpus.size()>0){
			GPU gpu=gpus.remove();
			this.gpus.putIfAbsent(gpu, new LinkedBlockingQueue<DataBatch>());
		}
	}

	public void setCpus(LinkedList<CPU> cpus){
		this.cpus=cpus;

	}


	public void sendDataFromGpuToCluster(DataBatch data){
		Data.Type type=data.getType();
		unProcessDataBatch.get(type).add(data);
	}


	public DataBatch GPUgetDataFromCluster(GPU gpu) {
		return gpus.get(gpu).poll();
	}

	public DataBatch CPUgetDataFromCluster(int core) {
		synchronized (unProcessDataBatch) {
			if(core==32){
				if (unProcessDataBatch.get(Data.Type.Images).size() != 0)
					return unProcessDataBatch.get(Data.Type.Images).poll();
				else if(unProcessDataBatch.get(Data.Type.Text).size() != 0)
					return unProcessDataBatch.get(Data.Type.Text).poll();
				else if(unProcessDataBatch.get(Data.Type.Tabular).size() != 0)
					return unProcessDataBatch.get(Data.Type.Tabular).poll();

			}
			else if(core==16){
				if(unProcessDataBatch.get(Data.Type.Text).size() != 0)
					return unProcessDataBatch.get(Data.Type.Text).poll();
				else if (unProcessDataBatch.get(Data.Type.Images).size() != 0)
					return unProcessDataBatch.get(Data.Type.Images).poll();
				else if(unProcessDataBatch.get(Data.Type.Tabular).size() != 0)
					return unProcessDataBatch.get(Data.Type.Tabular).poll();

			}
			else if(core==8){
				if(unProcessDataBatch.get(Data.Type.Text).size() != 0)
					return unProcessDataBatch.get(Data.Type.Text).poll();
				else if(unProcessDataBatch.get(Data.Type.Tabular).size() != 0)
					return unProcessDataBatch.get(Data.Type.Tabular).poll();
				else if (unProcessDataBatch.get(Data.Type.Images).size() != 0)
					return unProcessDataBatch.get(Data.Type.Images).poll();

			}
			else if(core==4){

				if(unProcessDataBatch.get(Data.Type.Tabular).size() != 0)
					return unProcessDataBatch.get(Data.Type.Tabular).poll();
				else if(unProcessDataBatch.get(Data.Type.Text).size() != 0)
					return unProcessDataBatch.get(Data.Type.Text).poll();
				else if (unProcessDataBatch.get(Data.Type.Images).size() != 0)
					return unProcessDataBatch.get(Data.Type.Images).poll();

			}
			else if(core==2){

				if(unProcessDataBatch.get(Data.Type.Tabular).size() != 0)
					return unProcessDataBatch.get(Data.Type.Tabular).poll();
				else if(unProcessDataBatch.get(Data.Type.Text).size() != 0)
					return unProcessDataBatch.get(Data.Type.Text).poll();
				else if (unProcessDataBatch.get(Data.Type.Images).size() != 0)
					return unProcessDataBatch.get(Data.Type.Images).poll();

			}
			else if(core==1){

				if(unProcessDataBatch.get(Data.Type.Tabular).size() != 0)
					return unProcessDataBatch.get(Data.Type.Tabular).poll();
				else if(unProcessDataBatch.get(Data.Type.Text).size() != 0)
					return unProcessDataBatch.get(Data.Type.Text).poll();
				else if (unProcessDataBatch.get(Data.Type.Images).size() != 0)
					return unProcessDataBatch.get(Data.Type.Images).poll();

			}

			else return null;

		}
		return null;
	}


	public void sendDataFromCpuToCluster(DataBatch data)  {
		gpus.get(data.getGpu()).add(data);

	}



	public void updateGpuWorkingTime(int TimeInProcess){
		int val;
		do{
			val=gpuWorkingTime.get();
		}while(!gpuWorkingTime.compareAndSet(val, val+TimeInProcess));

	}
	public void updateCpuWorkingTime(int TimeInProcess){
		int val;
		do{
			val=cpusWorkingTime.get();
		}while(!cpusWorkingTime.compareAndSet(val, val+TimeInProcess));

	}
	public void updateCpuNumberOfProcessData(int TimeInProcess){
		int val;
		do{
			val=cpuNumberOfProcessData.get();
		}while(!cpuNumberOfProcessData.compareAndSet(val, val+TimeInProcess));

	}

	public void updateModelsName(LinkedList<String> modelsName){
		for(String name: modelsName){
			this.models.add(name);
		}
	}

	public AtomicInteger getGpuWorkingTime(){
		return gpuWorkingTime;
	}

	public AtomicInteger getCpusWorkingTime(){
		return cpusWorkingTime;
	}

	public AtomicInteger getcpuNumberOfProcessData(){
		return cpuNumberOfProcessData;
	}


	public boolean containsUnProcessedData(DataBatch data){
		return unProcessDataBatch.containsKey(data);
	}

	public boolean containsProcessData(DataBatch data){

		return gpus.get(data.getGpu()).contains(data);
	}
	public  ConcurrentHashMap<Data.Type, LinkedBlockingQueue<DataBatch>> getUnProcessDataBatch(){
		return unProcessDataBatch;
	}




	public void setGpus(ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> gpus) {
		this.gpus = gpus;
	}

	public void setCpusWorkingTime(AtomicInteger cpusWorkingTime) {
		this.cpusWorkingTime = cpusWorkingTime;
	}

	public AtomicInteger getCpuNumberOfProcessData() {
		return cpuNumberOfProcessData;
	}

	public void setCpuNumberOfProcessData(AtomicInteger cpuNumberOfProcessData) {
		this.cpuNumberOfProcessData = cpuNumberOfProcessData;
	}

	public void setGpuWorkingTime(AtomicInteger gpuWorkingTime) {
		this.gpuWorkingTime = gpuWorkingTime;
	}

	public LinkedBlockingQueue<String> getModels() {
		return models;
	}

	public void setModels(LinkedBlockingQueue<String> models) {
		this.models = models;
	}
}