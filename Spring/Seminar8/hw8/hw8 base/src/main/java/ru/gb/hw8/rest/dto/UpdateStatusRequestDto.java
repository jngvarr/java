package ru.gb.hw8.rest.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import ru.gb.hw8.model.TaskStatus;

/**
 * DTO (Data Transfer Object) для запроса обновления статуса задачи.
 */

@Data
public class UpdateStatusRequestDto {
    @NotNull(message = "ID value is required")
    private Long id;

    @NotNull(message = "status value is required")
    private TaskStatus status;
}

