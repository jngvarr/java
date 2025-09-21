package jngvarr.ru.pto_ackye_rzhd.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public record UpdateEvent(Update update) {}

/*
Что такое record в Java
record — это специальный вид классов (появился в Java 16), предназначенный для хранения иммутабельных данных (data carrier).
То есть это «короткая запись» неизменяемого объекта с автоматическим созданием:
- private final полей,
- конструктора,
- equals/hashCode,
- toString,
- геттеров (методы с именами полей).

Особенности
Иммутабельность: все поля автоматически private final. После создания объект менять нельзя.
Лаконичность: убирает «шум» кода — геттеры, equals/hashCode, конструктор — всё сгенерировано автоматически.
Подходит для DTO, value-object'ов, где нужны только данные без логики.
*/