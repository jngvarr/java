package org.example;

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
        String saveDirectoryPath = "d:\\Downloads\\профили2\\reports\\";

        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.partialfetch", "false"); // Полный fetch

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

            String fileName = bodyPart.getFileName();
            String contentType = bodyPart.getContentType().toLowerCase();

            if (fileName != null) {
                fileName = MimeUtility.decodeText(fileName);
            } else if (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    contentType.contains("application/vnd.ms-excel")) {
                fileName = "excel_attachment.xlsx"; // Имя по умолчанию для Excel-файлов
            } else {
                System.out.println("Часть сообщения не имеет имени файла и не является Excel-файлом.");
                continue;
            }

            if (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls")) {
                hasExcelAttachments = true;
                fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");

                if (bodyPart.getContent() instanceof InputStream) {
                    saveStreamAttachment((InputStream) bodyPart.getContent(), saveDirectory, fileName);
                } else {
                    saveAttachment(bodyPart, saveDirectory, fileName);
                }
            } else {
                System.out.println("Пропущено вложение: " + fileName + " (не является файлом Excel)");
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
