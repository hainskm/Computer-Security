package processScheduler;

public class Job {
	
	private int aT;
	private int pT;
	private int jN;
	//0 means waiting, 1 means running
	private int status;
	public Job(int jobNum, int arrivalTime, int processingTime){
		jN = jobNum;
		aT = arrivalTime;
		pT= processingTime;
		status = 0;
	}
	
	public int arrivalTime(){
		return aT;
	}
	public int processingTime(){
		return pT;
	}
	public int jobNumber(){
		return jN;
	}
}
