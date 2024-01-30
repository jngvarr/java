package com.example.demo.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Компонент, содержащий настройки для работы с базой данных.
 */
@Component
@ConfigurationProperties(prefix = "magic.data")
@Data
public class MagicData {
    private String findAllQuery;
    private String saveQuery;
    private String deleteByIdQuery;
    private String updateUserQuery;
    private String getOneQuery;
}