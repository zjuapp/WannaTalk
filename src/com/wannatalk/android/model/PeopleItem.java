package com.wannatalk.android.model;

import java.io.Serializable;

public class PeopleItem implements Serializable{
	private static final long serialVersionUID = 1L;
	public final static String [] STATUS = new String [] {"happy", "annoyed", "sorror", "justsoso"};
	public int id;
	public boolean sex;
	public String imgtext;
	public String status;
	public String happen;
}