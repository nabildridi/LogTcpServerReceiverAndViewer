package org.nd.logging.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.nd.logging.model.ProcessedLog;
import org.nd.logging.threads.SseSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
	private static Logger logger = LoggerFactory.getLogger(SseService.class);
	private Map<String, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();
	private ExecutorService executor = Executors.newFixedThreadPool(100);

	public SseEmitter createEmitter(String memberId) {
		SseEmitter emitter = new SseEmitter(180000L);
		emitter.onCompletion(() -> remove(memberId));
		emitter.onTimeout(() -> remove(memberId));
		emitter.onError(e -> {
			logger.error("Create SseEmitter exception", e);
			remove(memberId);
		});
		addOrReplaceEmitter(memberId, emitter);
		return emitter;
	}

	public void addOrReplaceEmitter(String memberId, SseEmitter emitter) {
		userEmitterMap.put(memberId, emitter);
	}

	public void remove(String memberId) {
		if (userEmitterMap.containsKey(memberId)) {
			SseEmitter emitter = userEmitterMap.remove(memberId);
			try {
				emitter.complete();
			} catch (Exception e) {
			}
		}
	}

	public Optional<SseEmitter> get(String memberId) {
		return Optional.ofNullable(userEmitterMap.get(memberId));
	}

	public void sendToClients(List<ProcessedLog> ProcessedLogs) {

		List<Future<Optional<String>>> list = new ArrayList<Future<Optional<String>>>();
		userEmitterMap.entrySet().forEach(entry -> {

			Future<Optional<String>> future = executor.submit(new SseSender(entry, ProcessedLogs));
			list.add(future);

		});

		for (Future<Optional<String>> future : list) {
			try {
				// print the return value of Future, notice the output delay in console
				// because Future.get() waits for task to get completed
				Optional<String> optional = future.get();
				if (optional.isPresent()) {
					remove(optional.get());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
