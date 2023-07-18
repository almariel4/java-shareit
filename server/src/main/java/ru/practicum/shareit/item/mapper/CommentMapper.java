package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment mapToComment(Item item, User user, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

}
