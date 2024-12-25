# LogTcpServerReceiverAndViewer
A Tcp server that receive and display log messages (Structured log formats : Logstach, Elasticsearh and Graylog) from multiple remote applications 

# About the project :
The project is composed by two parts :
- A Tcp server that receives *structured log messages (Logstach, Elasticsearh and Graylog)* from remote applications
- A web application that displays the received messages

# Important note :
The project is very basic.
**It does not persist log messages or offer a search function**

# Configuration :
In application.properties we can configure :
- The Tcp server port with '**logging.server.port**'
- The web application port with '**server.port**'

# The web interface :
The web interface is accessible with the url :  **http://localhost:{server.port}**

# How to send structured log messages from your applications:
In your pom.xml, import the socket appender :

    <dependency>
        <groupId>me.moocar</groupId>
        <artifactId>socket-encoder-appender</artifactId>
        <version>0.1beta1</version>
    </dependency>


Then import one of those encoders :

    <dependency>    
	    <groupId>co.elastic.logging</groupId>    
	    <artifactId>logback-ecs-encoder</artifactId>    
	    <version>1.6.0</version>    
    </dependency>   
      
    
    <dependency>    
	    <groupId>net.logstash.logback</groupId>    
	    <artifactId>logstash-logback-encoder</artifactId>    
	    <version>8.0</version>    
    </dependency>
          
    
    <dependency>    
	    <groupId>de.siegmar</groupId>    
	    <artifactId>logback-gelf</artifactId>    
	    <version>6.1.0</version>    
    </dependency>


In your logback-spring.xml, add the appender and the encoder :

    <appender name="SOCKET_APPENDER" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    	<encoder class="co.elastic.logging.logback.EcsEncoder"/>
    	<!-- <encoder class="net.logstash.logback.encoder.LogstashEncoder"/> -->
    	<!-- <encoder class="de.siegmar.logbackgelf.GelfEncoder"/> -->
    	<destination>127.0.0.1:{logging.server.port}</destination>
    </appender> 

Note : uncomment and comment your preferred format.


  
