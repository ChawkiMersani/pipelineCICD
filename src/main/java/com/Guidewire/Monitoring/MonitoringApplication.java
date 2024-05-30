package com.Guidewire.Monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EnableScheduling
@SpringBootApplication
public class MonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoringApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(MonitoringApplication.class);


	@Scheduled(cron = "0 0 0 * * ?")  // Cron expression to run this task every day at midnight

	//@Scheduled(cron="0 * * * * ?")// Schedules the task to run at midnight every day
	public void backupDatabase() {
		log.info("Executing database backup...");

		// Database configuration
		String dbName = "gw_logs";
		String backupPath = "C:\\Users\\cmersani001\\Documents\\Backup";

		// Get the current date and time formatted as "yyyy-MM-dd_HH-mm-ss"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		String timestamp = LocalDateTime.now().format(formatter);
		String outputFile = backupPath + "\\" + dbName + "_" + timestamp + ".sql";

		// Ensure the backup directory exists
		File backupDir = new File(backupPath);
		if (!backupDir.exists() && !backupDir.mkdirs()) {
			log.error("Failed to create backup directory. Backup aborted.");
			return;
		}

		// Construct and execute the command without a password
		ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ".\\mysqldump", "-u", "root", dbName);
		processBuilder.directory(new File("C:\\xampp\\mysql\\bin")); // Set the MySQL bin directory
		processBuilder.redirectOutput(new File(outputFile)); // Redirect output to the backup file

		try {
			Process process = processBuilder.start();
			int processComplete = process.waitFor(); // Wait for the process to complete

			// Check the exit value of the process
			if (processComplete == 0) {
				log.info("Backup process completed successfully.");
			} else {
				log.error("Backup process failed with exit code: {}", processComplete);
			}
		} catch (IOException | InterruptedException e) {
			log.error("An error occurred during the backup process", e);
			Thread.currentThread().interrupt(); // Proper handling of InterruptedException
		}
	}
}
