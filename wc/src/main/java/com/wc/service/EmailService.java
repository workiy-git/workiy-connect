package com.wc.service;

import org.springframework.context.annotation.Import;
//import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ClassPathResource;
//import org.springframework.util.FileCopyUtils;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.wc.configuration.EmailThymeleafConfig;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Import(EmailThymeleafConfig.class)
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final Map<String, String> emailTemplates;

    public static final String REGISTRATION_REQUEST_TEMPLATE = "registrationRequestEmail";
    public static final String EMPLOYEE_ACTIVE_TEMPLATE = "employeeActiveEmail"; // Add more as needed
    public static final String EMPLOYEE_BLOCKED_TEMPLATE = "employeeBlockedEmail";
    public static final String EMPLOYEE_REMOVED_TEMPLATE = "employeeRemovedEmail";
    public static final String EMPLOYEE_RESET_PASSWORD = "employeeResetPasswordEmail";

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;

        // Initialize email templates
        this.emailTemplates = new HashMap<>();
        emailTemplates.put(REGISTRATION_REQUEST_TEMPLATE, "<html xmlns:th=\"http://www.thymeleaf.org\">\r\n"
        		+ "<head>\r\n"
        		+ "    <meta charset=\"UTF-8\"/>\r\n"
        		+ "    <title>Employee Registration Request</title>\r\n"
        		+ "</head>\r\n"
        		+ "<body>\r\n"
        		+ "    <h1 th:text=\"${title}\">Workiy Connect</h1>\r\n"
        		+ "    <p th:text=\"${content}\">Your registration is in process. You will receive a confirmation mail once request is approved. If your have any queries, kindly reach out to HR at hr@workiy.ca</p>\r\n"
        		+ "</body>\r\n"
        		+ "</html>");
        emailTemplates.put(EMPLOYEE_ACTIVE_TEMPLATE, "<html xmlns:th=\"http://www.thymeleaf.org\">\r\n"
        		+ "<head>\r\n"
        		+ "    <meta charset=\"UTF-8\"/>\r\n"
        		+ "    <title>Employee Active</title>\r\n"
        		+ "</head>\r\n"
        		+ "<body>\r\n"
        		+ "    <h1 th:text=\"${title}\">Workiy Connect</h1>\r\n"
        		+ "    <p th:text=\"${content}\">Your account is now active. Kindly login with your credentials. If your have any queries, kindly reach out to HR at hr@workiy.ca</p>\r\n"
        		+ "</body>\r\n"
        		+ "</html>");
        emailTemplates.put(EMPLOYEE_BLOCKED_TEMPLATE, "<html xmlns:th=\"http://www.thymeleaf.org\">\r\n"
        		+ "<head>\r\n"
        		+ "    <meta charset=\"UTF-8\"/>\r\n"
        		+ "    <title>Employee Blocked</title>\r\n"
        		+ "</head>\r\n"
        		+ "<body>\r\n"
        		+ "    <h1 th:text=\"${title}\">Workiy Connect</h1>\r\n"
        		+ "    <p th:text=\"${content}\">Your account is now blocked. If your have any queries, kindly reach out to HR at hr@workiy.ca</p>\r\n"
        		+ "</body>\r\n"
        		+ "</html>");
        emailTemplates.put(EMPLOYEE_REMOVED_TEMPLATE, "<html xmlns:th=\"http://www.thymeleaf.org\">\r\n"
        		+ "<head>\r\n"
        		+ "    <meta charset=\"UTF-8\"/>\r\n"
        		+ "    <title>Employee Removed</title>\r\n"
        		+ "</head>\r\n"
        		+ "<body>\r\n"
        		+ "    <h1 th:text=\"${title}\">Workiy Connect</h1>\r\n"
        		+ "    <p th:text=\"${content}\">Your account is removed. If your have any queries, kindly reach out to HR at hr@workiy.ca</p>\r\n"
        		+ "</body>\r\n"
        		+ "</html>");
        emailTemplates.put(EMPLOYEE_RESET_PASSWORD, "<html xmlns:th=\"http://www.thymeleaf.org\">\r\n"
        		+ "<head>\r\n"
        		+ "    <meta charset=\"UTF-8\"/>\r\n"
        		+ "    <title>Employee Reset Password</title>\r\n"
        		+ "</head>\r\n"
        		+ "<body>\r\n"
        		+ "    <h1 th:text=\"${title}\">Workiy Connect</h1>\r\n"
        		+ "    <p th:text=\"${content}\">Click the link below to reset your password:</p>\r\n"
        		+ "    <p><a th:href=\"'http://your-app-url/password-reset/reset?token=' + ${resetToken}\">Reset Password</a></p>\r\n"
        		+ "</body>\r\n"
        		+ "</html>\r\n"
        		+ "");

        // Add more templates and their content
    }

    public void sendEmail(String to, String subject, String templateName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Set other properties like subject, from, etc.
            helper.setTo(to);
            helper.setSubject(subject);

            // Get template content
            String emailContent = emailTemplates.get(templateName);

            // Set the email content
            helper.setText(emailContent, true);

            // Send the email
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }
    
    public void sendPasswordResetEmail(String to, String subject, String emailContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Set other properties like subject, from, etc.
            helper.setTo(to);
            helper.setSubject(subject);

            // Get template content
//            emailContent = "Click the link below to reset your password: <a href='" + resetLink + "'>Reset Password</a>";

            // Set the email content
            helper.setText(emailContent, true);

            // Send the email
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }
    
//    public void sendPasswordResetEmail(String to, String subject, String resetLink) {
//        try {
//            // Construct the email content
//            String emailContent = "Click the link below to reset your password: <a href='" + resetLink + "'>Reset Password</a>";
//
//            // Send the email
//            sendEmail(to, subject, emailContent);
//        } catch (Exception e) {
//            // Handle exception
//            e.printStackTrace();
//        }
//    }

    
    public void sendEmailUsingHtmlTemplate(String to, String subject, String templateName,
            String titleKey, String titleValue,
            String contentKey, String contentValue,
            String resetLinkKey, String resetLinkValue) {

        try {
            Context context = new Context();
            context.setVariable(titleKey, titleValue);
            context.setVariable(contentKey, contentValue);
            context.setVariable(resetLinkKey, resetLinkValue);

            // Load HTML template from /templates/email directory
            String templatePath = "email/" + templateName + ".html";
            String emailContent = loadHtmlTemplate(templatePath, context);

            sendEmail(to, subject, emailContent);
        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
        }
    }
    
    private String loadHtmlTemplate(String templatePath, Context context) {
        try {
            // Load HTML template from the classpath
            ClassPathResource resource = new ClassPathResource(templatePath);
            InputStream inputStream = resource.getInputStream();

            // Read the content of the HTML template using a BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder templateContentBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                templateContentBuilder.append(line).append("\n");
            }

            // Close the BufferedReader
            reader.close();

            // Process the Thymeleaf template with the provided context
            return templateEngine.process(templateContentBuilder.toString(), context);
        } catch (IOException e) {
            // Handle the exception or rethrow it as needed
            e.printStackTrace(); // Print the exception for demonstration purposes
            return ""; // Return an empty string or handle the error accordingly
        }
    }
}
