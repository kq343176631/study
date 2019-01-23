package com.style.utils.network;

import java.io.*;

/**
 * CommandUtils
 */
public class CmdUtils{

	public static String execute(String command) throws IOException {
		return execute(command, "GBK");
	}
	
	public static String execute(String command, String charsetName) throws IOException {
		Process process = Runtime.getRuntime().exec(command);
		// 记录dos命令的返回信息
		StringBuilder stringBuffer = new StringBuilder();
		// 获取返回信息的流
		InputStream in = process.getInputStream();
		Reader reader = new InputStreamReader(in, charsetName);
		BufferedReader bReader = new BufferedReader(reader);
		String res = bReader.readLine();
		while (res != null) {
			stringBuffer.append(res);
			stringBuffer.append("\n");
			res = bReader.readLine();
		}
		bReader.close();
		reader.close();
		return stringBuffer.toString();
	}
}
