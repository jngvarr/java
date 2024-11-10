package org.example;

import com.sun.mail.util.BASE64DecoderStream;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class EmailAttachmentSaver2 {

    public static void main(String[] args) {
        LocalDate localDateToday = LocalDate.now();
        String today = localDateToday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String host = "imap.mail.ru";
        String user = "jngvarr@inbox.ru";
        String password = "pkfbmuMnfRwRF0dVetZn";
        String saveDirectoryPath = "d:\\Downloads\\профили2\\reports\\";

        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.connectiontimeout", "5000");
        properties.put("mail.imap.timeout", "5000");

        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(user, password);

            Folder inbox = store.getFolder("Askye reports");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;

                try {
                    // Пропускаем сообщение, если его содержимое не является Multipart
                    Object content = message.getContent();
                    if (!(content instanceof Multipart)) {
                        System.out.println("Сообщение не содержит вложений.");
                        continue; // Переход к следующему сообщению
                    }

                    String saveDirectory = saveDirectoryPath + new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate());
                    Multipart multipart = (Multipart) content;
                    processMultipartContent(multipart, saveDirectory);

                } catch (MessagingException e) {
                    System.err.println("Ошибка при загрузке сообщения: " + e.getMessage());
                }
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isFromAllowedSender(Message message, List<String> allowedSenders) throws MessagingException {
        Address[] fromAddresses = message.getFrom();
        for (Address address : fromAddresses) {
            String emailAddress = ((InternetAddress) address).getAddress();
            if (allowedSenders.contains(emailAddress)) {
                return true;
            }
        }
        return false;
    }

    private static void processMultipartContent(Multipart multipart, String saveDirectory) throws MessagingException, IOException {
        boolean hasAttachments = false; // Флаг для отслеживания наличия вложений

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                hasAttachments = true;
                String decodedFileName = MimeUtility.decodeText(bodyPart.getFileName());
                decodedFileName = decodedFileName.replaceAll("[\\\\/:*?\"<>|]", "_");

                if (bodyPart.getContent() instanceof BASE64DecoderStream) {
                    saveStreamAttachment((InputStream) bodyPart.getContent(), saveDirectory, decodedFileName);
                } else {
                    saveAttachment(bodyPart, saveDirectory, decodedFileName);
                }
            }
        }

        // Выводим сообщение, если вложений нет
        if (!hasAttachments) {
            System.out.println("В сообщении отсутствуют вложения.");
        }
    }

    private static void saveStreamAttachment(InputStream inputStream, String saveDirectory, String fileName) throws IOException {
        File directory = new File(saveDirectory);
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("Вложение сохранено: " + file.getAbsolutePath());
    }

    private static void saveAttachment(BodyPart bodyPart, String saveDirectory, String fileName) throws IOException, MessagingException {
        File directory = new File(saveDirectory);
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory + File.separator + fileName);
        try (InputStream is = bodyPart.getInputStream(); FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("Вложение сохранено: " + file.getAbsolutePath());
    }
}
