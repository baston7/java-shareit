package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    String description;
    @NotNull(message = "Статус вещи не можеть быть null")
    Boolean available;
    Long requestId;
}
