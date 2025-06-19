package kosa.server.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.service.sender.host}")
    private String serviceHost;
    @Value("${spring.mail.service.sender.port}")
    private int servicePort;
    @Value("${spring.mail.service.sender.username}")
    private String serviceUsername;
    @Value("${spring.mail.service.sender.password}")
    private String servicePassword;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean startTlsEnable;
    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private boolean sslEnable;

    @Bean(name = "notificationMailSender")
    public JavaMailSenderImpl notificationMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(serviceHost);
        sender.setPort(servicePort);
        sender.setUsername(serviceUsername);
        sender.setPassword(servicePassword);

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", startTlsEnable);
        props.put("mail.smtp.ssl.enable", sslEnable);

        sender.setProtocol("smtp");

        return sender;
    }
}
