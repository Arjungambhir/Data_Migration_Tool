package com.migration.controllers;
import static com.migration.controllers.DataTypeConverter.toOracleType;
import static com.migration.controllers.DataTypeConverter.toServerType;
import static com.migration.controllers.DataTypeConverter.tohsqldb;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import oracle.sql.CLOB;
import oracle.sql.TIMESTAMP;
import javax.sql.rowset.serial.*;

import javafx.scene.control.ProgressIndicator;


/** 
 * 
 * @author H264637 (Arjun Gambhir)
 *
 */
public class CoreMigration {
        private Connection src;
        private Connection dst;
        private String srcName;
        private String dstName;
        private String dstDriver;
        private List<String> deleteAllKeys= new ArrayList<>();
        private Statement srcStmt;
        private Statement dstStmt;
        private ResultSet srcRs;
        private ResultSet dstRs;
        private DatabaseMetaData databaseMetaData;        
        private DatabaseMetaData databaseMetaData1;
        private String srcscema;
        private String dstscema;

        Logger logger = Logger.getLogger("MigrationDetailedLog");  
        FileHandler fh;  
        Logger logger1 = Logger.getLogger("MigrationLogSummary");  
        FileHandler fh1;  
       
        public CoreMigration(Connection srcdb,String srcName,Connection dstdb,String dstName,String destdriver,String srcscema,String dstscema) throws SQLException
        {
        	this.src=srcdb;
            this.dst=dstdb;
            this.srcName=srcName;
            this.dstName=dstName;
            this.dstDriver=destdriver;
            this.srcStmt=src.createStatement();
            this.dstStmt=dst.createStatement();
            this.databaseMetaData = srcdb.getMetaData();
            this.databaseMetaData1 = dstdb.getMetaData();
            this.srcscema=srcscema;
            this.dstscema=dstscema;
        }


        public void start() throws SQLException
        {
        	
        	logger.info("Migration begins successfully....");
        	logger.info("Getting all tables from src :"+srcName+" From scema :"+srcscema+"....");
        	List<Table> tables  = getAllTablesFromsource();
       //   deleteAllKeysFromDst();
      //    deleteAllTables(tables);
        	logger.info("Scema setup in destination begins....");
        	logger1.info("Scema setup in destination begins....");
        	createAllTables(tables);
        	logger.info("Adding Constraints to destination database started......");
        	addPrimaryKeys(tables);
        	logger.info("Migrating data from src :"+srcName+" tables "+"to destination : "+dstName+"tables....");
        	insertIntoDestination(tables);
            addForeignKeys(tables);
        }

        public List<Table> getAllTablesFromsource() throws SQLException{
        	 List<Table> tables  = new ArrayList<>();
        	 List<String> tablesName =getSrcTables();
                     for(String table:tablesName) {
                Table tab = new Table(table,getSrcColumns(table),primaryKeys(table),foreignKeys(table));
                tables.add(tab);
            }
            return tables;
        }
        
