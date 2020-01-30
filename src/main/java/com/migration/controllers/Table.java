package com.migration.controllers;



/** 
 * 
 * @author H264637 (Arjun Gambhir)
 *
 */


import java.util.List;

public class Table {
    String name;
    List<TableColumn> columns;
    List<String> primary;
    List<String> foreign;

    public Table(String name,List<TableColumn> columns,List<String> primary,List<String> foreign){
        this.name = name;
        this.columns = columns;
        this.foreign=foreign;
        this.primary=primary;
    }
}
