/**
 * 
 */
package com.ibm.cliapp;

/**
 * @author obernin
 *
 */
public interface CliCommand<App extends CliApp<?>> {

	public int execute(App parentCliApp, String[] parameters) throws IllegalArgumentException;
}
