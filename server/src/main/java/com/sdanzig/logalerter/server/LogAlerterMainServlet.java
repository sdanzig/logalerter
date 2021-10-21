package com.sdanzig.logalerter.server;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class LogAlerterMainServlet extends HttpServlet {
	Logger logger = LoggerFactory.getLogger(LogAlerterMainServlet.class);
}
