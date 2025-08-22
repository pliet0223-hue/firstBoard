package com.example.newboard.comment;

import com.example.newboard.domain.Article;
import com.example.newboard.repository.ArticleRepository;
import com.example.newboard.comment.dto.CommentCreateRequest;
import com.example.newboard.domain.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<CommentResponse> list(Long articleId) {
        return commentRepository.findByArticleIdOrderByIdAsc(articleId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse create(Long articleId, CommentCreateRequest req, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String email = auth.getName();
        User user = (User) auth.getPrincipal(); // UserDetails 구현 필요

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + articleId));

        Comment saved = commentRepository.save(Comment.create(article, req.getContent(), email, user));
        return CommentResponse.from(saved);
    }

    @Transactional
    public void delete(Long commentId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String email = auth.getName();
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));

        if (!c.isOwner(email)) {
            throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(c);
    }
}