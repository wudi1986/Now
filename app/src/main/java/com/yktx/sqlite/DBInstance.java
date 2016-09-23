package com.yktx.sqlite;


public final class DBInstance {

	private static final DBInstance ins = new DBInstance();
	
	public static DBInstance getInstance(){
		return ins;
	}
}
