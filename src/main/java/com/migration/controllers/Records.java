package com.migration.controllers;


/** 
 * 
 * @author H264637 (Arjun Gambhir)
 *
 */


import javafx.beans.property.*;

public class Records {
	private SimpleStringProperty name;
	private SimpleStringProperty srcrows;
	private SimpleStringProperty dstrows;
	private SimpleStringProperty srno;
	
	Records(String srno,String name,String srcrows,String dstrows){
		this.srno=new SimpleStringProperty(srno);
		this.name=new SimpleStringProperty(name);
		this.srcrows=new SimpleStringProperty(srcrows);
		this.dstrows=new SimpleStringProperty(dstrows);
		
	}
	
	public String getName(){
		return name.get();
	}
	
	public void setName(String name)
	{
		this.name.set(name);
	}
	
	public String getSrcrows(){
		return srcrows.get();
	}
	
	public void setSrcrows(String srcrows)
	{
		this.srcrows.set(srcrows);
	}
	
	public String getDstrows(){
		return dstrows.get();
	}
	
	public void setDstrows(String dstrows)
	{
		this.dstrows.set(dstrows);
	}
	
	
	public String getSrno(){
		return srno.get();
	}
	
	public void setSrno(String srno)
	{
		this.srno.set(srno);
	}
	

}
