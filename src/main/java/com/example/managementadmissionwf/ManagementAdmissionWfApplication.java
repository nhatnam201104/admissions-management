package com.example.managementadmissionwf;

import com.example.managementadmissionwf.exception.GlobalException;
import com.example.managementadmissionwf.ui.frame.LoginFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ManagementAdmissionWfApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(ManagementAdmissionWfApplication.class, args);
        LoginFrame loginFrame = new LoginFrame();
        Thread.setDefaultUncaughtExceptionHandler(
                new GlobalException()
        );

        javax.swing.SwingUtilities.invokeLater(loginFrame::display);
    }

}
