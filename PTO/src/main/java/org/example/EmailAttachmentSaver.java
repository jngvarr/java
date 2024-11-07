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

public class EmailAttachmentSaver {

    public static void main(String[] args) {
        LocalDate localDateToday = LocalDate.now();
        String today = localDateToday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String host = "imap.mail.ru";
        String user = "jngvarr@inbox.ru";
        String password = "pkfbmuMnfRwRF0dVetZn";
        String saveDirectoryPath = "d:\\загрузки\\PTO\\reports\\";

        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.connectiontimeout", "10000");
        properties.put("mail.imap.timeout", "10000");

        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(user, password);

            Folder inbox = store.getFolder("Ackye reports");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;
                if (!(new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate()).equals(today))) continue;

                try {
                    Object content = message.getContent();
                    if (!(content instanceof Multipart)) {
                        System.out.println("Сообщение не содержит вложений.");
                        continue;
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

        } catch (FolderClosedException e) {
            System.err.println("Папка была закрыта из-за тайм-аута. Попробуйте снова или увеличьте таймаут.");
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
        boolean hasExcelAttachments = false;

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            // Попытка получить имя файла
            String fileName = bodyPart.getFileName();
            if (fileName != null) {
                String decodedFileName = MimeUtility.decodeText(fileName);

                // Проверяем только расширение файла
                if (decodedFileName.toLowerCase().endsWith(".xlsx") || decodedFileName.toLowerCase().endsWith(".xls")) {
                    hasExcelAttachments = true;
                    decodedFileName = decodedFileName.replaceAll("[\\\\/:*?\"<>|]", "_");

                    if (bodyPart.getContent() instanceof BASE64DecoderStream) {
                        saveStreamAttachment((InputStream) bodyPart.getContent(), saveDirectory, decodedFileName);
                    } else {
                        saveAttachment(bodyPart, saveDirectory, decodedFileName);
                    }
                } else {
                    System.out.println("Пропущено вложение: " + decodedFileName + " (не является файлом Excel)");
                }
            } else {
                System.out.println("Часть сообщения не имеет имени файла и была пропущена.");
            }
        }

        if (!hasExcelAttachments) {
            System.out.println("В сообщении отсутствуют файлы Excel.");
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
