package com.ibm.cliapp;

public class TestLib {
	
	private int config;

	public TestLib(int config) {
		this.config = config;
	}
	
	public int method(String intValue) {
		return intValue == null ? config : Integer.parseInt(intValue);
	}
	
	public int noparam() {
		return config;
	}
	
	public int throwing() {
		throw new RuntimeException("exception message from TestLib.throwing");
	}

}
