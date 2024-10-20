/**
 * 
 */
package com.ibm.cliapp;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;

/**
 * 
 */
public class TestCliApp extends CliApp<TestCliApp> {
	
	String[] cmdArgs = null;
	CommandLine cmdLine;
	
	public TestCliApp() {
		this(null, null, null);
	}

	/**
	 * 
	 */
	public TestCliApp(InputStream in, PrintStream out, PrintStream err) {
		super(in, out, err);

		this.addAction("test", (parentCliApp, parameters) -> { 
			parentCliApp.setCommandCalled(parameters); 
			return Integer.parseInt(parameters[4]); 
		});
		
		this.addAction("throw", (parentCliApp, parameters) -> { 
			parentCliApp.setCommandCalled(parameters); 
			throw new RuntimeException(parameters[4]);
		});
	}
	
	// Methods used for testing
	
	public void setCommandCalled(String[] cmdArgs) {
		this.cmdArgs = cmdArgs;
	}
}
