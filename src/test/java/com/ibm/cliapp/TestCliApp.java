/**
 * 
 */
package com.ibm.cliapp;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * 
 */
public class TestCliApp extends CliApp<TestCliApp> {
	
	public TestCliApp() {
		this(null, null, null);
	}

	/**
	 * 
	 */
	public TestCliApp(InputStream in, PrintStream out, PrintStream err) {
		super(in, out, err);

		this.addAction("test", /*new CliAction<TestCliApp>() {
			public int execute(TestCliApp parentCliApp, String[] parameters) throws IllegalArgumentException {
				return 0;
				
			}			
		} */
				(TestCliApp parentCliApp, String[] parameters) -> 0
		);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new TestCliApp().run(args);
	}

}
