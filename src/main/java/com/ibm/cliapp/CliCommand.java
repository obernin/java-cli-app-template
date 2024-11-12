/**
 * 
 */
package com.ibm.cliapp;

/**
 * @author obernin
 *
 */
public interface CliCommand<App extends CliApp<?>> {

	public int execute(App cliApp, String[] parameters) throws IllegalArgumentException;
}
