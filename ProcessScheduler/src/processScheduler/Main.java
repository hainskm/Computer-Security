package processScheduler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Scanner;

public class Main{
	private static int currentTime = 0;
	private static int i = 0;
	private static int j = 0;
	private static int m = 0;
	private static int overallTurnaround;
	private static int[]oneHundredTurnaround = new int[100];
	private static int[]oneHundredTurnaround2 = new int[100];
	private static double[] stdDev = new double[100];
	private static double standardDeviation;
	private static int startTime;
	private static int endTime;
	private static int minimumT = 999999999;
	private static int maximumT = -50;
	private static int sum = 0;
	private static double average;
	private static ArrayList<ArrayList<Job>> jobArray = new ArrayList<ArrayList<Job>>();
	private static ArrayList<ArrayList<Job>> jobArray2 = new ArrayList<ArrayList<Job>>();
	private static File file = new File("hainsworth_kevin_1.output");
	private static FileWriter fw;
	private static ArrayList<Job> waitQueue = new ArrayList<Job>();
	
	
	
	public static void main(String[] args){
		//Code to write output to file
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		Random rand = new Random();
		Scanner scan = new Scanner(System.in);
		ArrayList<Job> jobs = new ArrayList<Job>();
		//b.1 creates a random 1000 job sequence 100 times
		for(int l = 0; l<5; l++){
			ArrayList<Job> temp = new ArrayList<Job>();
			
			for(int k = 1; k <= 1000; k++){
				//random run time from 1 ms to 500
				temp.add(new Job(k,k,rand.nextInt(499)+1));
			}
		
			jobArray.add(temp);			
		}
		//runs the 100 sets of job sequences in a nonpreemptive round robin fashion
		for(int s=0; s<5; s++){
			executeProcessors(jobArray.get(s));
			//Used to force one set of jobs to run at a time. press enter to run the next one after the previous completes
			String halt = scan.nextLine();
		}
		//calculates min and max turnaround times
		for(int n=0; n<5;n++){
			if(oneHundredTurnaround[n] == 0){
				continue;
			}
			sum = sum + oneHundredTurnaround[n];
			if(oneHundredTurnaround[n] < minimumT){
				minimumT = oneHundredTurnaround[n];
			}
			if(oneHundredTurnaround[n] > maximumT){
				maximumT = oneHundredTurnaround[n];
			}
		}
		//calculates average
		average = (double) sum/5;
		//stdDev calc
		for(int b=0; b<5; b++){
			if(oneHundredTurnaround[b] == 0){
				continue;
			}
			double temp = Math.pow((oneHundredTurnaround[b]-average),2);
			stdDev[b] = temp;
		}
		double stdSum = 0;
		for(int z = 0; z<5;z++){
			stdSum = stdSum+stdDev[z];
		}
		double variance = stdSum/5;
		standardDeviation = Math.sqrt(variance);
		
		System.out.println("Minimum: " + minimumT);
		System.out.println("Maximum: " + maximumT);
		System.out.println("Average: " + average);
		System.out.println("Standard Deviation: " + standardDeviation);
		//writes to file
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			bw.write("Random Job Sequence of 1000: \n");
			bw.write("Minimum: "+ minimumT+ "\n");
			bw.write("Maximum: "+ maximumT+ "\n");
			bw.write("Average: "+ average+"\n");
			bw.write("Standard Deviation: "+ standardDeviation+"\n\n");
		

			//System.out.println("Done");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//reset values
		minimumT = 999999999;
		maximumT = -50;
		sum = 0;
		average = 0;
		//part b.2 12 job schedule
		jobs.add(new Job(1,4,9));
		jobs.add(new Job(2,15,2));
		jobs.add(new Job(3,18,16));
		jobs.add(new Job(4,20,3));
		jobs.add(new Job(5,26,29));
		jobs.add(new Job(6,29,198));
		jobs.add(new Job(7,35,7));
		jobs.add(new Job(8,45,170));
		jobs.add(new Job(9,57,180));
		jobs.add(new Job(10,83,178));
		jobs.add(new Job(11,88,73));
		jobs.add(new Job(12,95,8));
		
		executeProcessors(jobs);
		//Same as previous
		String halt = scan.nextLine();
		//writes to file
		try {
			bw.write("Twelve Job Sequence: \n");
			bw.write("Turnaround Time: " + overallTurnaround + "\n\n");
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//part c, improved on the 12 job sequence
		jobs.add(new Job(1,4,9));
		jobs.add(new Job(2,15,2));
		jobs.add(new Job(3,18,16));
		jobs.add(new Job(4,20,3));
		jobs.add(new Job(5,26,29));
		jobs.add(new Job(6,29,198));
		jobs.add(new Job(7,35,7));
		jobs.add(new Job(8,45,170));
		jobs.add(new Job(9,57,180));
		jobs.add(new Job(10,83,178));
		jobs.add(new Job(11,88,73));
		jobs.add(new Job(12,95,8));
		improvedMethod(jobs);
		//same as previous
		String halt1 = scan.nextLine();
		//writes to file
		try {
			bw.write("Twelve Job Sequence on Improved Method: \n");
			bw.write("Turnaround Time: " + overallTurnaround + "\n\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//part c 1000 random jobs. code similar to above
		for(int l = 0; l<20; l++){
			ArrayList<Job> temp = new ArrayList<Job>();
			
			for(int k = 1; k <= 1000; k++){
				temp.add(new Job(k,k,rand.nextInt(499)+1));
			}
		
			jobArray2.add(temp);	
			
		}
		for(int s=0; s<20; s++){
			improvedMethod(jobArray2.get(s));
			//Used to force one set of jobs to run at a time. press enter to run the next one after the previous completes
			String halt2 = scan.nextLine();
		}

		for(int n=0; n<20;n++){
			if(oneHundredTurnaround2[n] == 0){
				continue;
			}
			sum = sum + oneHundredTurnaround2[n];
			if(oneHundredTurnaround2[n] < minimumT){
				minimumT = oneHundredTurnaround2[n];
			}
			if(oneHundredTurnaround2[n] > maximumT){
				maximumT = oneHundredTurnaround2[n];
			}
		}
		average = (double) sum/20;
		//stdDev calc
		for(int b=0; b<20; b++){
			if(oneHundredTurnaround2[b] == 0){
				continue;
			}
			double temp = Math.pow((oneHundredTurnaround2[b]-average),2);
			stdDev[b] = temp;
		}
		double stdSum2 = 0;
		for(int z = 0; z<20;z++){
			stdSum2 = stdSum2+stdDev[z];
		}
		double variance2 = stdSum2/20;
		standardDeviation = Math.sqrt(variance2);
		
		System.out.println("Minimum: " + minimumT);
		System.out.println("Maximum: " + maximumT);
		System.out.println("Average: " + average);
		System.out.println("Standard Deviation: " + standardDeviation);
		
		try {
			
			
			bw.write("Random Job Sequence of 1000 on Improved Method: \n");
			bw.write("Minimum: "+ minimumT+ "\n");
			bw.write("Maximum: "+ maximumT+ "\n");
			bw.write("Average: "+ average+"\n");
			bw.write("Standard Deviation: "+ standardDeviation);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	//Create 3 processors (1534%3 +2 = 3) and overall timer for main to keep track of all
	public static void executeProcessors(ArrayList<Job> jobs){
		Timer processor0 = new Timer();
		Timer processor1 = new Timer();
		Timer processor2 = new Timer();
		Processor p1 = new Processor();
		Processor p2 = new Processor();
		Processor p3 = new Processor();
		processor0.schedule(p1,0,1);
		processor1.schedule(p2,0,1);
		processor2.schedule(p3,0,1);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				//increment every 1 ms
				currentTime++;
				//System.out.println(currentTime + "!");
					
				//if not done and a job is arriving
				if(i<jobs.size() && currentTime == jobs.get(i).arrivalTime()){
					//figure out which processor to put job in
					if(jobs.get(i).jobNumber() == 1){
						p1.addJob(jobs.get(i));
						j = 1;
					}
					else if((j+1) % 3 == 1){
						p1.addJob(jobs.get(i));
						j = 1;
					}
					else if((j+1) % 3 == 2){
						p2.addJob(jobs.get(i));
						j = 2;
					}
					else{
						p3.addJob(jobs.get(i));
						j = 3;
					}
					i++;
						
				}
				//checks if done with all jobs
				else if(i >= jobs.size() && p1.getStatus()==0 && p2.getStatus()==0 && p3.getStatus()==0){
					
					if(p1.getStartTime() <= p2.getStartTime() && p1.getStartTime() <= p3.getStartTime()){
						startTime = p1.getStartTime();
					}
					else if(p2.getStartTime() <= p1.getStartTime() && p2.getStartTime() <= p3.getStartTime()){
						startTime = p2.getStartTime();
					}
					else{
						startTime = p3.getStartTime();
					}
					if(p1.getEndTime() >= p2.getEndTime() && p1.getEndTime() >= p3.getEndTime()){
						endTime = p1.getEndTime();
					}
					else if(p2.getEndTime() >= p1.getEndTime() && p2.getEndTime() >= p3.getEndTime()){
						endTime = p2.getEndTime();
					}
					else{
						endTime = p3.getEndTime();
					}
					//turnaround time calculations
					overallTurnaround = endTime - startTime;
					oneHundredTurnaround[m]=overallTurnaround;
					m++;
					
					System.out.println("Overall Start Time: " + startTime);
					System.out.println("Overall End Time: " + endTime);
					System.out.println("Overall Turnaround Time: " + overallTurnaround);
					//stops timers
					timer.cancel();
					timer.purge();
					p1.cancel();
					p2.cancel();
					p3.cancel();
					processor0.cancel();
					processor1.cancel();
					processor2.cancel();
					processor0.purge();
					processor1.purge();
					processor2.purge();
					i = 0;
					currentTime = 0;
				}
	
			}
		}, 0, 1);
		
		
	}
	/*Improved method idea: 
	 * Rather than have individual jobs assigned to be on a specific processor,
	 * Have the processor immediately grab the next available job in the
	 * waiting queue. This way, a lot less processor time is wasted.*/
	public static void improvedMethod(ArrayList<Job> jobs){
		//similar code to original method
		Timer processor0 = new Timer();
		Timer processor1 = new Timer();
		Timer processor2 = new Timer();
		ImprovedMethodProcessor p1 = new ImprovedMethodProcessor();
		ImprovedMethodProcessor p2 = new ImprovedMethodProcessor();
		ImprovedMethodProcessor p3 = new ImprovedMethodProcessor();
		processor0.schedule(p1,0,1);
		processor1.schedule(p2,0,1);
		processor2.schedule(p3,0,1);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				//increment every 1 ms
				currentTime++;
				//System.out.println(currentTime + "*");
					
				//if not done and a job is arriving
				if(i<jobs.size() && currentTime == jobs.get(i).arrivalTime()){
					//add to wait queue and pick which processor to put it on
					waitQueue.add(jobs.get(i));
					if(waitQueue.get(i).jobNumber() == 1){
						p1.addJob(waitQueue.get(i));
						j = 1;
					}
					else if(p1.getStatus()==0){
						p1.addJob(waitQueue.get(i));
						j = 1;
					}
					else if(p2.getStatus()==0){
						p2.addJob(waitQueue.get(i));
						j = 2;
					}
					else if(p3.getStatus()==0){
						p3.addJob(waitQueue.get(i));
						j = 3;
					}
					else{
						waitQueue.add(jobs.get(i));
					}
					i++;
						
				}
				//if done with jobs
				else if(i >= jobs.size() && p1.getStatus()==0 && p2.getStatus()==0 && p3.getStatus()==0){
					if(p1.getStartTime() <= p2.getStartTime() && p1.getStartTime() <= p3.getStartTime()){
						startTime = p1.getStartTime();
					}
					else if(p2.getStartTime() <= p1.getStartTime() && p2.getStartTime() <= p3.getStartTime()){
						startTime = p2.getStartTime();
					}
					else{
						startTime = p3.getStartTime();
					}
					if(p1.getEndTime() >= p2.getEndTime() && p1.getEndTime() >= p3.getEndTime()){
						endTime = p1.getEndTime();
					}
					else if(p2.getEndTime() >= p1.getEndTime() && p2.getEndTime() >= p3.getEndTime()){
						endTime = p2.getEndTime();
					}
					else{
						endTime = p3.getEndTime();
					}
					
					//turnaround calculations
					overallTurnaround = endTime - startTime;
					oneHundredTurnaround2[m]=overallTurnaround;
					m++;
					
					System.out.println("Overall Start Time: " + startTime);
					System.out.println("Overall End Time: " + endTime);
					System.out.println("Overall Turnaround Time: " + overallTurnaround);
					//stop timers
					timer.cancel();
					timer.purge();
					p1.cancel();
					p2.cancel();
					p3.cancel();
					processor0.cancel();
					processor1.cancel();
					processor2.cancel();
					processor0.purge();
					processor1.purge();
					processor2.purge();
					i = 0;
					currentTime = 0;
				}
	
			}
		}, 0, 1);
		
		
	}
	
}