        private void insertIntoDestination(List<Table> tables) throws SQLException {
        
        	
        	if (dstDriver=="SQL MS Server"){
        	for(Table table : tables) {
        		String request = "select * from " + srcscema+"."+table.name;
                String request1 ="set identity_insert " +dstName+"."+dstscema+"."+table.name+" OFF"+";"; 
                String request2 ="set identity_insert " +dstName+"."+dstscema+"."+table.name+" ON"+";"; 
                String value = "";
                String value1 = "";
            
                try {
                	try {
                	    dstStmt.execute(request2);
                	    System.out.println(table.name+"  On done");
                	    logger.info("identity_insert for "+table.name+" set as ON");
                    }
                    catch(Exception e)
                    {
                    	System.out.println("no identity column");
                    	logger.warning("Unable to set identity_insert for "+table.name+" as ON because this table not contain any identity column");
                    	
                    }
                	    srcRs = srcStmt.executeQuery(request);
                        while (srcRs.next()) {
                        String sql3 = "INSERT INTO "+dstscema+"." + table.name +"(";
                        int count=0;
                        for (TableColumn col : table.columns) {
                    		if (count == table.columns.size() - 1) 
                    			{
                    			sql3 +=col.name + " " ;
                    			 break;                   			
                    			} 
                    		else{
                    		count++;
                            sql3 +=" "+col.name + " "  + ",";	
                    	}}
                        
                        sql3 += ")  VALUES (";
                        for (int i = 1; i <= table.columns.size(); i++) {
                      
                        	if(dstDriver.equalsIgnoreCase("SQL MS Server") && (table.name).equalsIgnoreCase("VOC_SYSTEM_TRANSLATIONS") && (Integer.toString(i)).equalsIgnoreCase("5") )
                        	{
                        		 value = srcRs.getString(i);
                                 if(value==null){value="NULL";}
                                 if(value!=null && value.equals("null")){value="NULL";}
                                 if(value!=null && !value.equals("NULL")){value="N'"+clean(value)+"'";}
                        	}
                          	else if(dstDriver.equalsIgnoreCase("SQL MS Server") && (table.name).equalsIgnoreCase("VOC_DATA_TRANSLATIONS") && (Integer.toString(i)).equalsIgnoreCase("5") )
                        	{
                        		 value = srcRs.getString(i);
                                 if(value==null){value="NULL";}
                                 if(value!=null && value.equals("null")){value="NULL";}
                                 if(value!=null && !value.equals("NULL")){value="N'"+clean(value)+"'";}
                        	}
                      
                        	else
                        	{
                              value = srcRs.getString(i);
                              if(value==null){value="NULL";}
                              if(value!=null && value.equals("null")){value="NULL";}
                              if(value!=null && !value.equals("NULL")){value="'"+clean(value)+"'";}
                        	}
                           if (dstDriver=="Oracle"){
                        	   if (i < table.columns.size())
                                   sql3 += value + ",";
                               else
                                   sql3 += value + ")";
                           }
                           else{
                              
                              if (i < table.columns.size())
                                sql3 += value + ",";
                            else
                                sql3 += value + ");";
                           }
                        }
                        dstStmt.execute(sql3);
                        logger.info(sql3+"....");
                    }
                    try {
                	    dstStmt.execute(request1);
                	    System.out.println(table.name+"  OFF done");
                    }
                    catch(Exception e)
                    {
                    	logger.warning("Unable to set identity_insert for "+table.name+" as OFF because this table not contain any identity column");
                    }
  
                } catch (SQLException e) {
                	
                    logger.warning("Unable to insert all records.in table."+table.name+"....");
                    logger1.warning("Unable to insert all records.in table."+table.name+"....Please check ApplicationDetailedLog.log file for details");
                  
                }
            }
        	}
        	
        	if (dstDriver=="Oracle"){
        		
        		  for(Table table : tables) {
                      String request = "select * from " + srcscema+"."+table.name;
                      String value = "";
                     
                      
                      try {
                          srcRs = srcStmt.executeQuery(request);
                          while (srcRs.next()) {
                              String sql3 = "INSERT INTO "+dstscema+"." + table.name + " VALUES (";
                              for (int i = 1; i <= table.columns.size(); i++) {
                                    value = srcRs.getString(i);
                                    if(value==null){value="NULL";}
                                    if(value!=null && value.equals("null")){value="NULL";}
                                    if(value!=null && !value.equals("NULL")){value="'"+clean(value)+"'";}
                                    
                                    
                                    if(value.contains("-")  && value.contains(":") && value.contains(".") && value.contains(" ") )
                                    {	
                                       if ((value.charAt(5)=='-' && (value.charAt(8)=='-' ) && (value.charAt(11)==' ' ) && (value.charAt(14)==':' ) && (value.charAt(17)==':' )&& (value.charAt(20)=='.' ) ))
                                      {
                                      value="TO_TIMESTAMP("+value+","+"'YYYY-MM-DD HH24:MI:SS.FF')";	
                                      }
                                    }
                               
                                       
                                   if(value.length()>4000) { 
                                	    int Cloblen=value.length();
                                	    int Clobvar=0;
                                        String clobvalue = "";
                             	   while(value.length()>=Clobvar)
                                 	{
                                 	if (Cloblen>4000)
                                 	{
                                 	if (Clobvar==0) {
                                 	clobvalue +="to_clob("+value.substring(Clobvar,Clobvar+4000)+"') ||";
                                 	}
                                 	else {
                                 		clobvalue +="to_clob('"+value.substring(Clobvar,Clobvar+4000)+"') ||";
                                     		
                                 	}
                                 	Clobvar=Clobvar+4000;
                                 	Cloblen= value.length()-Clobvar;
                                 	}
                                 	else{
                                 	
                                 	clobvalue +="to_clob('"+value.substring(Clobvar,value.length())+")";
                                 	Clobvar=value.length()+1;
                                 	}
                                 	}
                             	  value= clobvalue;
                                   }
                                   
                                   if (i < table.columns.size())
                                         sql3 += value + ",";
                                     else
                                         sql3 += value + ")";
                               }
                              dstStmt.execute(sql3);
                              logger.info(sql3+"....");
                          }
                      } catch (SQLException e) {
                    	  logger.info("Unable to insert all records.in table."+table.name+"....");
                          logger1.info("Unable to insert all records.in table."+table.name+"....Please check Applicationlog.log file for details");
                      }
                  }     	
        	}	
        	}
        	
