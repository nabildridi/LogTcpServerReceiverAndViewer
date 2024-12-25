package org.nd.logging.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {

		
		String s = " Message Received : version\":\"1\",\"message\":\"1732732325666\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:10.6749672+01:00\",\"@version\":\"1\",\"message\":\"1732732330674\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:15.6801385+01:00\",\"@version\":\"1\",\"message\":\"1732732335680\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:20.6733863+01:00\",\"@version\":\"1\",\"message\":\"1732732340673\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:25.6700614+01:00\",\"@version\":\"1\",\"message\":\"1732732345670\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:30.6684295+01:00\",\"@version\":\"1\",\"message\":\"1732732350668\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:35.6801476+01:00\",\"@version\":\"1\",\"message\":\"1732732355680\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:40.6708329+01:00\",\"@version\":\"1\",\"message\":\"1732732360670\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}\r\n"
				+ "{\"@timestamp\":\"2024-11-27T19:32:45.6800267+01:00\",\"@version\":\"1\",\"message\":\"1732732365680\",\"logger_name\":\"org.nd.primeng.services.UsersService\",\"thread_name\":\"scheduling-1\",\"level\":\"DEBUG\",\"level_value\":10000}";
		
		Pattern p = Pattern.compile("\\{.*\\}");
		 Matcher m = p.matcher(s);
		 
		 while(m.find()) {
	         System.out.println(m.group());
	      }

	}

}
