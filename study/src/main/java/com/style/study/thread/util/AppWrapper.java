package com.style.study.thread.util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AppWrapper {

	public static void invokeMain0(Class<?> app, String[] args, int prePause,
	    int postPause, boolean inputRedirect) throws Exception {
		if (inputRedirect) {
			if (null == System.getProperty("x.std.in")) {
				throw new RuntimeException(
				    "System property x.std.in was not specified.");
			}
		}

		int argc = args.length;

		if (prePause > 0) {
			System.setProperty("x.prepause", String.valueOf(prePause));
		}
		
		if (postPause > 0) {
			System.setProperty("x.postpause", String.valueOf(postPause));
		}

		String[] allArgs = new String[argc + 1];
		allArgs[0] = app.getCanonicalName();
		int i = 0;
		for (String arg : args) {
			allArgs[++i] = arg;
		}
		AppWrapper.main(allArgs);
	}

	public static void invokeMain0(Class<?> app, String[] args) throws Exception {
		invokeMain0(app, args, 0, 0, false);
	}

	public static void invokeMain0(Class<?> app, String[] args,
	    boolean inputRedirect) throws Exception {
		invokeMain0(app, args, 0, 0, inputRedirect);
	}

	public static void main(String[] args) throws Exception {
		int argc = args.length;
		String destClass = args[0];

		String[] passArgs;
		passArgs = Arrays.copyOfRange(args, 1, argc);

		int prePause;
		int postPause;
		String strPrePause = System.getProperty("x.prepause");
		String strPostPause = System.getProperty("x.postpause");
		prePause = (null != strPrePause) ? Integer.valueOf(strPrePause) : 0;
		postPause = (null != strPrePause) ? Integer.valueOf(strPostPause) : 0;

		String stdInFile = System.getProperty("x.std.in");
		if (null != stdInFile) {
			System.setIn(new FileInputStream(new File(stdInFile)));
		}

		Thread.sleep(prePause);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date start = null;

		Method method = getForwardMethod(destClass);
		start = new Date();
		System.out.println("Arguments:" + Arrays.toString(args));
		System.out.println("Started at:" + sdf.format(start));
		method.invoke(null, new Object[] { passArgs });

		Date end = new Date();
		System.out.println("Finished at:" + sdf.format(end));
		System.out.println("Time consumed:" + (end.getTime() - start.getTime())
		    + "ms");

		Thread.sleep(postPause);

	}

	private static Method getForwardMethod(String destClass) {
		Method method = null;
		try {
			method = Class.forName(destClass).getMethod("main0", String[].class);
		} catch (NoSuchMethodException e) {
			System.err.println("main0 was not defined!");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return method;
	}

}