        public String clean( String sourceString )
        {
            StringBuffer target = new StringBuffer();
            int length = sourceString.length();
            char c;
            for( int i=0; i<length; i++ )
            {
                c = sourceString.charAt( i );
                
                target.append( c );
                if ( c == '\'' )
                {
                    target.append( c ); // add another single quote for escape char
                }
            }
            return target.toString();
        }
        
       
    /**
        public void deleteAllKeysFromDst() throws SQLException
               {
            for(String req : deleteAllKeys)
            {
           try
           {
            	dstStmt.executeUpdate(req);
            	System.out.println(""+req+"......Completed");
          	   
            }
            catch(Exception e) {
            	System.out.println("The "+req+" Unable to execute because no keys with given name is present in destination");
         	   
            }
            
            }
        }

       **/
       
       public void addPrimaryKeys(List<Table> tables) throws SQLException
        {
            for(Table table:tables)
            {
                for(String primaires:table.primary){
                	
                	try {
                    dstStmt.executeUpdate("ALTER TABLE "+dstscema+"."+table.name+" "+primaires);
                	logger.info("Executed.. ALTER TABLE "+table.name+" "+primaires+"....");
                	}
                	catch (Exception e)
                	{
                		logger.warning("Unable to execute"+"ALTER TABLE "+table.name+" "+primaires+"....");
                		logger1.warning("Unable to Add following constraint as primary key in "+table.name+" "+primaires+"....");
                		e.printStackTrace();
                	}
                	}
            }
        }

        void addForeignKeys(List<Table> tables) throws SQLException
        {
            for(Table table:tables)
            {
                for(String etrange:table.foreign){
                	
                	try {
                    dstStmt.executeUpdate("ALTER TABLE "+dstscema+"."+table.name+" "+etrange);

                    logger.info("ALTER TABLE "+table.name+" "+etrange+"....");
                	}
                catch (Exception e)
                	{
                	logger.warning("Unable to execute"+"ALTER TABLE "+table.name+" "+etrange+"....");
                	logger1.warning("Unable to Add following constraint as foreign key in "+table.name+" "+etrange+"....");
                	}
                	}
                }
            }
        
