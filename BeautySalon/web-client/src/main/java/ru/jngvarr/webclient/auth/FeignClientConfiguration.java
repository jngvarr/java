//package ru.jngvarr.webclient.auth;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FeignClientConfiguration {
//    @Value("${your.token}") // Подставьте имя свойства, в котором хранится ваш токен
//    private String token;
//
//    @Bean
//    public TokenInterceptor tokenInterceptor() {
//        return new TokenInterceptor(token);
//    }
//}
