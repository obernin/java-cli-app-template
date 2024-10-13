/**
 * 
 */
package com.ibm.cliapp;

/**
 * @author obernin
 *
 */
public interface CliAction<A extends CliApp<?>> {

	public int execute(A parentCliApp, String[] parameters) throws IllegalArgumentException;
}