        public List<String> primaryKeys(String table) throws SQLException
        {
            List<String> primaires=new ArrayList<>();
            srcRs = databaseMetaData.getPrimaryKeys(src.getCatalog(), srcscema, table);
            while (srcRs.next()) {
                String pk_nom = srcRs.getString("COLUMN_NAME");
                primaires.add(pk_nom);
            }
            int count =0;
            String keys="";
           
            for(String key:primaires)
            {
                if(count==primaires.size()-1 ){
                    keys+=key;
                }else
                {
                    keys+=key+",";
                }

                count++;
            }
            
            Random rn = new Random();
            int min=1;
            int max=1000000;
            int range = max - min + 1;
            int randomNum =  rn.nextInt(range) + min;
            
            
            if(!primaires.isEmpty()) {
            	
            	String req;
          
            	if (dstDriver=="Oracle")
                {
            		if((primaires.get(0)).length()>20)
                	{
                	
            	req = "ADD CONSTRAINT PK_"+Integer.toString(randomNum)+"_"+" PRIMARY KEY ("+keys+")";
                	}
            		else
                	{
            			req = "ADD CONSTRAINT PK_"+Integer.toString(randomNum)+"_"+primaires.get(0)+" PRIMARY KEY ("+keys+")";

                	}
            	
                }
            
            else
            {
            	req = "ADD CONSTRAINT PK_"+table+"_"+primaires.get(0)+" PRIMARY KEY ("+keys+");";
                        
            }
            	deleteAllKeys.add("ALTER TABLE "+table+" DROP CONSTRAINT PK_"+table+"_"+primaires.get(0));
                
            primaires.clear();
            primaires.add(req);
            }
            return primaires;
        }

        private List<String> foreignKeys(String table) throws SQLException
        {
            List<String> etrange=new ArrayList<>();
            srcRs = databaseMetaData.getImportedKeys(src.getCatalog(),srcscema, table);
            while (srcRs.next()) {
                String pkTable = srcRs.getString("PKTABLE_NAME");
                String pkColName = srcRs.getString("PKCOLUMN_NAME");
                String fkTable = srcRs.getString("FKTABLE_NAME");
                String fkColName = srcRs.getString("FKCOLUMN_NAME");
                String req;
                Random rn = new Random();
                int min=1;
                int max=1000000;
                int range = max - min + 1;
                int randomNum =  rn.nextInt(range) + min;
    
                if (dstDriver=="Oracle")
                {
                	if(fkColName.length()>20)
                	{
                		  req = "ADD CONSTRAINT FK_"+Integer.toString(randomNum)+" FOREIGN KEY ("+fkColName+") REFERENCES "+pkTable+" ("+pkColName+")";
                   }
                	else
                	{
                 req = "ADD CONSTRAINT FK_"+Integer.toString(randomNum)+"_"+fkColName+" FOREIGN KEY ("+fkColName+") REFERENCES "+pkTable+" ("+pkColName+")";
                	}
                	}
                else
                {
                	req = "ADD CONSTRAINT FK_"+fkTable+"_"+fkColName+" FOREIGN KEY ("+fkColName+") REFERENCES "+pkTable+" ("+pkColName+");";
                            
                }
            	deleteAllKeys.add("ALTER TABLE "+fkTable+" DROP CONSTRAINT IF FK_"+fkTable+"_"+fkColName);
 
                etrange.add(req);
            }
            return etrange;
        }

