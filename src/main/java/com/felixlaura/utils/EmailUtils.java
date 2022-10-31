package com.felixlaura.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String body){
        boolean isMailSent = false;
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

//            FileSystemResource file = new FileSystemResource(new File(fileToAttach));
//            helper.addAttachment("Pdf Report", file);

            mailSender.send(mimeMessage);
            isMailSent = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return isMailSent;
    }

}
