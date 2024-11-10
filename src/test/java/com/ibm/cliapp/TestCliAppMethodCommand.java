/**
 * 
 */
package com.ibm.cliapp;

/**
 * 
 */
public class TestCliAppMethodCommand implements CliCommand<TestCliApp> {

	@Override
	public int execute(TestCliApp parentCliApp, String[] parameters) throws IllegalArgumentException {
		parentCliApp.setCommandCalled(parameters); 
		return parentCliApp.method(parameters[4]);
	}

}
