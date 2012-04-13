package CS247;

import java.util.*;

public class Scheduler extends Thread {
	
	// job_queue holds the jobs that are ready to be sent to clients.
	PriorityQueue<ScheduledJob> job_queue;
	ArrayList<PeriodicJob> periodic_jobs;
	
	long prev_time;
	
	Scheduler(Server server){
		super("Scheduler");
		job_queue = new PriorityQueue<ScheduledJob>();
		periodic_jobs = new ArrayList<PeriodicJob>();
		prev_time = System.currentTimeMillis();
		// this.database = server.database; FIXME: implement database.
		// load periodic jobs from database.
		// periodic_jobs.addAll(database.getPeriodicJobs());
		// Add a job for testing purposes.
		PeriodicJob p = new PeriodicJob(1000, Job.TEST, "Testing!");
		periodic_jobs.add(p);
	}
	
	public void run(){
		while(true){
			// get the time delta from when we did this last.
			long delta = System.currentTimeMillis() - prev_time;
			prev_time = System.currentTimeMillis();
			
			// update the countdown for all the periodic jobs,
			// adding them to the job_queue if the countdown is <= 0, and resetting it.
			for(PeriodicJob p : periodic_jobs){
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
	// this can return null if no job is ready.
	synchronized Job getNextJob(){
		return job_queue.poll();
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
		this.countdown = interval;
	}
}
