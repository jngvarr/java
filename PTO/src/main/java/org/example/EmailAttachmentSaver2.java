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
//        String host = "imap.mail.ru";
        String user = "jngvarr.jd@yandex.ru";
//        String password = "pkfbmuMnfRwRF0dVetZn";
        String password = "cfbfhdlzejiaiuil";
//        String saveDirectoryPath = "d:\\Downloads\\профили2\\reports\\" + today;
        String saveDirectoryPath = "d:\\загрузки\\PTO\\reports\\" + today;

//        List<String> allowedSenders = List.of("jngvarr.jd@yandex.ru", "jngvarr@jngvarr.ru");
        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru", "jngvarr.jd@yandex.ru", "jngvarr@jngvarr.ru");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.partialfetch", "false"); // Отключаем частичную загрузку
        properties.put("mail.imap.connectiontimeout", "5000");
        properties.put("mail.imap.timeout", "5000");
        properties.put("mail.imap.writetimeout", "5000");
        properties.put("mail.debug", "false"); // Отключаем отладку, чтобы избежать лишнего вывода
        properties.put("mail.imap.ignorebodystructuresize", "true");
        properties.put("mail.imap.cachestructure", "false");


        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(user, password);

            Folder inbox = store.getFolder("Ackye reports");
            inbox.open(Folder.READ_ONLY);

            // Устанавливаем дату для фильтрации
//            Date targetDate = Date.from(localDateToday.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//            // Создаём SearchTerm для сообщений, отправленных в указанную дату
//            SearchTerm dateTerm = new SentDateTerm(javax.mail.search.ComparisonTerm.EQ, targetDate);
//
//            // Ищем сообщения по дате
//            Message[] messages = inbox.search(dateTerm);

            int messagesQuantity = inbox.getMessageCount();
//            Message[] messages = inbox.getMessages(messagesQuantity - 10, messagesQuantity);
            Message[] messages = inbox.getMessages();
            int attachmentCount = 1;

            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;
                if (!(new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate()).equals("11.11.2024"))) continue;
//                if (!(new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate()).equals(today))) continue;

                try {
                    // Создаём новое MimeMessage для загрузки локально
                    MimeMessage mimeMessage = new MimeMessage((MimeMessage) message);

                    Object content = mimeMessage.getContent();

                    if (content instanceof Multipart multipart) {
                        // Обработка содержимого Multipart
                        processMultipartContent(multipart, saveDirectoryPath, attachmentCount);
                    } else {
                        // Обработка одиночного вложения
                        processSinglePartContent(message, saveDirectoryPath, attachmentCount);
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
            // Получаем первый лист
            Sheet sheet = workbook.getSheetAt(0);
            // Получаем первую строку и первую ячейку
            Row row = sheet.getRow(2);
            Cell cell = row.getCell(0);
            // Читаем значение ячейки
            if (cell != null) {
                cellValue = cell.getStringCellValue();
                System.out.println("Значение ячейки A3: " + cellValue);
            } else {
                System.out.println("Ячейка пуста.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении Excel файла: " + e.getMessage());
        }
        return cellValue;
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

    private static void processMultipartContent(Multipart multipart, String saveDirectory, int attachmentCount) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType().toLowerCase();

            // Проверяем, является ли часть вложением и Excel-файлом
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                    (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                            contentType.contains("application/vnd.ms-excel"))) {

                String fileName = MimeUtility.decodeText(bodyPart.getFileName());

                try (InputStream inputStream = bodyPart.getInputStream()) {
                    if (fileName.contains("report")) fileName = setFileName(inputStream);

                    saveStreamAttachment(inputStream, saveDirectory, fileName);
                    System.out.println("Вложение сохранено (PMPC): " + fileName);
                }
                attachmentCount++;
            }
        }
    }

    private static void processSinglePartContent(Message message, String saveDirectory, int attachmentCount) throws MessagingException, IOException {
        String contentType = message.getContentType().toLowerCase();

        // Проверяем, является ли содержимое одиночным вложением Excel
        if (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.contains("application/vnd.ms-excel")) {

            String uniqueFileName = setFileName("report.xlsx", attachmentCount);
            try (InputStream inputStream = message.getInputStream()) {
                saveStreamAttachment(inputStream, saveDirectory, uniqueFileName);
                System.out.println("Вложение сохранено (PSPC): " + uniqueFileName);
            }
        }
    }
}
