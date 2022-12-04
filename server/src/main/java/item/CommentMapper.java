package item;

import item.dto.CommentDto;
import lombok.experimental.UtilityClass;
import item.model.Comment;
import item.model.Item;
import user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(User user, Item item, String text) {
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