        private void createAllTables(List<Table> liste)throws  SQLException
        {
        	
        	int i=0;
        	int j=0;
        	
            for(Table table : liste) {
                dstStmt = dst.createStatement();
                StringBuilder sql = new StringBuilder("CREATE TABLE "+dstscema+".");
                sql.append(table.name + " (");
                int count = 0;
                int d=0;  //number of digits on right side of decimal specifically used for numeric and decimal data types
             
              //table creation logic for sql server database
                
              if (dstDriver=="SQL MS Server"){
                for (TableColumn col : table.columns) {
                	
                if ( col.type=="varchar" || col.type=="nvarchar" || col.type=="varchar2" || col.type=="nvarchar2" || col.type=="varbinary" ||  col.type=="double"   )
                {
                	if (col.type=="nvarchar2" && Integer.valueOf(col.size)>2000)
                	{
                		col.type="nclob";
                		if (count == table.columns.size() - 1) {
                            sql.append(col.name + " " + col.type +" " +col.auto_inc+" "+ col.nullable);
                            break;
                        }
                        count++;
                        sql.append(col.name + " " + col.type  +" "+ col.nullable + ",");	
                	}
                	
                	else
                	{
                		if (count == table.columns.size() - 1) {
                            sql.append(col.name + " " + col.type + "(" + col.size + ") " +" " +col.auto_inc+ " "+col.nullable);
                            break;
                        }
                        count++;
                        sql.append(col.name + " " + col.type + "(" + col.size + ") " +" " +col.auto_inc+ " "+ col.nullable + ",");	
                		
                	}
                }
                
                else if (col.type=="decimal" || col.type=="numeric" || col.type=="number" )
                {
                    if (count == table.columns.size() - 1) {
                        sql.append(col.name + " " + col.type +" ("+  col.size+","+d+") " +" " +col.auto_inc+ " "+ col.nullable);
                        break;
                    }
                    count++;
                    sql.append(col.name + " " + col.type +" ("+  col.size+","+d+") " +" " +col.auto_inc+ " "+  col.nullable + ",");
                }
                else
                {
                	
                    if (count == table.columns.size() - 1) {
                       
                    	sql.append(col.name + " " + col.type +  " " +" " +col.auto_inc+ " "+ col.nullable);
                        break;
                    }
                    count++;
                    sql.append(col.name + " " + col.type +  " " +" " +col.auto_inc+ " "+ col.nullable+ ",");
                    }
            }
                }
                else{
                    for (TableColumn col : table.columns) {
                    	
                        if ( col.type=="varchar" || col.type=="nvarchar" || col.type=="varchar2" || col.type=="nvarchar2" || col.type=="varbinary" ||  col.type=="double"   )
                        {
                        	if (col.type=="nvarchar2" && Integer.valueOf(col.size)>2000)
                        	{
                        		col.type="nclob";
                        		if (count == table.columns.size() - 1) {
                                    sql.append(col.name + " " + col.type +" "+ col.nullable);
                                    break;
                                }
                                count++;
                                sql.append(col.name + " " + col.type  +" "+ col.nullable + ",");	
                        	}
                        	
                        	else
                        	{
                        		if (count == table.columns.size() - 1) {
                                    sql.append(col.name + " " + col.type + "(" + col.size + ") " + " "+col.nullable);
                                    break;
                                }
                                count++;
                                sql.append(col.name + " " + col.type + "(" + col.size + ") " + " "+ col.nullable + ",");	
                        		
                        	}
                        }
                        
                        else if (col.type=="decimal" || col.type=="numeric" || col.type=="number" )
                        {
                            if (count == table.columns.size() - 1) {
                                sql.append(col.name + " " + col.type +" ("+  col.size+","+d+") " +" "+ col.nullable);
                                break;
                            }
                            count++;
                            sql.append(col.name + " " + col.type +" ("+  col.size+","+d+") " + " "+  col.nullable + ",");
                        }
                        else
                        {
                        	
                            if (count == table.columns.size() - 1) {
                               
                            	sql.append(col.name + " " + col.type +  " " +" " + col.nullable);
                                break;
                            }
                            count++;
                            sql.append(col.name + " " + col.type +  " " + " "+ col.nullable+ ",");
                            }
                        
                        
                    }
                        }
                	

              //table creation logic for Oracle database
              
                if (dstDriver=="Oracle"){
                
                sql.append(")");
            	
                }
                else {
                    sql.append(");");
                        	
                }
                String req = sql.toString();
                
        try {   
                dstStmt.executeUpdate(req);
                i=i+1;
                logger.info("Executed..."+req+"....");
                logger1.info("Table  "+table.name+" Created Successfully");
            }
        
        
catch(Exception e)
        {
	 logger.warning("Unable to execute "+req+" Statement....");
	 logger1.warning("Unable to create "+table.name+" in destination...");
	    }
        }
            
            if(dstDriver=="Oracle"){
            try{
            	dstStmt.execute("drop sequence voc_primary_key_sequence");
            }
            catch(Exception e){
            	logger.info("No sequence available to delete");
            }
            try{
            dstStmt.execute("CREATE SEQUENCE VOC_PRIMARY_KEY_SEQUENCE  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 10001 CACHE 20 NOORDER  NOCYCLE") ;
            	
            }
            	catch(Exception e){
            		logger.info("Unable to create sequence VOC_PRIMARY_KEY_SEQUENCE  ");
            		e.printStackTrace();
                    }
            }
            
            //Here we have some columns with some default values we are using the static queries to setup them instead of using dynamic queries.
            
            if(dstDriver=="SQL MS Server"){
                try{
                	dstStmt.execute("ALTER TABLE "+dstName+"."+dstscema+".VC_CORE_AI ADD CONSTRAINT DF_VCCA_ADL DEFAULT 0 FOR autoDefaultLoad;");
                	logger.info("successfuly added default constraint for column autoDefaultLoad in table VC_CORE_AI ");
                	dstStmt.execute("ALTER TABLE "+dstName+"."+dstscema+".VC_DEVICE_TERMINAL ADD CONSTRAINT DF_VCDT_MC DEFAULT 0 FOR mixedConfig;");
                	logger.info("successfuly added default constraint for column mixedConfig in table VC_CORE_AI ");
                	dstStmt.execute("ALTER TABLE  "+dstName+"."+dstscema+".VC_DEVICE_TERMINAL ADD CONSTRAINT DF_VCDT_OEP DEFAULT 0 FOR onExternalPower;");
                	logger.info("successfuly added default constraint for column onExternalPower in table VC_CORE_AI ");
                	dstStmt.execute("ALTER TABLE  "+dstName+"."+dstscema+".VC_DEVICE_TERMINAL ADD CONSTRAINT DF_VCDT_PAV DEFAULT 0 FOR powerAdapterVersion;");
                	logger.info("successfuly added default constraint for column powerAdapterVersion in table VC_CORE_AI ");
                	dstStmt.execute("ALTER TABLE  "+dstName+"."+dstscema+".VC_DEVICE_TERMINAL ADD CONSTRAINT DF_VCDT_MRU DEFAULT 0 FOR multipleResidentTtsEnabled;");
                	logger.info("successfuly added default constraint for column multipleResidentTtsEnabled in table VC_CORE_AI ");
                	dstStmt.execute("ALTER TABLE "+dstName+"."+dstscema+".VC_DEVICE_TERMINALPROFILE ADD CONSTRAINT DF_VCDTP_DP DEFAULT 0 FOR defaultProfile;");
                	logger.info("successfuly added default constraint for column defaultProfile in table VC_DEVICE_TERMINALPROFILE ");
                	dstStmt.execute("ALTER TABLE "+dstName+"."+dstscema+".voc_dashboards ADD CONSTRAINT DF_VOCD_TF DEFAULT 0 FOR timeFilter;");
                	logger.info("successfuly added default constraint for column timeFilter in table voc_dashboards ");
                	
                }
                catch (Exception e){
                	logger.warning("Unable to create default coluns for tables VC_CORE_AI ,VC_DEVICE_TERMINAL ,VC_DEVICE_TERMINALPROFILE,voc_dashboards");
                e.printStackTrace();
                
                }
            }
            
            else {
            	 try{
            		 dstStmt.execute("alter table "+dstscema+".VC_CORE_AI modify (autoDefaultLoad default 0)");
            		 logger.info("successfuly added default constraint for column autoDefaultLoad in table VC_CORE_AI ");
            		 dstStmt.execute("alter table "+dstscema+".VC_DEVICE_TERMINAL modify (mixedConfig default 0)");
                 	logger.info("successfuly added default constraint for column mixedConfig in table VC_CORE_AI ");
                 	dstStmt.execute("alter table "+dstscema+".VC_DEVICE_TERMINAL modify (onExternalPower default 0)");
                 	logger.info("successfuly added default constraint for column onExternalPower in table VC_CORE_AI ");
                 	dstStmt.execute("alter table "+dstscema+".VC_DEVICE_TERMINAL modify (powerAdapterVersion default 0)");
                 	logger.info("successfuly added default constraint for column powerAdapterVersion in table VC_CORE_AI ");
                 	dstStmt.execute("alter table "+dstscema+".VC_DEVICE_TERMINAL modify (multipleResidentTtsEnabled default 0)");
                 	logger.info("successfuly added default constraint for column multipleResidentTtsEnabled in table VC_CORE_AI ");
                 	dstStmt.execute("alter table "+dstscema+".VC_DEVICE_TERMINALPROFILE modify (defaultProfile default 0)");
                 	logger.info("successfuly added default constraint for column defaultProfile in table VC_DEVICE_TERMINALPROFILE ");
                 	dstStmt.execute("alter table "+dstscema+".voc_dashboards modify (timeFilter default 0)");
                 	logger.info("successfuly added default constraint for column timeFilter in table voc_dashboards ");
                 	
                 }
                 catch (Exception e){
                 	logger.warning("Unable to create default coluns for tables VC_CORE_AI ,VC_DEVICE_TERMINAL ,VC_DEVICE_TERMINALPROFILE,voc_dashboards");
                 e.printStackTrace();
                 
                 }
             
          //Static Queries for columns with some default values ends here.
                 
            }
            
            logger1.info(i+" Tables Created successfully in destination");
            logger1.info(j+" Number of tables failed to create in destination refer mylog file for more details....");
           }
        
