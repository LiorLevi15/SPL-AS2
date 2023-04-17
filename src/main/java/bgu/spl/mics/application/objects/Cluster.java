package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Vector<GPU> gpus;
	private PriorityBlockingQueue<CPU> cpus;


	private static class ClusterHolder{
		private static Cluster instance = new Cluster();
	}

	private Cluster(){ }


	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return Cluster.ClusterHolder.instance;
	}
	public void addGPUs(Vector<GPU> gpus) {
		this.gpus = gpus;
	}
	public void addCPUs(Vector<CPU> cpus) {
		this.cpus = new PriorityBlockingQueue<CPU>(cpus.size(), Comparator.comparingLong(CPU::getTimeToProcess));
		for (CPU currCpu : cpus) {
			this.cpus.add(currCpu);
		}
	}

	public void addProcessed(DataBatch batch) {
		batch.getData().getGpu().addBatch(batch);
	}

	public synchronized void sendForProcess(DataBatch batch) {
		CPU minCPU = this.cpus.poll();
		minCPU.addBatch(batch);
		cpus.add(minCPU);
	}

}
