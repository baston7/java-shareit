package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    long id;
    @NotBlank(message = "Текст комментария не должен быть пуст")
    String text;
    long itemId;
    String authorName;
    LocalDateTime created;

    public CommentDto(long id, String text, long itemId, String authorName) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorName = authorName;
    }
}
