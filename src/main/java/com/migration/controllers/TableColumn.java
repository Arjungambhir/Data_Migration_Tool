package com.migration.controllers;

/** 
 * This class contains standard structure we are using for each column
 * @author H264637 (Arjun Gambhir)
 *
 */



public class TableColumn {
    String name;
    String type;
    String nullable;
    String auto_inc;
    String size;
    public TableColumn(String name,String type,String nullable,String auto_inc,String size){
        this.name=name;
        this.auto_inc=auto_inc;
        this.nullable=nullable;
        this.type = type;
        this.size = size;
    }
}
