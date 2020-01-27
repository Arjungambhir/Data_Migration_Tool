package com.honeywell.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


/** 
 * This is the Controller class for sample.fxml 
 * this will control all the workflows we had in migration homepage.... 
 * @author H264637 (Arjun Gambhir)
 *
 */

public class Controller implements Initializable{
    @FXML ComboBox<String> driver;
    @FXML ComboBox<String> driver1;
    @FXML TextField url,url1,userid,userid1,dbname,dbname1,srcscema,dstscema;
    @FXML TextField SrcTables,DstTables,SrcTables1,DstTables1,LOGS,Hostname,Port,Hostname1,Port1;
    @FXML PasswordField password,password1;
    @FXML private TableView tableView;
    @FXML ProgressIndicator Indicator;  
    @FXML Button Prepare,migration,validation;
    
  
    TableColumn name = new TableColumn("NAME");
    TableColumn srcrows = new TableColumn("ROWS AT SOURCE");
    TableColumn dstrows = new TableColumn("ROWS AT DESTINATION");
    TableColumn srno = new TableColumn("SERIAL NO.");
    Logger logger = Logger.getLogger("MigrationDetailedLog");  
    Logger logger1 = Logger.getLogger("MigrationLogSummary");  
    FileHandler fh;  
    FileHandler fh1;  
    
