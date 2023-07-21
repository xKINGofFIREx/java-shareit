package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getCreated(),
                commentDto.getItem() == null ? null : ItemMapper.toItem(commentDto.getItem()),
                commentDto.getAuthor() == null ? null : UserMapper.toUser(commentDto.getAuthor())
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getCreated(),
                ItemMapper.toItemDto(comment.getItem()),
                UserMapper.toUserDto(comment.getAuthor()),
                comment.getAuthor().getName());
    }

    public static List<CommentDto> toCommentDtos(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments)
            commentDtos.add(toCommentDto(comment));
        return commentDtos;
    }
}