        /**
        private void deleteAllTables(List<Table> liste)throws  SQLException
        {        
                 for(Table table : liste) {
                dstStmt = dst.createStatement();
                StringBuilder sql = new StringBuilder("DROP TABLE ");
                sql.append(table.name );
                int count = 0;
               String req = sql.toString();
                System.out.println(req);
               
             try {   
                dstStmt.executeUpdate(req);
                System.out.println(" "+req+"....Completed");
            }
            catch(Exception e) {
            	System.out.println("The "+req+" Unable to execute because no keys with given name is present in destination");
            }
            }

        }
        
        
        **/
        
        private String formatType(String type,String size)
        {
            switch (dstDriver)
            {
                case "Oracle":
                    return toOracleType(type,size);
                case "SQL MS Server":
                    return toServerType(type,size);
                case "HSQLDB":
                    return tohsqldb(type,size);
            }
            return "VARCHAR";
        }

        private List<String> getSrcTables() throws SQLException{
            srcRs = databaseMetaData.getTables(src.getCatalog(),srcscema,"%",null);
            List<String> tables= new ArrayList<>();
            while(srcRs.next())
            {
                tables.add(srcRs.getString("TABLE_NAME"));
            }
            return tables;
        }

        private List<TableColumn> getSrcColumns(String tableName) throws SQLException
        {
            List<TableColumn> columns= new ArrayList<>();
            srcRs = databaseMetaData.getColumns(src.getCatalog(),srcscema, tableName, "%");
            while(srcRs.next()){
            	try {
                String name,type,nullable,autoinc,size,data_type_name,precision;
               
                name = srcRs.getString("COLUMN_NAME");
                type = JDBCType.valueOf(srcRs.getInt("DATA_TYPE")).toString();
                size = srcRs.getString("COLUMN_SIZE");
                nullable = srcRs.getString("NULLABLE");
                autoinc = srcRs.getString("IS_AUTOINCREMENT");
                type = formatType(type.toLowerCase(),size);  
                
               if (dstDriver=="SQL MS Server" && autoinc.equalsIgnoreCase("yes")){autoinc="identity";}
                else {autoinc="";}
                
               if ((type.equalsIgnoreCase("nvarchar") ||type.equalsIgnoreCase("nvarchar2")) && Integer.valueOf(size) >4000 )
                {
                	if (dstDriver=="SQL MS Server")
                	{
                	type="ntext";
                	}
                	else{
                		type="nclob";
                		
                	}
                }
                
                if (type.equalsIgnoreCase("varbinary") && Integer.valueOf(size) >8000 )
                {
                	if (dstDriver=="SQL MS Server"){
                
                	type="varchar(max)";
                }
                	
                }
                if(type.contains("VARCHAR")){size="("+size+")";}
                	else {size=size;}
                if(type.equalsIgnoreCase("number") && dstDriver=="Oracle")
                {
                	// For a number size is 22 by default and here we are passing the precision value by storing it into size variable 
                	//here size is storing the precision value for the number data type
                	int size1=Integer.parseInt(size);
                	if(size1>40){size="19";}     
                	else if (size1>25){size="10";}
                	else{size=size;}
                }
                
                
                if (type.contains("int")){size="";}
                if (type.contains("date")){size="";}
                if(nullable.equals("0")){nullable="NOT NULL";}else{nullable="";}
                TableColumn col = new TableColumn(name,type,nullable,autoinc,size);
                columns.add(col);
            	}catch(IllegalArgumentException e) {
            		logger.warning("Unable to read columns: from table " +tableName);
            		logger1.warning("Unable to read columns: from table " +tableName);
            	}
                }
            return columns;
        }

