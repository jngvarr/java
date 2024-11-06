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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class EmailAttachmentSaver {

    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
        LocalDate localDateToday = LocalDate.now();
        String today = localDateToday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String host = "imap.mail.ru"; // Укажите сервер IMAP
        String user = "jngvarr@inbox.ru"; // Укажите email
        String password = "pkfbmuMnfRwRF0dVetZn"; // Укажите пароль
        String saveDirectoryPath = "d:\\загрузки\\PTO\\reports\\"; // Путь для сохранения вложений

        // Список допустимых адресов отправителей
        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        try {
            // Подключаемся к почтовому ящику
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(user, password);

            // Открываем папку "Входящие"
            Folder inbox = store.getFolder("Ackye reports");
            inbox.open(Folder.READ_ONLY);

            // Получаем сообщения
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;
                Object content = message.getContent();
                String saveDirectory = saveDirectoryPath + new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate());

                File directory = new File(saveDirectory);
                if (!directory.exists()) directory.mkdirs();

                if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    processMultipartContent(multipart, saveDirectory);


                } else if (content instanceof BASE64DecoderStream) {
                    saveStreamAttachment((InputStream) content, saveDirectory, "attachment.bin");
                } else {
                    System.out.println("Тип содержимого не поддерживается: " + content.getClass().getName());
                }
            }

            // Закрываем соединение
            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processMultipartContent(Multipart multipart, String saveDirectory) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                String decodedFileName = MimeUtility.decodeText(bodyPart.getFileName());
                decodedFileName = decodedFileName.replaceAll("[\\\\/:*?\"<>|]", "_"); // Замена запрещённых символов
                if (bodyPart.getContent() instanceof BASE64DecoderStream) {
                    saveStreamAttachment((InputStream) bodyPart.getContent(), saveDirectory, decodedFileName);
                } else {
                    saveAttachment(bodyPart, saveDirectory, decodedFileName);
                }
            }
        }
    }

    private static void saveStreamAttachment(InputStream inputStream, String saveDirectory, String fileName) throws
            IOException {
        File file = new File(saveDirectory + File.separator + fileName);
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
        File file = new File(saveDirectory + File.separator + fileName);

        try (InputStream is = bodyPart.getInputStream(); FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("Вложение сохранено: " + file.getAbsolutePath());
    }
//    private static void saveAttachment(BodyPart bodyPart, String saveDirectory) throws
//            IOException, MessagingException {
//        String fileName = bodyPart.getFileName();
//        File file = new File(saveDirectory + File.separator + fileName);
//
//        try (InputStream is = bodyPart.getInputStream(); FileOutputStream fos = new FileOutputStream(file)) {
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, bytesRead);
//            }
//        }
//        System.out.println("Вложение сохранено: " + file.getAbsolutePath());
//    }
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
}
