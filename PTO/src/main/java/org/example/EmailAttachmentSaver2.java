package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

        String host = "imap.yandex.ru";
        String user = "jngvarr.jd@yandex.ru";
        String password = "cfbfhdlzejiaiuil";
        String saveDirectoryPath = "d:\\Downloads\\профили2\\reports\\" + today;

        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru", "jngvarr.jd@yandex.ru", "jngvarr@jngvarr.ru");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.partialfetch", "false");
        properties.put("mail.imap.connectiontimeout", "5000");
        properties.put("mail.imap.timeout", "5000");
        properties.put("mail.imap.writetimeout", "5000");
        properties.put("mail.debug", "false");

        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(user, password);

            Folder inbox = store.getFolder("Ackye reports");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;
                if (!new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate()).equals("11.11.2024")) continue;

                try {
                    MimeMessage mimeMessage = new MimeMessage((MimeMessage) message);
                    Object content = mimeMessage.getContent();

                    if (content instanceof Multipart multipart) {
                        processMultipartContent(multipart, saveDirectoryPath);
                    } else {
                        processSinglePartContent(mimeMessage, saveDirectoryPath);
                    }
                } catch (IOException | MessagingException e) {
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

    private static String setFileName(InputStream inputStream) {
        String cellValue = null;
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(2);
            Cell cell = row.getCell(0);
            if (cell != null) {
                cellValue = cell.getStringCellValue().trim().replaceAll("[\\\\/:*?\"<>|]", "_");
                System.out.println("Значение ячейки A3: " + cellValue);
            } else {
                System.out.println("Ячейка пуста.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении Excel файла: " + e.getMessage());
        }
        return (cellValue != null ? cellValue : "default") + "_" + LocalDate.now() + ".xlsx";
    }

    private static void saveStreamAttachment(InputStream inputStream, String saveDirectory, String fileName) throws IOException {
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            int totalBytes = 0; // Для отслеживания общего количества байтов

            // Читаем данные и записываем в файл
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead; // Считаем, сколько байтов было прочитано
            }

            if (totalBytes == 0) {
                System.out.println("Предупреждение: Вложение пустое или не удалось прочитать данные.");
            } else {
                System.out.println("Вложение сохранено: " + file.getAbsolutePath() + " (Размер: " + totalBytes + " байт)");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении вложения: " + e.getMessage());
        }
    }


    private static void processMultipartContent(Multipart multipart, String saveDirectory) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType().toLowerCase();

            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                    (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                            contentType.contains("application/vnd.ms-excel"))) {

                String fileName = MimeUtility.decodeText(bodyPart.getFileName());

                try (InputStream inputStream = bodyPart.getInputStream()) {
                    if (fileName.contains("report")) {
                        fileName = setFileName(inputStream);
                    }
                    saveStreamAttachment(inputStream, saveDirectory, fileName);
                    System.out.println("Вложение сохранено (PMPC): " + fileName);
                }
            }
        }
    }

    private static void processSinglePartContent(Message message, String saveDirectory) throws MessagingException, IOException {
        String contentType = message.getContentType().toLowerCase();

        if (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.contains("application/vnd.ms-excel")) {
            String fileName = "report.xlsx";  // Установка имени по умолчанию для одиночного вложения

            try (InputStream inputStream = message.getInputStream()) {
                fileName = setFileName(inputStream);
                saveStreamAttachment(inputStream, saveDirectory, fileName);
                System.out.println("Вложение сохранено (PSPC): " + fileName);
            }
        }
    }
}
