package CS247;

import java.util.*;
import java.util.concurrent.*;

public class Scheduler extends Thread {
	
	// job_queue holds the jobs that are ready to be sent to clients.
	private PriorityBlockingQueue<ScheduledJob> job_queue;
	private ArrayList<PeriodicJob> periodic_jobs;
	private DataSourceManager data_source_manager;
	private long prev_time;
	
	Scheduler(Server server){
		super("Scheduler");
		job_queue = new PriorityBlockingQueue<ScheduledJob>();
		periodic_jobs = new ArrayList<PeriodicJob>();
		data_source_manager = new DataSourceManager();
		prev_time = System.currentTimeMillis();
	}
	
	public void run(){
		while(true){
			// update jobs.
			data_source_manager.getSources(periodic_jobs);
			// get the time delta from when we did this last.
			long delta = System.currentTimeMillis() - prev_time;
			prev_time = System.currentTimeMillis();
			
			// update the countdown for all the periodic jobs,
			// adding them to the job_queue if the countdown is <= 0, and resetting it.
			for(PeriodicJob p : periodic_jobs){
				if(job_queue.contains(p)) continue;
				p.countdown -= delta;
				if(p.countdown <= 0){
					job_queue.add(p);
					p.countdown = p.interval;
				}
			}
			// sleep, so as not to use 100% cpu unnecessarily.
			try {
				Thread.sleep(10);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	// this will block until a job is available.
	Job getNextJob() {
		while(true){	
			try {
				return job_queue.take();
			} catch(InterruptedException e){
				continue;
			}
		}
	}
	
	void addJob(Job j) {
		job_queue.put(new ScheduledJob(j, 10));
	}
}
// class to use in the job_queue.
class ScheduledJob extends Job implements Comparable<ScheduledJob> {
	// lower number = higher priority.
	int priority;
	
	ScheduledJob(int priority, byte type, String url){
		super(type, url);
		this.priority = priority;
	}
	
	ScheduledJob(Job j, int priority){
		super(j);
		this.priority = priority;
	}
	
	public int compareTo(ScheduledJob other){
		return this.priority - other.priority;
	}
	
}
// class for periodic jobs.
class PeriodicJob extends ScheduledJob {
	int interval;
	int countdown;
	
	PeriodicJob(int interval, byte type, String url){
		super(100, type, url);
		
		this.interval = interval;
		this.countdown = 1000;
	}
}
