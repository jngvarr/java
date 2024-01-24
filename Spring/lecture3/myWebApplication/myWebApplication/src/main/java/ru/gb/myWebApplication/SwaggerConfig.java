//package ru.gb.myWebApplication;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///*
//Swagger
//Swagger — это инструмент для автоматической генерации документации для RESTful
//API. Он позволяет вам описать структуру вашего API, а затем автоматически
//создает красивую, интерактивную документацию, которую можно использовать для
//проверки работы вашего API. Это очень удобно для разработчиков, тестировщиков
//и конечных пользователей вашего API.
//чтобы увидеть нашу документацию и проверить работу API открываем веб-браузер
//и переходим по адресу:
// http://localhost:8080/swagger-ui.html
//*/
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//}
