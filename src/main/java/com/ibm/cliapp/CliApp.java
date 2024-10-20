package com.ibm.cliapp;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class CliApp<A extends CliApp<?>> {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final int ERR_OK = 0;
	public static final int ERR_INVALID_OPTION = ERR_OK + 1;
	public static final int ERR_MISSING_ACTION = ERR_INVALID_OPTION + 1;
	public static final int ERR_UNKNOWN_ACTION = ERR_MISSING_ACTION + 1;
	public static final int ERR_ACTION_EXCEPTION = ERR_UNKNOWN_ACTION + 1;
	
	public static final String DEFAULT_MISSING_ACTION_PARAM_MSG = String.format("No action name provided.%s", LINE_SEPARATOR);
	public static final String DEFAULT_UNKNOWN_ACTION_PARAM_MSG = String.format("No action named '%%s'.%s", LINE_SEPARATOR);
	public static final String DEFAULT_ACTION_EXECUTION_ERR_MSG = String.format("Error executing action '%%s': %%s.%s", LINE_SEPARATOR);
	
	
	private InputStream in;
	private PrintStream out;
	private PrintStream err;
	private RuntimeException lastThrownException;
	private Map<String, CliAction<A>> actions = new HashMap<String, CliAction<A>>();
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
		
		if (args == null || args.length < 1) {
			retCode = handleActionError(null);
		
		} else {
			CliAction<A> action = actions.get(args[0]);
			if (action == null) {
				retCode = handleActionError(args[0]);
			
			} else {
				try {
					retCode = action.execute((A) this, args);
				
				} catch (RuntimeException rex) {
					this.lastThrownException = rex;
					retCode = handleActionExecutionException(args[0], rex);
				}
			}
		}
		
		return retCode;
	}
	
	protected final PrintStream getErrorStream() {
		return this.err;
	}
	
	protected final InputStream getInputStream() {
		return this.in;
	}
	
	public RuntimeException getLastActionException() {
		return lastThrownException;
	}
	
	protected final Options getOptions() {
		return this.options;
	}
	
	protected final PrintStream getOutputStream() {
		return this.out;
	}
	
	protected int handleActionError(String actionName) {
		
		if (actionName == null)
			this.getErrorStream().print(DEFAULT_MISSING_ACTION_PARAM_MSG);
		else
			this.getErrorStream().print(String.format(DEFAULT_UNKNOWN_ACTION_PARAM_MSG, actionName));
				
		return actionName == null ? ERR_MISSING_ACTION : ERR_UNKNOWN_ACTION;
	}
	
	protected int handleActionExecutionException(String actionName, RuntimeException rex) {
		
		err.print(String.format(DEFAULT_ACTION_EXECUTION_ERR_MSG, actionName, rex.getMessage()));
		return ERR_ACTION_EXCEPTION;
	}

	protected int handleOptionParseException(ParseException pex) {
		
		err.print(pex.getMessage());
		return ERR_INVALID_OPTION;
	}
	
	protected void initialize(CommandLine cmdLine) {
		// do nothing
	}
	
	public final int run(String[] args) {
		
		int retCode;
		
		try {
			CommandLine cmdLine = new DefaultParser().parse(options, args);
			this.initialize(cmdLine);
			retCode = this.execute(cmdLine);

		} catch (ParseException pex) {
			retCode = handleOptionParseException(pex);
		}
		
		return retCode;
	}
}
