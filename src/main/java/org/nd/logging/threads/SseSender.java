package org.nd.logging.threads;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.nd.logging.model.ProcessedLog;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

public class SseSender implements Callable<Optional<String>> {

	private List<ProcessedLog> ProcessedLogs;
	private Entry<String, SseEmitter> entry;

	public SseSender(Entry<String, SseEmitter> entry, List<ProcessedLog> ProcessedLogs) {
		super();
		this.entry = entry;
		this.ProcessedLogs = ProcessedLogs;
	}

	@Override
	public Optional<String> call() throws Exception {
		for (ProcessedLog processedLog : ProcessedLogs) {

			SseEventBuilder seb = toSseEventBuilder(processedLog);

			try {
				entry.getValue().send(seb);
			} catch (Exception e) {
				return Optional.of(entry.getKey());
			}
		}
		return Optional.empty();

	}

	private SseEventBuilder toSseEventBuilder(ProcessedLog processedLog) {
		return SseEmitter.event().data(processedLog, MediaType.APPLICATION_JSON);
	}

}
