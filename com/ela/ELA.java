package com.ela;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


@WebServlet("/ELA")
public class ELA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	
    public static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File doesn\'t exist");
            return -1;
        }
        return file.length();
    }
       

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out1 = response.getWriter();
		String L3c_Url = request.getParameter("name[name]");
	    String L3c_Feeds = request.getParameter("password[password]");
	    String Status = request.getParameter("status");
	    String StatusLocation="C:\\Users\\Administrator\\Desktop\\apache-tomcat-9.0.69\\tools\\status.txt";
	        
	    
	     
	      
	      
	 // if status is false, a thread is created to run ELA Changes       
    if(Status.equals("false"))
    {
        ELAchanges change =new ELAchanges();
        change.elabuildchanges(L3c_Url,L3c_Feeds);	
        out1.println("started");

    }
    // if status is true, Status is fetched from file and displayed to user 
   else
     {
	   JSONObject jo = new JSONObject();
	    String value;
	     File file = new File(StatusLocation);
       Scanner myReader = new Scanner(file);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
         if (data.contains("Failed")) {
       	   value="Failed";
       	 jo.put("message", data);
       	   
          }  else {
       	   value ="continue";
       	 jo.put("message", data);
       	   if(data.contains("ELA restarting"))//check if ELA is restarted and if restarted change status to success or continue
       			   {
       		   try {
       		 URL url = new URL("http://localhost:8400/");
             HttpURLConnection huc = (HttpURLConnection) url.openConnection();

             int responseCode = huc.getResponseCode();

             System.out.println(responseCode);
             if(responseCode==200) {
             	  System.out.println("loop2");
	     			data ="ELA started. Use http://ela-w2019-6:8400 webclient ";
	     			value = "success";
	     			 jo.put("message", data);
             }
       			   }
       	   catch(Exception e)
       	   {
       		   
       	   }
       			   }
       	   
          }
        
       
        jo.put("status", value);
       out1.println(jo);
         
     }
	
     }
	        
	
	        
	     

	}
	}


