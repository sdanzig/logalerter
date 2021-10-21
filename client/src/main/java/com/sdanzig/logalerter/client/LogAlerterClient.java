package com.sdanzig.logalerter.client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LogAlerterClient {
	private static final Logger log = LoggerFactory.getLogger(LogAlerterClient.class);

	private static final String DEFAULT_FILENAME = "config.txt";

	private static String serverUrl;
	private static String serverUiUrl;
	private static String emailAddress;
	private static Map<File, Long> filesToLogIdMap;

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
				new SpringApplicationBuilder(LogAlerterClient.class,
						LogAlerterClientController.class,
						LogAlerterClientUI.class).headless(false).run(args);
		EventQueue.invokeLater(() -> {
			LogAlerterClientUI ui = ctx.getBean(LogAlerterClientUI.class);
			LogAlerterClientController controller = ctx.getBean(LogAlerterClientController.class);
			controller.setServerUrl(serverUrl);
			controller.setEmailAddress(emailAddress);
			controller.setFilesToLogIdMap(filesToLogIdMap);
			controller.startThread();
			ui.setServerUiUrl(serverUiUrl);
			ui.setVisible(true);
			ui.activateClientUi();
		});
	}

	private boolean processConfig(String[] args) {
		BufferedReader reader;
		String fileToRead = (args.length >= 1) ? args[0] : DEFAULT_FILENAME;
		Map<File, Long> filesToLogIdMapBeingBuilt = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(fileToRead));
			String line1 = reader.readLine();
			if(line1 == null) {
				log.error("No server URL configured in file \"{}\"", fileToRead);
				return false;
			}
			serverUrl = line1;
			String line2 = reader.readLine();
			if(line2 == null) {
				log.error("No server UI URL configured in file \"{}\"", fileToRead);
				return false;
			}
			serverUiUrl = line2;
			String line3 = reader.readLine();
			if(line3 == null) {
				log.error("No email address configured in file \"{}\"", fileToRead);
				return false;
			}
			emailAddress = line3;
			String line = reader.readLine();
			while (line != null) {
				String[] parts = line.split("\\|");
				if(parts.length != 2) {
					log.warn("Invalid line in config file, [{}]",line);
					continue;
				}
				String filePath = parts[0];
				Long logId = null;
				try {
					logId = Long.parseLong(parts[1]);
				} catch(NumberFormatException nfe) {
					log.warn("Could not parse log ID in config file line, [{}]",line);
					continue;
				}
				File file = new File(filePath);
				if(!file.exists()) {
					log.warn("No file found yet at path \"{}\"", file.getAbsolutePath());
				}
				filesToLogIdMapBeingBuilt.put(file, logId);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			log.error("Error reading config file, \"{}\"", fileToRead, e);
			return false;
		}
		if(filesToLogIdMapBeingBuilt.size() < 1) {
			log.error("Files to monitor not listed in config file. Aborting.");
			return false;
		}
		filesToLogIdMap = filesToLogIdMapBeingBuilt;
		return true;
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			if(!processConfig(args)) {
				return;
			}
		};
	}
}
