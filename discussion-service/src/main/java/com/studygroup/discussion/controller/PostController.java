package com.studygroup.discussion.controller;

import com.studygroup.discussion.dto.*;
import com.studygroup.discussion.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // ---- Posts ----

    @PostMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<PostDto>> createPost(
            @PathVariable Long groupId,
            @Valid @RequestBody CreatePostRequest request) {
        log.info("Creating post in group {}", groupId);
        request.setGroupId(groupId);
        PostDto post = postService.createPost(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostDto>builder()
                        .success(true)
                        .message("Post created successfully")
                        .data(post)
                        .build());
    }

    @GetMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<Page<PostDto>>> getPostsByGroup(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching posts for group {}", groupId);
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDto> posts = postService.getPostsByGroup(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<PostDto>>builder()
                .success(true)
                .message("Posts fetched successfully")
                .data(posts)
                .build());
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> getPostById(@PathVariable Long postId) {
        log.info("Fetching post {}", postId);
        PostDto post = postService.getPostById(postId);
        return ResponseEntity.ok(ApiResponse.<PostDto>builder()
                .success(true)
                .message("Post fetched successfully")
                .data(post)
                .build());
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostRequest request) {
        log.info("Updating post {}", postId);
        PostDto post = postService.updatePost(postId, request);
        return ResponseEntity.ok(ApiResponse.<PostDto>builder()
                .success(true)
                .message("Post updated successfully")
                .data(post)
                .build());
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        log.info("Deleting post {}", postId);
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Post deleted successfully")
                .build());
    }

    // ---- Replies ----

    @PostMapping("/groups/{groupId}/posts/{postId}/replies")
    public ResponseEntity<ApiResponse<ReplyDto>> addReply(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @Valid @RequestBody CreateReplyRequest request) {
        log.info("Adding reply to post {} in group {}", postId, groupId);
        ReplyDto reply = postService.addReply(groupId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ReplyDto>builder()
                        .success(true)
                        .message("Reply added successfully")
                        .data(reply)
                        .build());
    }

    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<ApiResponse<Void>> deleteReply(@PathVariable Long replyId) {
        log.info("Deleting reply {}", replyId);
        postService.deleteReply(replyId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Reply deleted successfully")
                .build());
    }
}
