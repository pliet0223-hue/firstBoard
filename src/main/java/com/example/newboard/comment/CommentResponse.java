package com.example.newboard.comment;

import java.time.LocalDateTime;
import com.example.newboard.domain.User;

public record CommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String username
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getUser() != null ? comment.getUser().getName() : "anonymous"

        );
    }

}
