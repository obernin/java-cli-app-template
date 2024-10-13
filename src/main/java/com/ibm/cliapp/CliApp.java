package com.ibm.cliapp;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class CliApp<A extends CliApp<?>> {
	
	public static final String DEFAULT_MISSING_ACTION_PARAM_MSG = "No action name provided";
	public static final String DEFAULT_UNKNOWN_ACTION_PARAM_MSG = "No action with '%s'";
	public static final String DEFAULT_ACTION_EXECUTION_ERR_MSG = "Error executing action '%s': %s";
	
	private InputStream in;
	private PrintStream out;
	private PrintStream err;
	private Map<String, CliAction<A>> actions;
	private final Options options = new Options();
	
	
	public CliApp(InputStream in, PrintStream out, PrintStream err) {
		
		this.in = in == null ? System.in : in;
		this.out = out == null ? System.out : out;
		this.err = err == null ? System.err : err;
	}

	protected final void addAction(String name, CliAction<A> action) {
		actions.put(name, action);
	}
	
	protected int execute(CommandLine cmdLine) {
		return executeAction(cmdLine.getArgs());
	}
	
	@SuppressWarnings("unchecked") // It's okay to make the cast `(A) this` as A extends from CliApp
	protected final int executeAction(String[] args) {
		
		int retCode = 0;
		
		if (args == null || args.length < 1)
			throw new IllegalArgumentException(generateMissingActionErrorMessage());
		
		CliAction<A> action = actions.get(args[0]);
		if (action == null)
			throw new IllegalArgumentException(generateUnknownActionErrorMessage(args[0]));
		
		try {
			retCode = action.execute((A) this, args);
		
		} catch (RuntimeException rex) {
			String message = generateActionExecutionErrorMessage(args[0], rex); 
			if (message != null)
				err.println(message);
			
			retCode = 255;
		}
		
		return retCode;
	}
	
	protected String generateActionExecutionErrorMessage(String actionName, Exception ex) {
		return String.format(DEFAULT_ACTION_EXECUTION_ERR_MSG, actionName, ex.getMessage());
	}
	
	protected String generateMissingActionErrorMessage() {
		return DEFAULT_MISSING_ACTION_PARAM_MSG;
	}

	protected String generateUnknownActionErrorMessage(String actionName) {
		return String.format(DEFAULT_UNKNOWN_ACTION_PARAM_MSG, actionName);
	}
	
	protected final InputStream getInputStream() {
		return this.in;
	}
	
	protected final PrintStream getOutputStream() {
		return this.out;
	}

	protected final PrintStream getErrorStream() {
		return this.err;
	}
	
	protected int handleParseException(ParseException pex) {
		
		err.println(pex.getMessage());
		return 1;
	}
	
	public final void run(String[] args) {
		
		int retCode;
		
		try {
			CommandLine cmdLine = new DefaultParser().parse(options, args);
			retCode = this.execute(cmdLine);

		} catch (ParseException pex) {
			retCode = handleParseException(pex);
		}
		
		System.exit(retCode);
	}
}
