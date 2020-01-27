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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class HelpController implements Initializable  {
	@FXML	private AnchorPane Controller;
	@FXML 	private TextArea Text;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Text.setStyle("-fx-font-size:15");
		try{
		Scanner s = new Scanner(new File(System.getProperty("user.dir")+File.separator+"/Application_Guide.txt")).useDelimiter("##");
			  while (s.hasNext()) {
	            if (s.hasNextInt()) { // check if next token is an int
	            	Text.appendText(s.nextInt() + " "); 					// display the found integer
	            } else {
	            	Text.appendText(s.next() +"  " ); 						// else read the next token
		            } 														// else read the next token
	        }
	    } catch (FileNotFoundException ex) {
	        System.err.println(ex);
	    }
 	}
		
	}