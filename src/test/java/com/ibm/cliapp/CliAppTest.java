/**
 * 
 */
package com.ibm.cliapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class CliAppTest {
	
	private final static String DEF_CHARSET_NAME = Charset.defaultCharset().name();
	
	TestCliApp cliApp;
	ByteArrayOutputStream out, err;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		err = new ByteArrayOutputStream();
		cliApp = new TestCliApp(null, 
				new PrintStream(out, true, DEF_CHARSET_NAME), 
				new PrintStream(err, true, DEF_CHARSET_NAME));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCliAppRun() {
		
		int retCode = cliApp.run(new String[] { "test", "arg1", "arg2", "arg3", "234" });
		
		assertEquals(5, cliApp.cmdArgs.length);
		assertEquals("test", cliApp.cmdArgs[0]);
		assertEquals("arg1", cliApp.cmdArgs[1]);
		assertEquals("arg2", cliApp.cmdArgs[2]);
		assertEquals("arg3", cliApp.cmdArgs[3]);
		assertEquals("234", cliApp.cmdArgs[4]);
		assertEquals(234, retCode);
	}
	
	@Test
	public void testNullArguments() throws UnsupportedEncodingException {
		
		int retCode = cliApp.run(null);
		
		assertEquals(TestCliApp.ERR_MISSING_ACTION, retCode);
		assertEquals(TestCliApp.DEFAULT_MISSING_ACTION_PARAM_MSG, err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testNullArgumentsWithOverridenHandler() throws UnsupportedEncodingException {
		
		class TestCliAppHandleError extends TestCliApp {
			
			public String actionName = "randomValue";
		
			@Override
			protected int handleActionError(String actionName) {
				this.actionName = actionName;
				return 123;
			}
		};
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		int retCode = cliApp2.run(null);
		
		assertNull(cliApp2.actionName);
		assertEquals(123, retCode);
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testNullArgumentsWithOverridenHandlerThrowingException() throws UnsupportedEncodingException {
		
		class TestCliAppHandleError extends TestCliApp {
			
			@Override
			protected int handleActionError(String actionName) {
				throw new IllegalArgumentException("Some message");
			}
		};
		
		IllegalArgumentException iaex = null;
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		try {
			int retCode = cliApp2.run(null);
		} catch (IllegalArgumentException ex) {
			iaex = ex;
		}
		
		assertNotNull(iaex);
		assertEquals("Some message", iaex.getMessage());
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testDefaultMissingActionErrorMessage() throws UnsupportedEncodingException {
		
		int retCode = cliApp.run(new String[0]);
		
		assertEquals(TestCliApp.ERR_MISSING_ACTION, retCode);
		assertEquals(TestCliApp.DEFAULT_MISSING_ACTION_PARAM_MSG, err.toString(DEF_CHARSET_NAME));
	}

	@Test
	public void testMissingActionErrorMessageWithOverridenHandler() throws UnsupportedEncodingException {
		
		
		class TestCliAppHandleError extends TestCliApp {
			
			public String actionName = "randomValue";
		
			@Override
			protected int handleActionError(String actionName) {
				this.actionName = actionName;
				return 123;
			}
		};
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		int retCode = cliApp2.run(new String[0]);
		
		assertNull(cliApp2.actionName);
		assertEquals(123, retCode);
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testMissingActionWithOverridenHandlerThrowingException() throws UnsupportedEncodingException {
		
		class TestCliAppHandleError extends TestCliApp {
			
			@Override
			protected int handleActionError(String actionName) {
				throw new IllegalArgumentException("Some message");
			}
		};
		
		IllegalArgumentException iaex = null;
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		try {
			int retCode = cliApp2.run(new String[0]);
		} catch (IllegalArgumentException ex) {
			iaex = ex;
		}
		
		assertNotNull(iaex);
		assertEquals("Some message", iaex.getMessage());
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testUnknownActionErrorMessage() throws UnsupportedEncodingException {
		
		int retCode = cliApp.run(new String[] { "unknownaction" });
		
		assertEquals(TestCliApp.ERR_UNKNOWN_ACTION, retCode);
		assertEquals(String.format(TestCliApp.DEFAULT_UNKNOWN_ACTION_PARAM_MSG, "unknownaction"), err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testUnknownActionErrorMessageWithOverridenHandler() throws UnsupportedEncodingException {
		
		
		class TestCliAppHandleError extends TestCliApp {
			
			public String actionName = "randomValue";
		
			@Override
			protected int handleActionError(String actionName) {
				this.actionName = actionName;
				return 123;
			}
		};
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		int retCode = cliApp2.run(new String[] { "unknownaction" });
		
		assertEquals("unknownaction", cliApp2.actionName);
		assertEquals(123, retCode);
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testUnknownActionWithOverridenHandlerThrowingException() throws UnsupportedEncodingException {
		
		class TestCliAppHandleError extends TestCliApp {
			
			@Override
			protected int handleActionError(String actionName) {
				throw new IllegalArgumentException("Some message");
			}
		};
		
		IllegalArgumentException iaex = null;
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		try {
			int retCode = cliApp2.run(new String[] { "unknownaction" });
		} catch (IllegalArgumentException ex) {
			iaex = ex;
		}
		
		assertNotNull(iaex);
		assertEquals("Some message", iaex.getMessage());
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}
	
	@Test
	public void testActionThrowingException() throws UnsupportedEncodingException {
		
		int retCode = cliApp.run(new String[] { "throw", "arg1", "arg2", "arg3", "exception message" });
		
		assertEquals(CliApp.ERR_ACTION_EXCEPTION, retCode);
		assertNotNull(cliApp.getLastActionException());
		assertEquals("exception message", cliApp.getLastActionException().getMessage());
		assertEquals(String.format(TestCliApp.DEFAULT_ACTION_EXECUTION_ERR_MSG, "throw", "exception message"), 
				err.toString(DEF_CHARSET_NAME));
	}

	@Test
	public void testActionThrowingExceptionWithOverridenHandler() throws UnsupportedEncodingException {
		
		class TestCliAppHandleError extends TestCliApp {
			
			@Override
			protected int handleActionExecutionException(String actionName, RuntimeException rex) {
				return 100;
			}
		};
		
		TestCliAppHandleError cliApp2 = new TestCliAppHandleError();
		int retCode = cliApp2.run(new String[] { "throw", "arg1", "arg2", "arg3", "exception message" });
		
		assertEquals(100, retCode);
		assertNotNull(cliApp2.getLastActionException());
		assertEquals("exception message", cliApp2.getLastActionException().getMessage());
		assertEquals("", err.toString(DEF_CHARSET_NAME));
	}

}
