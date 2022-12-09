package com.ela;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;



public class ELAchanges extends Thread{
	


	public void elabuildchanges(String L3c_Url, String L3c_Feeds)
	{
		
		
		ELAchanges r1=new ELAchanges();    
	        Thread t1 =new Thread() {
	        	@Override
	        	public void run() {
	        		//Location of all files
	        		String StatusLocation="C:\\Users\\Administrator\\Desktop\\apache-tomcat-9.0.69\\tools\\status.txt";
	        		String ELALocation = "C:\\ManageEngine\\gokulraj2\\EventLogAnalyzer\\bin";
	        		String AgentproLocation="C:\\ManageEngine\\gokulraj2\\EventLogAnalyzer\\conf\\Log360Agent\\agent.properties";
	        		String oldcacert ="C:\\Users\\Administrator\\Desktop\\apache-tomcat-9.0.69\\tools\\old\\cacerts";
	        		String newcacert ="C:\\Users\\Administrator\\Desktop\\apache-tomcat-9.0.69\\tools\\\\cacerts";
	        		String CacertLocation ="C:\\ManageEngine\\gokulraj2\\EventLogAnalyzer\\jre\\lib\\security\\Cacerts";
	        		
	        		 FileWriter status;
	        		 int i=0;
	        			try {
							status = new FileWriter(StatusLocation);
							status.write("ELA shutdown going on");
			     			status.close();	
						} catch (IOException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
		     		    
	     		
	        		//ELA Shutdown
	        		try {
	        			int o=0;
	        		
	        			 while(o==0) {
	        	        File dir = new File(ELALocation);
	        	        Process powerShellProcess = Runtime.getRuntime().exec("shutdown.bat", null, dir);
	        	        // Getting the results
	        	        powerShellProcess.getOutputStream().close();
	        	        String line;
	        	        System.out.println("Standard Output:");
	        	        BufferedReader stdout = new BufferedReader(new InputStreamReader(
	        	                powerShellProcess.getInputStream()));
	        	        int k=0;
	        	        while ((line = stdout.readLine()) != null) {
	        	        	if(line.contains("Server Already Shutdown"))
	        	            {
	        	        		status = new FileWriter(StatusLocation,false);
		 	     	     		 status.write("ELA stopped. changing files");
		 	     	     		status.close();
		 	     	     		++o;
		 	     	     		break;
		 	     	     		}
	        	      
	        	        }
	        	        stdout.close();
	        	        System.out.println(k);
	        	        System.out.println("Standard Error:");
	        	        BufferedReader stderr = new BufferedReader(new InputStreamReader(
	        	                powerShellProcess.getErrorStream()));
	        	        while ((line = stderr.readLine()) != null) {
	        	            System.out.println(line);
	        	        }
	        	        stderr.close();
	        			 }
	        	    	        	
	     				
	        		} catch (IOException e) {	
	        			e.printStackTrace();
	        		}
	     				
	        	
	        		try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e2) {						
						e2.printStackTrace();
					}

	 	                
	 	                
	        	
	     				
	              String maingrid="localmanageengine.com";
	              String localgrid="localzoho.com";
	              
	              
	               
	               
	              // Maingrid & local grid Changes
	              // changes done according to https://learn.zoho.com/portal/zohocorp/knowledge/manual/knowledge-base-4/article/steps-to-sync-ela-to-l3c
	              if(L3c_Url.contains(maingrid)||L3c_Url.contains(localgrid))
	                {
	                    try {
	        
	                // local and maingrid uses 101kb cacert file. so checking for size..
	                // if size is greater then 200kb. will delete the file and will add a 101kb cacert
	                File cacerts = new File(CacertLocation);
	     	        long Size = cacerts.length();
	     	        System.out.println("Filesize in bytes: " + Size);
	                if(Size>200000)
	                {
	                    File file
	                        = new File(CacertLocation);
	        
	                if (file.delete()) {
	                    System.out.println();
	                    status = new FileWriter(StatusLocation,false);
		     		    status.write("cacerts Transfer Going on");
		     			status.close();
	                }
	                else {
	                	
	                	
	                	 status = new FileWriter(StatusLocation,false);
			     		    status.write("Failed while transfering cacerts file");
			     			status.close();
	                }
	                }
	                
	                TimeUnit.SECONDS.sleep(1);
	                
	                // Transfering Cacert files
	              FileSystem fileSys = FileSystems.getDefault();
	              Path srcPath = fileSys.getPath(oldcacert);
	              Path destPath = fileSys.getPath(CacertLocation);
	              try {
	                 
	                  Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
	              } catch (IOException e) {
	            	  status = new FileWriter(StatusLocation,false);
		     		    status.write("Failed while transfering cacerts file");
		     			status.close();
	              }
	              } catch (InterruptedException e) {
	          
	                  } catch (IOException e) {
					e.printStackTrace();
				}
	                    try { //changing agent.properties entries
	                  FileInputStream in = new FileInputStream(AgentproLocation);
	                  Properties props = new Properties(); 
						props.load(in);
						 in.close();
						 
						 FileOutputStream out = new FileOutputStream(AgentproLocation);
		                  props.setProperty("log360.host.url", L3c_Url);
		                  props.setProperty("log360.threat.host.url", L3c_Feeds);
		                  props.store(out, null);
		                  out.close();
		                  
					} catch (IOException e) {
						
						e.printStackTrace();
					}
	                 
	  
	                 
	                  try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
					
						e.printStackTrace();
					}
	                  
	        	}else
	        	{ //Live server changes
	        		 try {
	        		        
	        		        //Live server uses 200kb cacert. so delete existing cacert file and add a 200kb cacert file
	 	                File cacerts = new File(CacertLocation);
	 	     	        long Size = cacerts.length();
	 	     	        System.out.println("Filesize in bytes: " + Size);
	 	                if(Size>200000)//check if cacert size is greater then 200kb
	 	                {
	 	                    File file
	 	                        = new File(CacertLocation);
	 	        
	 	                if (file.delete()) {
	 	                    System.out.println();
	 	                    status = new FileWriter(StatusLocation,false);
	 		     		    status.write("cacerts Transfer Going on");
	 		     			status.close();
	 	                }
	 	                else {
	 	                	
	 	                	
	 	                	 status = new FileWriter(StatusLocation,false);
	 			     		    status.write("Failed while transfering cacerts file");
	 			     			status.close();
	 	                }
	 	                }
	 	                
	 	                TimeUnit.SECONDS.sleep(1);
	 	                
	 	              FileSystem fileSys = FileSystems.getDefault();
	 	              Path srcPath = fileSys.getPath(newcacert);
	 	              Path destPath = fileSys.getPath(CacertLocation);
	 	              try {
	 	                 
	 	                  Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
	 	              } catch (IOException e) {
	 	            	  status = new FileWriter(StatusLocation,false);
	 		     		    status.write("Failed while transfering cacerts file");
	 		     			status.close();
	 	              }
	 	              } catch (InterruptedException e) {
	 	          
	 	                  } catch (IOException e) {
	 					e.printStackTrace();
	 				}
	 	                    try { //changing agent.property file
	 	                  FileInputStream in = new FileInputStream(AgentproLocation);
	 	                  Properties props = new Properties(); 
	 						props.load(in);
	 						 in.close();
	 						 
	 						 FileOutputStream out = new FileOutputStream(AgentproLocation);
	 		                  props.setProperty("log360.host.url", L3c_Url);
	 		                  props.setProperty("log360.threat.host.url", L3c_Feeds);
	 		                  props.store(out, null);
	 		                  out.close();
	 		                  
	 					} catch (IOException e) {
	 						
	 						e.printStackTrace();
	 					}
	 	                 
	 	  
	 	                 
	 	                  try {
	 						TimeUnit.SECONDS.sleep(1);
	 					} catch (InterruptedException e) {
	 					
	 						e.printStackTrace();
	 					}
	        		
	        	}
	              
	     		    try {

	  	              status = new FileWriter(StatusLocation,false);
						status.write("ELA restarting");
						
						status.close();
					} catch (IOException e2) {
						
						e2.printStackTrace();
					}
	     			
	              //ELA Start
	            try {
	            
	            	 


	            	   ProcessBuilder builder = new ProcessBuilder(
	                           "cmd.exe", "/c", "cd \"C:\\ManageEngine\\gokulraj2\\EventLogAnalyzer\\bin\" && run.bat");
	            	   System.out.println("loop1");
	                   builder.redirectErrorStream(true);
	                   Process p = builder.start();
	                   System.out.println("loop2");
	                   BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	                   String line;
	                   System.out.println("loop3");
	                   while (true) {
	                       line = r.readLine();
	                       if (line == null) { break; }
	                     
	                       if(line.contains("Connect to: [ http://localhost:8400 ]"));
	                       {
	                    	   System.out.println(line);
	                       }
	                       
	                   }
	                   System.out.println("loop4");

	                   
	   	      
	           
	        } catch (IOException e) {
	        	 System.out.println(e);
	        }
	       
	     		    

	     		    
			
	        }};
	        t1.start();  
	       
		
	}

}
