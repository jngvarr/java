package ru.gb.sem12_example1.service;

import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.handler.annotation.Header;


@MessagingGateway(defaultRequestChannel = "textInputChanel")
public interface FileGateway {
    public void writeToFile(@Header(FileHeaders.FILENAME) String fileName, String data);
}
