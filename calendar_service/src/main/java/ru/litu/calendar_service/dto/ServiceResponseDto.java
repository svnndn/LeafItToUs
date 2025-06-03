package ru.litu.calendar_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDto<T> {
    @NotEmpty(message = "Status cannot be empty")
    private String status;

    @NotNull(message = "Data cannot be null")
    private T data;

    private List<Integer> isCompleteList;
}