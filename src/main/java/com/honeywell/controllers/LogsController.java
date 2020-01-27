package com.honeywell.controllers;



/** 
 * 
 * @author H264637 (Arjun Gambhir)
 *
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class LogsController implements Initializable {
    @FXML TextArea LOGS;
    @FXML Button Logs;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGS.setPromptText("System will efficiently generate logs when lines in log file < 100000 .. the file is reading logs at rate of 5000 lines per min so it is recommended to use this plane when logs are less in number ");
	}
	@FXML
	    private void Logs(ActionEvent event) {
	   	  	try {
			    Scanner s = new Scanner(new File(System.getProperty("user.dir")+File.separator+"/MigrationLogSummary.log")).useDelimiter("INFO");
		      int i=0;
				  while (s.hasNext()) {
					  
		            if (s.hasNextInt()) { // check if next token is an int
		                LOGS.appendText((i+1)+"  INFO "+s.nextInt() + " "); // display the found integer
		                i=i+1;
		            } else {
		            	LOGS.appendText((i+1)+"  INFO "+s.next() +"  " ); // else read the next token
		                i=i+1;
		            } 
		        }
		        
		    } catch (FileNotFoundException ex) {
		       System.err.println(ex);
		    }
	 }
		 @FXML
		    private void Clear(ActionEvent event) {
			LOGS.setPromptText("You have cleared the logs please press the LOGS Button to see the current session logs");
			LOGS.setText("");
		 }
	}
	
	 
	 
