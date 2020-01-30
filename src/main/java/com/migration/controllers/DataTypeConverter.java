package com.migration.controllers;


/** 
 * This class contains standard compatible data types in respective database and relative conversion between databases
 * @author H264637 (Arjun Gambhir)
 *
 */
public class DataTypeConverter {

	public static String tohsqldb(String type,String size){
        switch(type) {
	    case "bigint":
            return "bigint";
        case "integer":
            return "int";
		case "int":
            return "int";
		case "smallint":
            return "smallint";
		case "numeric":
            return "numeric";
		case "float":
            return "float";
		case "real":
            return "real";
		case "datetime2":
            return "datetime";
		case "smalldatetime":
            return "smalldatetime";
		case "date":
            return "date";
		case "time":
            return "time";
		case "char":
            return "char";
		case "varchar":
            return "varchar";
		case "decimal":
		  return "decimal";   
		case "timestamp":
			  return "timestamp";
		case "number":
            return "decimal";
		case "nvarchar2":
            return "varchar";
		case "blob":
            return "varbinary";
		case "double":
            return "float";
		case "long":
            return "text";
		case "longvarchar":
            return "varchar";
 		 }
    return "varchar";
}
     public static String toOracleType(String type,String size){
        switch(type) {
        //In oracle we are changing HSQL DB data types to compatible oracle datatypes       
        
        case "INTEGER":
            return "number";
        case "bigint":
            return "number";   
        case "bit":
            return "number";   
       case "int":
            return "int";
		case "smallint":
            return "smallint";
		case "decimal":
            return "number";
		case "number":
            return "numeric";
		case "float":
            return "float";
		case "real":
            return "real";
		case "datetime2":
            return "date";
		case "date":
            return "date";
		case "varchar":
            return "nvarchar2";
		case "char":
            return "nchar";
		case "nvarchar":
            return "nvarchar2";
		case "timestamp":
            return "TIMESTAMP";
		case "varbinary":
            return "clob";
		case "text":
            return "long";
   	        }
        return "number";
    }

    public static String toServerType(String type,String size){
        switch(type) {
        
        case "integer":
            return "int";
		case "bigint":
            return "bigint";
		case "int":
            return "int";
		case "smallint":
            return "smallint";
		case "tinyint":
            return "tinyint";
		case "bit":
            return "bit";
		case "decimal":
            return "decimal";
		case "numeric":
            return "numeric";
		case "money":
            return "money";
		case "smallmoney":
            return "smallmoney";
		case "float":
            return "float";
		case "real":
            return "real";
		case "datetime":
            return "datetime2";
		case "smalldatetime":
            return "smalldatetime";
		case "date":
            return "date";
		case "time":
            return "time";
		case "char":
            return "char";
		case "varchar":
            return "nvarchar";
		case "varchar(max)":
            return "nvarchar(max)";
		case "text":
            return "text";
		case "nchar":
            return "nchar";
		case "nvarchar":
            return "nvarchar";
		case "nvarchar(max)":
            return "nvarchar(max)";
		case "image":
            return "image";
		case "sql_variant":
            return "sql_variant";
		case "timestamp":
            return "datetime2";
		case "uniqueidentifier":
            return "uniqueidentifier";
		case "xml":
            return "xml";
		case "cursor":
            return "cursor";
		case "table":
            return "table";
		case "binary":
            return "binary";
		case "varbinary":
            return "varbinary";
           }
        return "nvarchar";
    }

    
}
