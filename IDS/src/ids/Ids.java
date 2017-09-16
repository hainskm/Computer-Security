/*
 * Code written by Kevin Hainsworth and Brendan Blanchard
 */
package ids;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jnetpcap.Pcap;   
import org.jnetpcap.packet.JPacket;  
import org.jnetpcap.packet.JPacketHandler;  
import org.jnetpcap.protocol.tcpip.Http;  
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class Ids {
	/** 
     * 
     *  
     * @param args 
     *          none expected 
     */  
	private static String hostIP = null;
	private static String policyName = null;
    private static String policyType = null;
    private static String protocol = null;
    private static String host_port = null;
    private static String attacker_port = null;
    private static String attackerIP = null;
    private static ArrayList<String> from_host = new ArrayList<String>();
    private static ArrayList<String> to_host = new ArrayList<String>();
    
    private static String runningPackets = "";
    
    public static void main(String[] args) {  
  
        final String FILENAME = args[0]; 
        final String POLICY = args[1];
        
        
        BufferedReader br = null;
        FileReader fr = null;
        try {

			fr = new FileReader(POLICY);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(POLICY));
			//Split up information in policy file and store it
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				if(sCurrentLine.matches("host=.*")){
					
					String temp = sCurrentLine.substring(5, sCurrentLine.length());
					hostIP = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("name=.*")){
					String temp = sCurrentLine.substring(5, sCurrentLine.length());
					policyName = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("type=.*")){
					String temp = sCurrentLine.substring(5, sCurrentLine.length());
					policyType = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("proto=.*")){
					String temp = sCurrentLine.substring(6, sCurrentLine.length());
					protocol = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("host_port=.*")){
					String temp = sCurrentLine.substring(10, sCurrentLine.length());
					host_port = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("attacker_port=.*")){
					String temp = sCurrentLine.substring(14, sCurrentLine.length());
					attacker_port = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("attacker=.*")){
					String temp = sCurrentLine.substring(9, sCurrentLine.length());
					attackerIP = temp;
					//System.out.println(temp);
				}
				else if(sCurrentLine.matches("to_host=.*")){
					String temp = sCurrentLine.substring(8, sCurrentLine.length());
					temp = temp.substring(1, temp.length()-1);
					to_host.add(temp);
					//System.out.println(to_host.get(to_host.size()-1));
				}
				else if(sCurrentLine.matches("from_host=.*")){
					String temp = sCurrentLine.substring(10, sCurrentLine.length());
					temp = temp.substring(1, temp.length()-1);
					from_host.add(temp);
					//System.out.println(to_host.get(to_host.size()-1));
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}

        final StringBuilder errbuf = new StringBuilder();  
        
         final Pcap pcap = Pcap.openOffline(FILENAME, errbuf);  
        if (pcap == null) {  
            System.err.println(errbuf); // Error is stored in errbuf if any  
            return;  
        }  
        
        
        pcap.loop(Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>() {  
  
            final Tcp tcp = new Tcp();  
            final Udp udp = new Udp();
            /* 
             * Same thing for our http header 
             */  
            final Http http = new Http();  
            //ArrayList<Connection> connections = new ArrayList<Connection>();
            boolean alert1 = false;
            boolean alert2 = false;
            boolean alert3 = false;
            boolean alert4 = false;
            boolean printed = false;
            int i = 0;
            int j = 0;
            int k = 0;
            public void nextPacket(JPacket packet, StringBuilder errbuf) {
            	
            	boolean f = false;
            	int hostP = -50;
            	//Code for test case 5
            	//Check if a udp packet
            	if(packet.hasHeader(Udp.ID)){
            		//get the header
            		packet.getHeader(udp);
            		//check that we're using the TFTP policy
            		if(policyName.equals("TFTP attacker boot")){
            			//check that the attacker is receiving
            			if(udp.destination()==Integer.parseInt(attacker_port)){
            				//Get the payload
            				String payload= new String(udp.getPayload());
            				//System.out.println(payload);
            				
            				//if it contains the from_host string in the policy
            				if(payload.contains(from_host.get(0))){
            					//save that host port
            					hostP = udp.source();
            					System.out.println("Found " + hostP);
            					f = true;
            				}
            			}
            			//THIS PART MAY NOT BE WORKING YET
            			//check if the attacker sending to the host port found from above
            			if(udp.source()==Integer.parseInt(attacker_port) /*&& udp.destination()==hostP*/){
            				//get payload
            				String payload= new String(udp.getPayload());
            				//System.out.println(payload);
            				//check if it contains the string and the attacker already received the msg from the host
            				if(payload.matches(to_host.get(0)) && f && udp.destination()==hostP){
            					//if it does, print alert
            					System.out.println("Alert! Policy breach found!");
                        		System.out.println("Attacker Port: " + udp.source());
            					
            				}
            			}
                    }
            		
            	}
            	
            	
                if (packet.hasHeader(Tcp.ID)) {  
                	
                    /* 
                     * Now get our tcp header definition (accessor) peered with actual 
                     * memory that holds the tcp header within the packet. 
                     */  
                    packet.getHeader(tcp);
                    //System.out.println(tcp.getPacket().toString());
                    if(policyType.equals("stateful")){
                    	String payload1 = new String(tcp.getPayload());
                    	
                    	runningPackets=runningPackets+payload1;
                    	                    	//Test case 2
                    	if(policyName.equals("Blame Attack 2")){
                    		if(tcp.destination()==Integer.parseInt(host_port)){
                        		//get the payload
                            	String payload = new String(tcp.getPayload());
                            	//System.out.println(payload);
                            	//if it contains the string in the policy
                            	//System.out.println(to_host.get(0));
                            	if(runningPackets.contains(to_host.get(0))){
                            		//print alert
                            		if(!printed){
                            			System.out.println("Alert! Policy breach found!");
                                		System.out.println("Attacker Port: " + tcp.source());
                                		printed = true;
                            		}
                            		
                            		
                            	}
                            }
                        }
                    	//Test case 3
                    	else if(policyName.equals("Buffer Overflow")){
                    		if(tcp.destination()==Integer.parseInt(host_port)){
                        		//get the payload
                            	String payload = new String(tcp.getPayload());
                            	//System.out.println(payload);
                            	//if it contains the string in the policy
                            	//System.out.println(to_host.get(0));
                            	if(runningPackets.matches(to_host.get(0))){
                            		//print alert
                            		System.out.println("Alert! Policy breach found!");
                            		System.out.println("Attacker Port: " + tcp.source());
                            	}
                            }
                        }
                    	//test case 4

                    	else if(policyName.equals("Plaintext POP")){
                    		
                    		if(tcp.source()==Integer.parseInt(host_port)){
                    			String payload = new String(tcp.getPayload());
                            	//System.out.println(payload);
                            	//if it contains the string in the policy
                            	if(payload.matches(from_host.get(0)) && !alert1){
                            		alert1 = true;
                            		
                            	}
                            	else if(payload.matches(from_host.get(1))&&!alert3){
                            		if(alert1&&alert2){
                            			alert3 = true;
                            			
                            		}
                            	}
                            	else if(payload.matches(from_host.get(2))&&alert4){
                            		if(alert1&&alert2&&alert3&&alert4){
                            			//print alert
                            			System.out.println("Alert! Policy breach found!");
                                		System.out.println("Attacker Port: " + tcp.source());
                                		
                            		}
                            	}
                            	else if(payload.equals("")){
                            		
                            	}
                            	else{
                            		alert1 = false;
                            		alert2 = false;
                            		alert3 = false;
                            		alert4 = false;
                            		
                            	}
                    		}
                    		if(tcp.destination()==Integer.parseInt(host_port)){
                        		//get the payload
                            	String payload = new String(tcp.getPayload());
                            	//System.out.println(payload);
                            	//if it contains the string in the policy
                            	
                            	if(payload.matches(to_host.get(0))){
                            		if(alert1){
                            			alert2 = true;
                            		}
                            	}
                            	
                            	else if(payload.matches(to_host.get(1))){
                            		if(alert1&&alert2&&alert3){
                            			alert4 = true;
                            		}
                            	}
                            	else if(payload.equals("")){
                            		
                            	}
                            	else{
                            		alert1 = false;
                            		alert2 = false;
                            		alert3 = false;
                            		alert4 = false;
                            	}
                            	
                            }
                    		
                        }

                	}
                    else if(policyType.equals("stateless")){
                    	//Check which policy is being used
                        //Test case 1
                    	if(policyName.equals("Blame Attack 1")){
                        	//check the packet is going to host port
                        	if(tcp.destination()==Integer.parseInt(host_port)){
                        		//get the payload
                            	String payload = new String(tcp.getPayload());
                            	//System.out.println(payload);
                            	//if it contains the string in the policy
                            	if(payload.contains(to_host.get(0))){
                            		//print alert
                            		System.out.println("Alert! Policy breach found!");
                            		System.out.println("Attacker Port: " + tcp.source());
                            	}
                            }
                        }
                    }
            
                }  
            }  
            
  
        }, errbuf);

        pcap.close();  
  
    }  
} 
    