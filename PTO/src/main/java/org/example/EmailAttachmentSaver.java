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
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.io.ByteArrayOutputStream;

public class EmailAttachmentSaver { // загрузка почты SMTP

    public static void main(String[] args) {
        LocalDate localDateToday = LocalDate.now();
        String today = localDateToday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String host = "imap.yandex.ru";
        String user = "jngvarr.jd@yandex.ru";
        String password = "cfbfhdlzejiaiuil";
        String saveDirectoryPath = "d:\\загрузки\\PTO\\reports\\" + today;

        List<String> allowedSenders = List.of("askue-rzd@gvc.rzd.ru", "jngvarr.jd@yandex.ru", "jngvarr@jngvarr.ru");

        Properties properties = getProperties(host);

        // Устанавливаем дату для фильтрации
        Date targetDate = Date.from(localDateToday.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Создаём фильтр по дате
        SearchTerm dateFilter = new SentDateTerm(ComparisonTerm.EQ, targetDate);


        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(user, password);

            Folder inbox = store.getFolder("Ackye reports");
            inbox.open(Folder.READ_ONLY);


            Message[] messages = inbox.search(dateFilter); // Получаем только сообщения, соответствующие дате
            for (Message message : messages) {
                if (!isFromAllowedSender(message, allowedSenders)) continue;
                if (!new SimpleDateFormat("dd.MM.yyyy").format(message.getSentDate()).equals(today)) continue;

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

    private static Properties getProperties(String host) {
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
        return properties;
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

    private static void saveStreamAttachment(InputStream inputStream, String saveDirectory, String fileName) throws IOException {
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Сохраняем данные потока во временный буфер в памяти
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;
        int totalBytes = 0;

        while ((bytesRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
            totalBytes += bytesRead;
        }

        if (totalBytes == 0) {
            System.out.println("Предупреждение: Вложение пустое или не удалось прочитать данные.");
            return; // Прекращаем, если данные пусты
        }

        // Проверяем, содержит ли исходное имя файла "report"
        String finalFileName = fileName.contains("report")
                ? setFileNameFromBuffer(buffer.toByteArray(), fileName)
                : fileName;

        // Сохраняем файл в нужное место
        try (FileOutputStream fos = new FileOutputStream(new File(directory, finalFileName))) {
            buffer.writeTo(fos);
        }

        System.out.println("Вложение сохранено: " + directory + File.separator + finalFileName);
    }

    // Метод для извлечения имени из ячейки A3, используя данные в памяти
    private static String setFileNameFromBuffer(byte[] fileData, String originalFileName) {
        String cellValue = null;
        try (InputStream inputStream = new ByteArrayInputStream(fileData);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(2);
            Cell cell = row.getCell(0);

            if (cell != null) {
                cellValue = cell.getStringCellValue().trim().replace("\\", "-").replace("/", "-");
                System.out.println("Значение ячейки A3: " + cellValue);
            } else {
                System.out.println("Ячейка пуста.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении Excel файла: " + e.getMessage());
        }

        return (cellValue != null ? cellValue : originalFileName) + "_" + LocalDate.now() + ".xlsx";
    }

    private static void processMultipartContent(Multipart multipart, String saveDirectory) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType().toLowerCase();

            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                    (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                            contentType.contains("application/vnd.ms-excel"))) {

                String fileName = MimeUtility.decodeText(bodyPart.getFileName());

                // Считываем поток в массив байтов
                byte[] fileData = inputStreamToByteArray(bodyPart.getInputStream());
                saveStreamAttachment(new ByteArrayInputStream(fileData), saveDirectory, fileName);
                System.out.println("Вложение сохранено (PMPC) ");
            }
        }
    }

    private static void processSinglePartContent(Message message, String saveDirectory) throws MessagingException, IOException {
        String contentType = message.getContentType().toLowerCase();

        if (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.contains("application/vnd.ms-excel")) {
            String fileName = "report.xlsx";

            // Считываем поток в массив байтов
            byte[] fileData = inputStreamToByteArray(message.getInputStream());
            saveStreamAttachment(new ByteArrayInputStream(fileData), saveDirectory, fileName);
            System.out.println("Вложение сохранено (PSPC)");
        }
    }

    // Универсальный метод для конвертации InputStream в массив байтов
    private static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }
}
