package org.example;

import javax.mail.*;
import javax.mail.internet.*;
import java.time.LocalDate;
import java.util.Properties;
import java.io.File;

public class EmailSenderMultipleRecipients {
    public static void main(String[] args) {

        final String senderEmail = "ooouksts@ooouksts.ru";
        final String senderPassword = "gixxffqgixhyxbel"; //
//        final String senderPassword = "cfbfhdlzejiaiuil"; // jngvarr.jd@yandex.ru
        final String smtpHost = "smtp.yandex.ru"; // Или другой SMTP-сервер
        final int smtpPort = 465; // 465 для SSL, 587 для TLS
        final String TODAY = String.valueOf(LocalDate.now());

        // Список получателей
        final String toRecipients =
//                "stspopov@mail.ru," +
//                        "bayaskin@mail.ru," +
//                        "sergofan20051@yandex.ru," +
//                        "vov_keks@mail.ru," +
//                        "gritsyna.vg@yandex.ru," +
//                        "ostrekalov@yandex.ru," +
//                        "40xx@inbox.ru," +
//                        "vv@brusenin.ru," +
                        "jngvarr@inbox.ru";
//        final String ccRecipients = "cc@example.com";
//        final String bccRecipients = "bcc@example.com";

        final String subject = "Контроль ПУ РРЭ (Задания на ОТО РРЭ) за " + TODAY;
        final String bodyText = "Добрый день! Во вложении отчет за " + TODAY;

        final String filePath = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx"; // Файл для вложения

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Создаем письмо
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));

            // Добавляем нескольких получателей в TO, CC, BCC
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toRecipients));
//            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccRecipients));
//            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccRecipients));

            message.setSubject(subject);

            // Текст письма
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText);

            // Вложение
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(filePath));

            // Объединяем части письма
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            // Устанавливаем контент письма
            message.setContent(multipart);

            // Отправляем письмо
            Transport.send(message);
            System.out.println("Письмо успешно отправлено!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при отправке письма: " + e.getMessage());
        }
    }
}
