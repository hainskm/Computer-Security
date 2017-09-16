package processScheduler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ImprovedMethodProcessor extends TimerTask {
	private int currentTime = 0;
	private int status = 0;
	private int arrivalTime;
	private int startTime;
	private int initialStartTime;
	private int completionTime;
	//final time to calculate overall turnaround time
	private int finalTime;
	private int firstJob = 0;
	private ArrayList<Job> jobs = new ArrayList<Job>();
	public ImprovedMethodProcessor(){
		
	}
	
	public void run(){
		//if a job is in queue
		currentTime++;
		if(!jobs.isEmpty()){
			//checks if job complete
			if(currentTime == completionTime){
				//final time updated after each job completes. Fine as long as turnaround time calculated after all jobs done
				finalTime = currentTime;
				//remove job from queue
				jobs.remove(0);
				//if a job is waiting, calculate its completion time
				status = 0;
				
			}
		}
		else{
			status = 0;
		}
		
	}
	public void setStatus(int s){
		status = s;
	}
	public int getStatus(){
		return status;
	}
	public int getTime(){
		return currentTime;
	}
	public int getStartTime(){
		return initialStartTime;
	}
	public int getEndTime(){
		return finalTime;
	}
	//adds a job to processor queue
	public void addJob(Job j){
		if(jobs.isEmpty() && firstJob == 0){
			//sets the initial start time of processor to calculate overall turnaround time
			initialStartTime = j.arrivalTime()+1;
			//makes sure initialStartTime not changed
			firstJob = 1;
		}
		jobs.add(j);
		status = 1;
		//sets arrival/completion time
		arrivalTime = j.arrivalTime();
		completionTime = (currentTime+1) + j.processingTime();
			

		if(!jobs.isEmpty()){
			status = 1;
		}
		else{
			status = 0;
		}
		
	}
}
