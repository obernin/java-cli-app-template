/**
 * 
 */
package com.ibm.cliapp;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * 
 */
public class TestCliApp extends CliApp<TestCliApp> {
	
	TestLib testLib;
	String[] cmdArgs = null;
	CommandLine cmdLine;
	
	public TestCliApp(TestLib testLib) {
		this(testLib, null, null, null);
	}

	/**
	 * 
	 */
	public TestCliApp(TestLib testLib, InputStream in, PrintStream out, PrintStream err) {
		
		super(in, out, err);
		
		this.testLib = testLib;

		this.addOption(Option.builder("to").build());		
		
		this.addCommand("method", new TestCliAppMethodCommand());
		
		this.addCommand("throw", (parentCliApp, parameters) -> { 
			parentCliApp.setCommandCalled(parameters); 
			return parentCliApp.throwing();
		});
		
		this.addCommand("noparam", (parentCliApp, parameters) -> { 
			parentCliApp.setCommandCalled(parameters); 
			return parentCliApp.noparam();
		});
	}
	
	public int method(String method) {
		return testLib.method(method);
	}
	
	public int noparam() {
		return testLib.noparam();
	}
	
	public int throwing() {
		return testLib.throwing();
	}
	
	// Methods used for testing
	
	public void setCommandCalled(String[] cmdArgs) {
		this.cmdArgs = cmdArgs;
	}
}
