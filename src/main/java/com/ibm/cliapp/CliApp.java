package com.ibm.cliapp;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class CliApp<ThisApp extends CliApp<?>> {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final int ERR_OK = 0;
	public static final int ERR_INVALID_OPTION = ERR_OK + 1;
	public static final int ERR_MISSING_ACTION = ERR_INVALID_OPTION + 1;
	public static final String DEFAULT_MISSING_ACTION_PARAM_MSG = String.format("No command name provided.%s", LINE_SEPARATOR);
	public static final int ERR_UNKNOWN_ACTION = ERR_MISSING_ACTION + 1;
	public static final String DEFAULT_UNKNOWN_ACTION_PARAM_MSG = String.format("Unknown command '%%s'.%s", LINE_SEPARATOR);
	public static final int ERR_ACTION_EXCEPTION = ERR_UNKNOWN_ACTION + 1;
	public static final String DEFAULT_ACTION_EXECUTION_ERR_MSG = String.format("Error executing command '%%s': %%s%s", LINE_SEPARATOR);
	
	
	private CommandLine cmdLine = null;
	private InputStream in;
	private PrintStream out;
	private PrintStream err;
	private RuntimeException lastThrownException;
	private Map<String, CliCommand<ThisApp>> commands = new HashMap<String, CliCommand<ThisApp>>();
	private final Options options = new Options();
	
	
	/**
	 * Shorten a long option name composed of hyphen (-) separated parts to a 
	 * string made up of the first letter of each part.
	 * E.g. some-long-option-name -> slon
	 * 
	 * @param longOptionName
	 * @return
	 */
	public static String shortenOptionName(String longOptionName) {
		
		StringBuilder sb = new StringBuilder();
		
		String[] components = longOptionName.split("-+");
		for (String component : components) {
			sb.append(component.charAt(0));
		}
		return sb.toString();
	}
	
	public CliApp(InputStream in, PrintStream out, PrintStream err) {
		
		this.in = in == null ? System.in : in;
		this.out = out == null ? System.out : out;
		this.err = err == null ? System.err : err;
	}

	protected final void addCommand(String name, CliCommand<ThisApp> command) {
		commands.put(name, command);
	}
	
	protected final void addOption(Option option) {
		options.addOption(option);
	}
	
	protected int execute(CommandLine cmdLine) {
		return executeAction(cmdLine.getArgs());
	}
	
	@SuppressWarnings("unchecked") // It's okay to make the cast `(ThisApp) this` as ThisApp extends from CliApp
	protected final int executeAction(String[] args) {
		
		int retCode = 0;
		
		if (args == null || args.length < 1) {
			retCode = handleActionError(null);
		
		} else {
			CliCommand<ThisApp> command = commands.get(args[0]);
			if (command == null) {
				retCode = handleActionError(args[0]);
			
			} else {
				try {
					retCode = command.execute((ThisApp) this, args);
				
				} catch (RuntimeException rex) {
					this.lastThrownException = rex;
					retCode = handleActionExecutionException(args[0], rex);
				}
			}
		}
		
		return retCode;
	}
	
	private String generateCommandHelp() {
		return null;
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
	
	protected final Option getOption(String optionName) {
		
		for (Option option : cmdLine.getOptions()) {
			if (optionName.equals(option.getOpt()) || optionName.equals(option.getLongOpt()))
				return option;
		}
		
		return null;
	}
	
	protected final Option[] getOptions() {
		return this.cmdLine.getOptions();
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
	
	protected final boolean isOptionSet(String optionName) {
		return getOption(optionName) != null;
	}
	
	protected void printUsage(PrintStream stream, String cmdName) {
		
		Set<String> actionNames = new TreeSet<String>(commands.keySet());
		
		stream.print("Actions are:");
		for (String actionName : actionNames) {
			stream.print(String.format("%s\t\n", actionName));
		}
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(cmdName, "\nOptions are:", options, "footer", true);
	}
	
	public final int run(String[] args) {
		
		int retCode;
		
		try {
			cmdLine = new DefaultParser().parse(options, args);
			this.initialize(cmdLine);
			retCode = this.execute(cmdLine);

		} catch (ParseException pex) {
			retCode = handleOptionParseException(pex);
		}
		
		return retCode;
	}
}
