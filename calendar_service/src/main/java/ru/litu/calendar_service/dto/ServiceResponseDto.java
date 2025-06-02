package ru.litu.calendar_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDto<T> {
    private String status;
    private T data;
    private List<Integer> isCompleteList;
}
