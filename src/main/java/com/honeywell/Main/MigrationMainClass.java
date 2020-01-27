package com.honeywell.Main;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class MigrationMainClass extends Application {

    private Parent rootNode;

    public static void main(final String[] args) {
        Application.launch(args);
        
    }    
    @Override
    public void init() throws Exception {
    	String s = (System.getProperty("user.dir")+File.separator+"fxml"+File.separator+"Home.fxml").replace("\\", "/");
    	FXMLLoader fxmlLoader = new FXMLLoader(new URL("file:///"+s));
        rootNode = fxmlLoader.load();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(rootNode));
        stage.show();
    }

}