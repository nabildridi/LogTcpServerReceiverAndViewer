package org.nd.logging.controllers;

import org.nd.logging.services.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AppController {

	@Autowired
	private SseService sseService;

	@GetMapping("/")
	public String viewHomePage(Model model) {
		return "index";
	}

	@GetMapping("/sse")
	public SseEmitter sse(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		return sseService.createEmitter(ip);

	}

}
