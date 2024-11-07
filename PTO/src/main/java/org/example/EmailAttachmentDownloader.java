package org.example;

import com.sun.mail.util.BASE64DecoderStream;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
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

public class EmailAttachmentDownloader {

    public static void main(String[] args) {
        // Настройка параметров подключения к почтовому серверу
        String host = "imap.mail.ru";
        String username = "jngvarr@inbox.ru";
        String password = "pkfbmuMnfRwRF0dVetZn";
        String saveDirectoryPath = "d:\\загрузки\\PTO\\reports\\";

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru");

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        try {
            // Подключение к почтовому ящику
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore();
            store.connect(host, username, password);

            // Открытие папки "Входящие"
//            Folder inbox = store.getFolder("Ackye reports");
            Folder inbox = store.getFolder("Ackye reports");
            inbox.open(Folder.READ_ONLY);

            // Извлечение сообщений из папки
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;

                String messageDate = new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate());
                if (!messageDate.equals(today)) {
                    continue;
                }
                try {
                    Object content = message.getContent();
                    if (!(content instanceof Multipart)) {
                        System.out.println("Сообщение не содержит вложений.");
                        continue;
                    }

                    Multipart multipart = (Multipart) content;
                    // Обработка каждого вложения
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);

                        String disposition = bodyPart.getDisposition();
                        String fileName = bodyPart.getFileName();

                        // Проверка, является ли часть вложением
                        if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || fileName != null) {
                            // Обработка вложения, например, сохранение файла
                            System.out.println("Найдено вложение: " + fileName);
                        }
//                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
//                                bodyPart.getFileName().endsWith(".xlsx")) {
//
//                            // Скачивание вложенного файла
                            saveAttachment(bodyPart, saveDirectoryPath);
//                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для сохранения вложения на диск
    private static void saveAttachment(BodyPart bodyPart, String saveDirectoryPath) throws Exception {
        InputStream inputStream = bodyPart.getInputStream();
        File file = new File( saveDirectoryPath+ bodyPart.getFileName());
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();

        System.out.println("Вложение сохранено: " + file.getAbsolutePath());
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
//    private static void processMultipartContent(Multipart multipart, String saveDirectory) throws MessagingException, IOException {
//        boolean hasExcelAttachments = false;
//
//        for (int i = 0; i < multipart.getCount(); i++) {
//            BodyPart bodyPart = multipart.getBodyPart(i);
//            // Проверяем, что у части есть имя файла
//            String fileName = bodyPart.getFileName();
//            if (fileName != null) {
//                String decodedFileName = MimeUtility.decodeText(fileName);
//                if (!decodedFileName.isEmpty()) {
//                    hasExcelAttachments = true;
////                    decodedFileName = decodedFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
//
//                    if (bodyPart.getContent() instanceof BASE64DecoderStream) {
//                        saveStreamAttachment((InputStream) bodyPart.getContent(), saveDirectory, decodedFileName);
//                    } else {
//                        saveAttachment(bodyPart, saveDirectory, decodedFileName);
//                    }
//                } else {
//                    System.out.println("Пропущено вложение: " + decodedFileName + " (не является файлом Excel)");
//                }
//            } else {
//                System.out.println("Часть сообщения не имеет имени файла и была пропущена.");
//            }
//        }
//
//        if (!hasExcelAttachments) {
//            System.out.println("В сообщении отсутствуют файлы Excel.");
//        }
//    }
}
