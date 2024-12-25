package org.nd.logging.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nd.logging.dto.RawLog;
import org.nd.logging.enums.LogLevel;
import org.nd.logging.enums.LogType;
import org.nd.logging.model.ProcessedLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageProcessingService {

	private ObjectMapper mapper = new ObjectMapper();

	private ConcurrentLinkedQueue<RawLog> receivedLogs = new ConcurrentLinkedQueue<RawLog>();

	private static Logger logger = LoggerFactory.getLogger(MessageProcessingService.class);

	Pattern p = Pattern.compile("\\{.*\\}");

	@Autowired
	private SseService sseService;

	public void putInQueue(RawLog rawLog) {
		receivedLogs.offer(rawLog);
	}

	@Scheduled(fixedRate = 5000)
	private void checkQueue() {
		RawLog rawLog;
		if (!receivedLogs.isEmpty()) {
			do {
				rawLog = receivedLogs.poll();
				if (rawLog != null) {
					process(rawLog);
				}
			} while (rawLog != null);
		}

	}

	private void process(RawLog rawLog) {
		List<ProcessedLog> ProcessedLogs = new ArrayList<ProcessedLog>();
		// logger.debug("Message Received : {} from {}", rawLog.getMessage(), rawLog.getRemoteAddress());

		// splice
		Matcher m = p.matcher(rawLog.getMessage());

		while (m.find()) {
			String logJson = m.group();
			LogType logType = detectType(logJson);
			if (logType != LogType.UNKNOWN) {
				ProcessedLog processedLog = extractData(logJson, rawLog.getRemoteAddress(), logType);
				ProcessedLogs.add(processedLog);
			}

		}

		sseService.sendToClients(ProcessedLogs);

	}

	private LogType detectType(String logJson) {
		if (logJson.contains("ecs.version")) {
			return LogType.ECS;
		} else if (logJson.contains("host")) {
			return LogType.GRAYLOG;
		} else if (logJson.contains("@version")) {
			return LogType.LOGSTASH;
		} else {
			return LogType.UNKNOWN;
		}
	}

	private LogLevel detectLevel(String level) {
		LogLevel logLevel = null;
		try {
			logLevel = LogLevel.valueOf(level);
		} catch (Exception e) {
		}

		if (logLevel == null) {
			if (level.equals("7"))
				logLevel = LogLevel.DEBUG;
			if (level.equals("6"))
				logLevel = LogLevel.INFO;
			if (level.equals("4"))
				logLevel = LogLevel.WARN;
			if (level.equals("3"))
				logLevel = LogLevel.ERROR;
		}

		return logLevel;
	}

	private ProcessedLog extractData(String logJson, String remoteAddress, LogType logType) {
		ProcessedLog processedLog = new ProcessedLog();

		processedLog.setRemoteAddress(remoteAddress);
		processedLog.setLogType(logType);

		try {
			JsonNode node = mapper.readTree(logJson.getBytes(StandardCharsets.UTF_8));

			if (logType == LogType.LOGSTASH) {
				String value = node.get("@timestamp").asText();
				Date date = Date.from(Instant.parse(value));
				Long timestamp = date.toInstant().toEpochMilli();
				processedLog.setTimestamp(timestamp);

				value = node.get("message").asText();
				processedLog.setMessage(value);

				value = node.get("logger_name").asText();
				processedLog.setLoggerName(value);

				value = node.get("level").asText();
				processedLog.setLevel(detectLevel(value));
			}

			if (logType == LogType.ECS) {
				String value = node.get("@timestamp").asText();
				Date date = Date.from(Instant.parse(value));
				Long timestamp = date.toInstant().toEpochMilli();
				processedLog.setTimestamp(timestamp);

				value = node.get("message").asText();
				processedLog.setMessage(value);

				value = node.get("log.logger").asText();
				processedLog.setLoggerName(value);

				value = node.get("log.level").asText();
				processedLog.setLevel(detectLevel(value));
			}

			if (logType == LogType.GRAYLOG) {
				double v = node.get("timestamp").asDouble();
				v = v * 100;
				processedLog.setTimestamp((long) v);

				String value = node.get("full_message").asText();
				processedLog.setMessage(value);

				value = node.get("_logger_name").asText();
				processedLog.setLoggerName(value);

				value = node.get("level").asText();
				processedLog.setLevel(detectLevel(value));
			}

		} catch (Exception e) {

		}

		return processedLog;
	}
}
