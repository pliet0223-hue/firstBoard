package com.example.newboard.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentCreateRequest {
    private String content;
    private String nickname; // optional
}