 // 	Validation related records and details are stored here starting from below method     
        public int countalltablesfromsrc() throws SQLException{
        	int countsrctables=0;
       	 List<Table> tables  = new ArrayList<>();
       	 List<String> tablesName =getSrcTables();
                    for(String table:tablesName) {
                    	countsrctables++;
                    }
                    return countsrctables;         	
        }
           
        public int countalltablesfromdst() throws SQLException{
        	int countdsttables=0;
       	 List<Table> tables  = new ArrayList<>();
       	 List<String> tablesName =getdstTables();
                    for(String table:tablesName) {
                    	countdsttables++;
                    }
                    return countdsttables;         	
        }

      public List<String> CalculateRows() throws SQLException {
	        logger.info(" Validation records for src and Dst......");
        	List<Table> tables  = getAllTablesFromsource();        	
        	List<String> rows=new ArrayList<>();
        	
            for(Table table : tables) {
                String request = "select * from " + srcscema+"."+table.name;
                String request1 = "select * from " + dstscema+"."+table.name;
                String value = "";
                int countsrc=0;
                int countdst=0;
                try {
                    srcRs = srcStmt.executeQuery(request);
                    dstRs = dstStmt.executeQuery(request1);
                while (srcRs.next()) {
                    	countsrc++;
                    }
                    while (dstRs.next()) {
                    	countdst++;
                    }
                } catch (SQLException e) {
                   logger.info("Unable to count rows in source and destination please check the database configuration provided.");
                }
                
                String countsrc1 = Integer.toString(countsrc) ;
                String countdst1 = Integer.toString(countdst) ;
                logger.info(table.name+"  "+countsrc+"  "+countdst+"......");
                rows.add(0,table.name);
                rows.add(1,countsrc1);
                rows.add(2,countdst1);
                            }
        return rows;
        }

//Prepare implementation ends here

public void prepare() throws SQLException{
	 List<Table> dsttables  = new ArrayList<>();
	 List<String> dsttablesName =getdstTables();
     String req;


     while(!getdstTables().isEmpty())
     	{
    	 int i=0;
    	 for(String dsttable:dsttablesName) {
		 
		 if (dstDriver=="Oracle")
		    {
				req = "DROP TABLE "+dstscema+"."+dsttable;
		    }

		else
		{
			req = "DROP TABLE "+dstscema+"."+dsttable+";";
		            
		}
		 
		 try{
			
		 dstStmt.executeUpdate(req);
		 logger.info("Table :"+dsttable+"  dropped successfuly");
		 }
		 catch (Exception e)
	        {
				 i=i+1;
	        }
	 }
	 }
}
private List<String> getdstTables() throws SQLException{
    dstRs = databaseMetaData1.getTables(dst.getCatalog(),dstscema,"%",null);
    List<String> tables= new ArrayList<>();
    while(dstRs.next())
    {
        tables.add(dstRs.getString("TABLE_NAME"));
    }
    return tables;
}

// Prepare implementation ends here
}
