package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;
    @NotBlank(message = "Текст комментария не должен быть пуст")
    private String text;
    private long itemId;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(long id, String text, long itemId, String authorName) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorName = authorName;
    }
}