   @FXML
    public void initialize(URL urli, ResourceBundle rb) {
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        list.add("Oracle");
        list.add("SQL MS Server");
        list1.add("HSQLDB");
        ObservableList obList = FXCollections.observableList(list);
        ObservableList obList1 = FXCollections.observableList(list1);
        
        driver.setItems(obList1);
        driver1.setItems(obList);
        driver.getSelectionModel().selectFirst();
        driver1.getSelectionModel().selectFirst();
        url1.setEditable(false);           //url fields only for user refrance so its not editable 
        url.setEditable(false);
        
        

        Hostname.setText("localhost");
        Port1.setText("9101");
        Hostname1.setText("localhost");
        Port.setText("1521");
        dbname.setText("vcdb");
        dbname1.setText("xe");
        userid.setText("SA");
        userid1.setText("VCD_3");
        password1.setText("talkman");
        password.setText("");
        dstscema.setText("VCD_3");
        srcscema.setText("PUBLIC");
    
/**        
        Hostname.setText("localhost");
        Port1.setText("9101");
        Hostname1.setText("localhost");
        Port.setText("1433");
        dbname.setText("vcdb");
        dbname1.setText("VCD_1");
        url.setPromptText("jdbc:hsqldb:<database location>");
        url.setText("jdbc:hsqldb:C:\\hsqldb-2.4.1\\hsqldb\\hsqldb\\demodb.tmp");
        url1.setPromptText("jdbc:<database.type>://<Hostname>:<Port>;databaseName=<Dbname>");
        userid.setPromptText("SA");
        userid.setText("SA");
        userid1.setPromptText("SA");
        userid.setText("SA");
        dbname.setPromptText("testdb");
        password1.setText("talkman@1768");
        password.setText("");
        driver1.getSelectionModel().select(1);
        userid1.setText("vc1");
        dstscema.setText("dbo");
        srcscema.setText("PUBLIC");
  	  **/

        SrcTables.setPromptText("000");
        DstTables.setPromptText("000");
        SrcTables1.setPromptText("000");
        DstTables1.setPromptText("000");
        SrcTables.setEditable(false);
        DstTables.setEditable(false);
        SrcTables1.setEditable(false);
        DstTables1.setEditable(false);
        name.setPrefWidth(250.0);
        srno.setPrefWidth(80.0);
        srcrows.setPrefWidth(155.0);
        dstrows.setPrefWidth(155.0);
        
        srno.setStyle("-fx-alignment: CENTER;");
        srcrows.setStyle("-fx-alignment: CENTER;");
        dstrows.setStyle("-fx-alignment: CENTER;");
        
        migration.setDisable(true);
    //    validation.setDisable(true);
        
        tableView.getColumns().addAll(srno,name,srcrows,dstrows);
    
        /** This block configure the logger with handler and formatter  
   	 		mylog.log contain complete detailed log and Logs_Summary.log contains all application 
   	 		high level log summary 
   		**/ 
        try {  
        	String s = (System.getProperty("user.dir")).replace("\\", "/");
        	fh = new FileHandler(s+"/MigrationDetailedLog.log");  
            fh1 = new FileHandler(s+"/MigrationLogSummary.log");  
            logger.addHandler(fh);
            logger1.addHandler(fh1);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);
            fh1.setFormatter(formatter);
        	} 
        catch (SecurityException e) {
        	logger.warning("User is not authorized to create file at specified location ");
        	logger1.warning("User is not authorized to create file at specified location "); }
        catch (IOException e) {  
        	logger.warning("Unable to create the log file at specified location");
        	logger1.warning("Unable to create the log file at specified location"); }
           
           }

    
   // Test Source Connection button action implementation 
   
    @FXML
    private void handleClickSrcCon(ActionEvent event) {
    	
   	    url.setText("jdbc:hsqldb:hsql://"+Hostname1.getText()+":"+Port1.getText()+"/"+dbname.getText()+";default_table_type=cached;");
    	logger.info("Testing Connection With Source with given database information....");
       	String srcDriver = driver.getValue();
        String srcUrl = url.getText(); 
        String srcUser = userid.getText();
        String srcDb = dbname.getText();
        String srcPass = password.getText();
        DataBaseConnection src = new DataBaseConnection();
        Connection srccon,tsrccon;

        src.setDriver(srcDriver);
        src.setName(srcDb);
        src.setPassword(srcPass);
        src.setUrl(srcUrl);
        src.setUser(srcUser);
        try {
            srccon = src.initConnection();
            Dialog alert = new Alert(Alert.AlertType.INFORMATION, "Connection to Source Database is established successfully");
            logger.info("Connection to Source Database is established successfully....");
            alert.setTitle("SOURCE CONNECTION");
            alert.setHeaderText("SOURCE CONNECTION");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                alert.close();
            }
            } 
        
        catch (ClassNotFoundException e) {
                Dialog alert = new Alert(Alert.AlertType.ERROR, "Incorrect driver for selected database please check url");
                logger.info("Incorrect driver for selected database please check url....");
                alert.setTitle("SOURCE DRIVER ERROR");
                alert.setHeaderText("SOURCE DRIVER ERROR");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    alert.close();
                }
        }
        catch (SQLException e){
            Dialog alert = new Alert(Alert.AlertType.ERROR, "Error in provided Source Database configuration please refer application guide for input standard format");
            logger.info("Error in provided Source Database configuration please refer application guide for input standard format....");
            alert.setTitle("SOURCE ERROR");
            alert.setHeaderText("SOURCE ERROR");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                alert.close();
            }
        }
       
    }
    
 // Test Destination Connection button action implementation 
    
    @FXML
    private void handleClickdstCon(ActionEvent event) {
    	if (driver1.getValue()=="Oracle")
    	{url1.setText("jdbc:oracle:thin:@"+Hostname.getText()+":"+Port.getText()+":"+dbname1.getText());}
    	else{url1.setText("jdbc:sqlserver://"+Hostname.getText()+":"+Port.getText()+";databaseName="+dbname1.getText());}
    	
    	
    	logger.info("Testing Connection With Source with given database information....");
    	String dstDriver = driver1.getValue();
        String dstUrl = url1.getText();
        String dstUser = userid1.getText();
        String dstDb = dbname1.getText();
        String dstPass = password1.getText();
        DataBaseConnection dst = new DataBaseConnection();
       
        Connection dstcon,tdstcon;

        dst.setDriver(dstDriver);
        dst.setName(dstDb);
        dst.setPassword(dstPass);
        dst.setUrl(dstUrl);
        dst.setUser(dstUser);
        try {
            dstcon = dst.initConnection();
            Dialog alert = new Alert(Alert.AlertType.INFORMATION, "Connection to Destination Database is established successfully");    
            logger.info("Connection to Destination Database is established successfully....");
            alert.setTitle("DESTINATION CONNECTION");
            alert.setHeaderText("DESTINATION CONNECTION");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                alert.close();
            }
            
        } 
        catch (ClassNotFoundException e) {
                Dialog alert = new Alert(Alert.AlertType.ERROR, "Incorrect driver for selected database please check url and database");
                logger.info("Incorrect driver for selected database please check url....");
                alert.setTitle("DESTINATION DRIVER ERROR");
                alert.setHeaderText("DESTINATION DRIVER ERROR");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    alert.close();
                }
        }
        catch (SQLException e){
            Dialog alert = new Alert(Alert.AlertType.ERROR, "Error in provided Destination Database configuration please refer application guide for input standard format");
            logger.info("Error in provided Destination Database configuration please refer application guide for input standard format....");
            alert.setTitle("DESTINATION ERROR");
            alert.setHeaderText("DESTINATION ERROR");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                alert.close();
            }
        }
      
    }
    
 // Migration button action implementation 
        @FXML
    public void handleClick(ActionEvent event) {
        tableView.getItems().clear();	
        String srcDriver = driver.getValue();
        String dstDriver = driver1.getValue();
        String srcUrl = url.getText();
        String dstUrl = url1.getText();
        String srcUser = userid.getText();
        String dstUser = userid1.getText();
        String srcDb = dbname.getText();
        String dstDb = dbname1.getText();
        String srcPass = password.getText();
        String dstPass = password1.getText();
        DataBaseConnection src = new DataBaseConnection();
        DataBaseConnection dst = new DataBaseConnection();
        String scemasrc = srcscema.getText();
        String scemadst = dstscema.getText();
        String s= "%";
        
        
        if(srcUser.isEmpty()||srcDb.isEmpty()||scemasrc.isEmpty()||dstUser.isEmpty()||dstDb.isEmpty()||scemadst.isEmpty())
        {
        	Dialog alert3 = new Alert(Alert.AlertType.ERROR, "PLease enter all the Mandetory fields.. ");
       	 Optional<ButtonType> result1 = alert3.showAndWait();
            if (result1.isPresent() && result1.get() == ButtonType.OK) {
                alert3.close();
                
            }  
        }
        
        if (scemasrc.contains("%") || scemasrc.contains("*") ||scemadst.contains("%") ||scemadst.contains("*"))
        {
        	Dialog alert3 = new Alert(Alert.AlertType.ERROR, "PLease enter Valid Schema names.. ");
          	 Optional<ButtonType> result1 = alert3.showAndWait();
               if (result1.isPresent() && result1.get() == ButtonType.OK) {
                   alert3.close();
          }
        }
        else
        {
        Connection srccon,dstcon;
        src.setDriver(srcDriver);
        src.setName(srcDb);
        src.setPassword(srcPass);
        src.setUrl(srcUrl);
        src.setUser(srcUser);
        try {
            srccon = src.initConnection();
        	logger.info("Connected to  :"+srcDb);
        	logger1.info("Connected to  :"+srcDb);
            dst.setDriver(dstDriver);
            dst.setName(dstDb);
            dst.setPassword(dstPass);
            dst.setUrl(dstUrl);
            dst.setUser(dstUser);
            try {
                if( dstDriver.equals(srcDriver) && dstUrl.equals(srcUrl) && dstPass.equals(srcPass) && dstDb.equals(srcDb) && dstUser.equals(srcUser)){
                    throw new Exception();
                }
                dstcon = dst.initConnection();
                logger.info("Connected to  :"+dstDb);
            	logger1.info("Connected to  :"+dstDb);
            	CoreMigration migrator = new CoreMigration(srccon,srcDb,dstcon,dstDb,dstDriver,scemasrc,scemadst);
                int countalltablesfromdst = migrator.countalltablesfromdst();
                
                if (countalltablesfromdst>0)
                {
                	Dialog alert = new Alert(Alert.AlertType.CONFIRMATION, "The destination database :"+dstDriver+" scema : "+scemadst+" has "+countalltablesfromdst+" table you want to delete them...");
        	        alert.setTitle("DATA FOUND IN DESTINATION..");
                    alert.setHeaderText("DELETE ALL DESTINATION DATA..");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                    	alert.close();
                    	logger.info("Starting deleting all existing data from :"+dstDb);
                    	logger1.info("Starting deleting all existing data from :"+dstDb);
                       	migrator.prepare();
                       	logger1.info("Successfully deleted all existing data from :"+dstDb);
                       	logger.info("Successfully deleted all existing data from :"+dstDb);
                       	logger.info("Starting Migration from :"+srcDb+"Database to :"+dstDb+"....");
                       	logger1.info("Starting Migration from :"+srcDb+"Database to :"+dstDb+"....");
                       	
                       	migrator.start();
                    	
                       	logger1.info("Data is successfully migrated from : "+srcDriver+"  to :"+dstDriver+"....For more migration summary refer above details");
                        Dialog alert1 = new Alert(Alert.AlertType.INFORMATION, "Migration of and data from :"+srcDriver+" Schema  "+scemasrc+ " to "+dstDriver+" Schema  "+scemadst+"  Completed Successfully");
             	        logger.info("Data is successfully migrated from : "+srcDriver+"  to :"+dstDriver+"....");
                        alert1.setTitle("Migration Successfull..");
                        alert1.setHeaderText("Migration");
                         Optional<ButtonType> result1 = alert1.showAndWait();
                         if (result1.isPresent() && result1.get() == ButtonType.OK) {
                             alert1.close();
                             }  
                         }
                    else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    	alert.close();
                    	Dialog alert2 = new Alert(Alert.AlertType.INFORMATION, "Migration aborded ....! ");
                    	 Optional<ButtonType> result1 = alert2.showAndWait();
                         if (result1.isPresent() && result1.get() == ButtonType.OK) {
                             alert2.close();
                         }  
                        }
                    }
                
                else
                {
               	 logger.info("Starting Migration from :"+srcDb+"Database to :"+dstDb+"....");
                 migrator.start();
                 Dialog alert3 = new Alert(Alert.AlertType.INFORMATION, "Migration of and data from :"+srcDriver+" Schema  "+scemasrc+ " to "+dstDriver+" Schema  "+scemadst+"  Completed Successfully");
     	         logger.info("Data is successfully migrated from : "+srcDriver+"  to :"+dstDriver+"....");
                 alert3.setTitle("INFORMATION");
                 alert3.setHeaderText("Migration");
                 Optional<ButtonType> result1 = alert3.showAndWait();
                 if (result1.isPresent() && result1.get() == ButtonType.OK) {
                     alert3.close();
                     Indicator.setVisible(false);
                 }  
                }
            } catch (ClassNotFoundException e) {
                Dialog alert = new Alert(Alert.AlertType.ERROR, "Incorrect driver for selected database please check url and database");
                logger.info("Incorrect driver for selected database please check url....");
                alert.setTitle("WRONG DATABASE DETAILS");
                alert.setHeaderText("WRONG DATABASE DETAILS");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    alert.close();
                }
            } catch (SQLException e) {
                Dialog alert = new Alert(Alert.AlertType.ERROR, "Destination Configuration Error");
                logger.info("Error in provided Destination Database configuration please refer application guide for input standard format....");
                alert.setTitle("WRONG DST DATABASE DETAILS");
                alert.setHeaderText("WRONG DST DATABASE DETAILS");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    alert.close();
                }
            }catch (Exception e) {
                Dialog alert = new Alert(Alert.AlertType.ERROR, "\n" + "Error in provided Destination Database configuration please refer application guide for input standard format");
                logger.info("Error in provided Destination Database configuration please refer application guide for input standard format....");
                alert.setTitle("WRONG DST DATABASE DETAILS");
                alert.setHeaderText("WRONG DST DATABASE DETAILS");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    alert.close();
                }
               
            }finally {
            	
              // dst.closeConnection();
            }
        }catch (ClassNotFoundException e){
            Dialog alert = new Alert(Alert.AlertType.ERROR, "Incorrect driver for selected database please check url and database");
            logger.info("Incorrect driver for selected database please check url....");
            alert.setTitle("SOURCE");
            alert.setHeaderText("SOURCE");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                alert.close();
            }
        }catch (SQLException e){
            Dialog alert = new Alert(Alert.AlertType.ERROR, "Source Configuration Error");
            logger.info("Error in provided Destination Source configuration please refer application guide for input standard format....");
            alert.setTitle("SOURCE");
            alert.setHeaderText("SOURCE");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                alert.close();
            }
        }
       finally{
            //   src.closeConnection();
       }
        migration.setDisable(true);
        validation.setDisable(false);
       }
       }
        
  /**
   * 
   *  this button removed from migration home page
        @FXML
    private void About(ActionEvent event){
        Dialog alert = new Alert(Alert.AlertType.INFORMATION, "Honeywell Technology Solutions Inc.");
        alert.setHeaderText("Author");
        alert.setTitle("Author");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            alert.close();
        }
    }
 
 **/
 
    @FXML
    public void HomePage(ActionEvent event) throws IOException{
       
    	String s = (System.getProperty("user.dir")+File.separator+"fxml"+File.separator+"Home.fxml").replace("\\", "/");
    	Parent root2 = FXMLLoader.load(new URL("file:///"+s));
  	   Scene  scene =new Scene(root2);
  	   Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
  	   window.setScene(scene);
  	   window.show();
       logger.info("Data is reset to default values....");
    }
    //Post-Migration validation implementation
    
    @FXML
    public void Translations(ActionEvent event) throws IOException{
    	Indicator.setVisible(true);
    	Indicator.setLayoutX(960.0);
    	Indicator.setLayoutY(320.0);
    	 new Thread(() -> {
        		tableView.getItems().clear();
    	    	logger.info("Validation of source and destination database begins....");
    	        String srcDriver = driver.getValue();
    	        String srcUrl = url.getText();
    	        String srcUser = userid.getText();
    	        String srcDb = dbname.getText();
    	        String srcPass = password.getText();
    	        String dstDriver = driver1.getValue();
    	        String dstUrl = url1.getText();
    	        String dstUser = userid1.getText();
    	        String dstDb = dbname1.getText();
    	        String dstPass = password1.getText();
    	        DataBaseConnection tsrc = new DataBaseConnection();
    	        DataBaseConnection tdst = new DataBaseConnection();
    	        
    	        String scemasrc = srcscema.getText();
    	        String scemadst = dstscema.getText();

    	        Connection tsrccon,tdstcon;

    	        
    	        tsrc.setDriver(srcDriver);
    	        tsrc.setName(srcDb);
    	        tsrc.setPassword(srcPass);
    	        tsrc.setUrl(srcUrl);
    	        tsrc.setUser(srcUser);
    	        try{
    	        tsrccon = tsrc.initConnection();
    	        
    	        tdst.setDriver(dstDriver);
    	        tdst.setName(dstDb);
    	        tdst.setPassword(dstPass);
    	        tdst.setUrl(dstUrl);
    	        tdst.setUser(dstUser);
    	        tdstcon = tdst.initConnection();
    	        
    	        logger.info("Database configurations provided for validation.... ");
    	        logger.info("srcDb: "+srcDb+" SrcUrl: "+srcUrl+" SrcUserid: "+srcUser+"srcscema: "+srcscema+"....");
    	        logger.info("dstDb: "+dstDb+" dstUrl: "+dstUrl+" dstUserid: "+dstUser+" dstscema: "+dstscema+"....");
    	        
    	        
    	        logger1.info("Database configurations provided for validation.... ");
    	        logger1.info("srcDb: "+srcDb+" SrcUrl: "+srcUrl+" SrcUserid: "+srcUser+"srcscema: "+srcscema+"....");
    	        logger1.info("dstDb: "+dstDb+" dstUrl: "+dstUrl+" dstUserid: "+dstUser+"dstscema: "+dstscema+"....");
    	        
    	        
    	        CoreMigration migrator = new CoreMigration(tsrccon,srcDb,tdstcon,dstDb,dstDriver,scemasrc,scemadst);         
    	    	int countalltablesfromsrc = migrator.countalltablesfromsrc();
    	    	String abc = Integer.toString(countalltablesfromsrc);
    	    	SrcTables.setText(abc);
    	    	logger.info("tables in src...."+countalltablesfromsrc+"...." );
    	    	logger1.info("tables in src...."+countalltablesfromsrc+"...." );
    	        
    	    	int countalltablesfromdst = migrator.countalltablesfromdst();
    	    	String bcd = Integer.toString(countalltablesfromdst);
    	    	DstTables.setText(bcd);
    	    	logger.info("tables in dst...."+countalltablesfromdst+"...." );
    	    	logger1.info("tables in dst...."+countalltablesfromdst+"...." );
        	        
    	    	List<String> records1=migrator.CalculateRows();
    	    	logger.info("Comparing data between source and destination database...." );
    	        ArrayList<Records> observabledata;
    	    	
    	    	observabledata = new ArrayList<Records>();
    	    	 int i =0;
    	    	 int j=0;
    	    	 while(i<records1.size()){ 
    	    	
    	    	observabledata.add(new Records(Integer.toString(j+1),records1.get(i), records1.get(i+1), records1.get(i+2)));
    	    	i=i+3;
    	    	j=j+1;
    	    	 }
    	    
    	    	 
    	    	//table view logic started here
    	    	final ObservableList<Records> data = FXCollections.observableArrayList(observabledata);
    	    	tableView.setItems(data);
    	        name.setCellValueFactory(new PropertyValueFactory<Records,String>("name"));
    	        srcrows.setCellValueFactory(new PropertyValueFactory<Records,String>("srcrows"));
    	        dstrows.setCellValueFactory(new PropertyValueFactory<Records,String>("dstrows"));
       	       srno.setCellValueFactory(new PropertyValueFactory<Records,String>("srno"));
       	    
/**
 * Below mentioned block of code compare the src and dst rows and if we have a mismatch between count of rows from src and 
   dst then it will highlight that row...
 *
 */
 
       	       
       	    tableView.setRowFactory(tv -> new TableRow<Records>() {
       	     @Override
       	     protected void updateItem(Records item, boolean empty) {
       	         super.updateItem(item, empty);
       	      
       	         if (item!=null && (item.getSrcrows().equalsIgnoreCase(item.getDstrows())))
       	         {
       	        	 this.setStyle("");
       	         } 
       	         else
       	         {
       	        	 this.setStyle("-fx-background-color:#F8C471");
       	        	
       	         }
       	     }
       	 });

       		//table view Logic ends here
    	       
       	       }
    	catch (Exception e)
    	        {
    		System.out.println("Unable to access migration class");
    		logger.warning("Unable to validate data between src and destination database check configuration provided for src and dst database...." );
    		logger1.warning("Unable to validate data between src and destination database check configuration provided for src and dst database...." );
    	      e.printStackTrace();
    	        }
    	        
    	        Indicator.setVisible(false);
		
		 }).start();
	  	
    	
    	      }
     //Validation part ends here
 
    /**
     * 
     * merged this implementation with migration implementation
    @FXML
    public void Prepare(ActionEvent event) throws IOException{
    	String srcDriver = driver.getValue();
        String srcUrl = url.getText();
        String srcUser = userid.getText();
        String srcDb = dbname.getText();
        String srcPass = password.getText();
        String dstDriver = driver1.getValue();
        String dstUrl = url1.getText();
        String dstUser = userid1.getText();
        String dstDb = dbname1.getText();
        String dstPass = password1.getText();
        DataBase tsrc = new DataBase();
        DataBase tdst = new DataBase();
        
        String scemasrc = srcscema.getText();
        String scemadst = dstscema.getText();

        Connection tsrccon,tdstcon;

        
        tsrc.setDriver(srcDriver);
        tsrc.setName(srcDb);
        tsrc.setPassword(srcPass);
        tsrc.setUrl(srcUrl);
        tsrc.setUser(srcUser);
        try{
        tsrccon = tsrc.initConnection();
        
        tdst.setDriver(dstDriver);
        tdst.setName(dstDb);
        tdst.setPassword(dstPass);
        tdst.setUrl(dstUrl);
        tdst.setUser(dstUser);
        tdstcon = tdst.initConnection();
        
        Migrator migrator = new Migrator(tsrccon,srcDb,tdstcon,dstDb,dstDriver,scemasrc,scemadst);  
      migrator.prepare();
        
        
        }
        catch (Exception e)
        {
        	System.out.println("unable to generate logs");
        }
        
      //  Prepare.setDisable(true);
        
        Dialog alert = new Alert(Alert.AlertType.CONFIRMATION, "Tables deleted from destination");
        alert.setTitle("Dst_Scema_Preparation");
        alert.setHeaderText("Dst_Scema_Preparation");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            alert.close();
        }
    	
    }
    
    **/
    
    //PRE-Migration validation implementation
    
        
    
    @FXML
    public void ScemaStatus(ActionEvent event) throws IOException{
    	tableView.getItems().clear();
      	logger.info("Validating current status of source and destination database scemas provided ...." );
        String srcDriver = driver.getValue();
        String srcUrl = url.getText();
        String srcUser = userid.getText();
        String srcDb = dbname.getText();
        String srcPass = password.getText();
        String dstDriver = driver1.getValue();
        String dstUrl = url1.getText();
        String dstUser = userid1.getText();
        String dstDb = dbname1.getText();
        String dstPass = password1.getText();
        
        String scemasrc = srcscema.getText();
        String scemadst = dstscema.getText();
        
        DataBaseConnection vsrc = new DataBaseConnection();
        DataBaseConnection vdst = new DataBaseConnection();

        Connection Vsrccon,Vdstcon;
        
        vsrc.setDriver(srcDriver);
        vsrc.setName(srcDb);
        vsrc.setPassword(srcPass);
        vsrc.setUrl(srcUrl);
        vsrc.setUser(srcUser);
        try{
        Vsrccon = vsrc.initConnection();
        
        vdst.setDriver(dstDriver);
        vdst.setName(dstDb);
        vdst.setPassword(dstPass);
        vdst.setUrl(dstUrl);
        vdst.setUser(dstUser);
        Vdstcon = vdst.initConnection();
        
        
        logger.info("Database configurations provided for validation....");
        logger.info("srcDb :"+srcDb+" SrcUrl:"+srcUrl+" SrcUserid:"+srcUser+" srcPassword:"+srcPass+" srcscema"+srcscema+"....");
        logger.info("dstDb :"+dstDb+" dstUrl:"+dstUrl+" dstUserid:"+dstUser+" dstPassword:"+dstPass+" dstscema"+dstscema+"....");
        
        CoreMigration migrator = new CoreMigration(Vsrccon,srcDb,Vdstcon,dstDb,dstDriver,scemasrc,scemadst);         
    	int countalltablesfromsrc = migrator.countalltablesfromsrc();
    	String abc1 = Integer.toString(countalltablesfromsrc);
    	SrcTables1.setText(abc1);
    	logger.info("tables in src...."+countalltablesfromsrc+"....");
        
    	
    	
    	int countalltablesfromdst = migrator.countalltablesfromdst();
    	String bcd1 = Integer.toString(countalltablesfromdst);
    	DstTables1.setText(bcd1);
    	logger.info("tables in dst...."+countalltablesfromdst +"....");
    	
    	
    	if (countalltablesfromdst>0)
    	{
    		  Prepare.setDisable(false);
    	}
    	else
    	{
    		 // Prepare.setDisable(true);
    	    	
    	}
    	
        }
        catch(Exception e)
        {
        	 Dialog alert = new Alert(Alert.AlertType.ERROR, "Error in provided Destination/Source configuration please refer application guide for input standard format....");
             logger.info("Error in provided Destination Source configuration please refer application guide for input standard format....");
             alert.setTitle("WRONG DATABASE CONFIGURATIONS");
             alert.setHeaderText("WRONG DATABASE CONFIGURATIONS");
             Optional<ButtonType> result = alert.showAndWait();
             if (result.isPresent() && result.get() == ButtonType.OK) {
                 alert.close();
             }
        	
        	System.out.println("wrong configuation given to calculate data before migration");
        	logger.info("wrong configuation for Validating current status of source and destination database scemas....");
        }
        
        migration.setDisable(false);
        }
       
}
