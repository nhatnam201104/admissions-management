package com.example.managementadmissionwf;

import com.example.managementadmissionwf.ui.MainFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main Application - Spring Boot + Swing Integration
 * 
 * This is the entry point for the application. It demonstrates how to integrate
 * Spring Boot with Java Swing desktop applications.
 * 
 * Integration Flow:
 * 1. Start Spring Boot context
 * 2. Get MainFrame bean from Spring context
 * 3. Initialize and display the Swing UI on the Event Dispatch Thread (EDT)
 * 
 * The Swing UI runs as a Spring-managed bean, allowing it to use dependency
 * injection to access business layer services.
 */
@SpringBootApplication
public class ManagementAdmissionWfApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context =  new SpringApplicationBuilder(ManagementAdmissionWfApplication.class).headless(false).run(args);
		
		MainFrame mainFrame = context.getBean(MainFrame.class);
		
//		Initialize Swing UI on the Event Dispatch Thread (EDT)
		javax.swing.SwingUtilities.invokeLater(() -> {
			// Set system look and feel for native appearance
			try {
				javax.swing.UIManager.setLookAndFeel(
					javax.swing.UIManager.getSystemLookAndFeelClassName()
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Initialize and show the main frame
			mainFrame.initialize();
		});
	}

}
