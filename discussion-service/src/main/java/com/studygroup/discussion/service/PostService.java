package com.studygroup.discussion.service;

import com.studygroup.discussion.aspect.RequireMembership;
import com.studygroup.discussion.dto.*;
import com.studygroup.discussion.exception.ForbiddenException;
import com.studygroup.discussion.exception.ResourceNotFoundException;
import com.studygroup.discussion.model.Post;
import com.studygroup.discussion.model.Reply;
import com.studygroup.discussion.repository.PostRepository;
import com.studygroup.discussion.repository.ReplyRepository;
import com.studygroup.discussion.security.AuthenticatedUser;
import com.studygroup.discussion.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @RequireMembership
    public PostDto createPost(Long groupId, CreatePostRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();

        Post post = Post.builder()
                .groupId(groupId)
                .authorId(currentUser.getUserId())
                .authorName(currentUser.getUsername())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post saved = postRepository.save(post);
        log.info("Post created: id={}, groupId={}, authorId={}", saved.getId(), groupId, currentUser.getUserId());

        PostDto dto = toDtoSummary(saved);
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/posts", dto);
        return dto;
    }

    /**
     * Returns a page of post summaries (no reply bodies, just reply count).
     * Avoids N+1 by using countByPostId per post rather than fetching all reply rows.
     */
    @Transactional(readOnly = true)
    @RequireMembership
    public Page<PostDto> getPostsByGroup(Long groupId, Pageable pageable) {
        return postRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable)
                .map(this::toDtoSummary);
    }

    /**
     * Returns a single post with full reply list.
     */
    @Transactional(readOnly = true)
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        List<Reply> replies = replyRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return toDtoWithReplies(post, replies);
    }

    public PostDto updatePost(Long postId, CreatePostRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!post.getAuthorId().equals(currentUser.getUserId()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Only the post author or an admin can update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        Post saved = postRepository.save(post);
        log.info("Post updated: id={}", postId);

        List<Reply> replies = replyRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return toDtoWithReplies(saved, replies);
    }

    public void deletePost(Long postId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!post.getAuthorId().equals(currentUser.getUserId()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Only the post author or an admin can delete this post");
        }

        postRepository.delete(post);
        log.info("Post deleted: id={}", postId);
    }

    @RequireMembership
    public ReplyDto addReply(Long groupId, Long postId, CreateReplyRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!post.getGroupId().equals(groupId)) {
            throw new ForbiddenException("Post does not belong to group " + groupId);
        }

        Reply reply = Reply.builder()
                .post(post)
                .authorId(currentUser.getUserId())
                .authorName(currentUser.getUsername())
                .content(request.getContent())
                .build();

        Reply saved = replyRepository.save(reply);
        log.info("Reply created: id={}, postId={}, authorId={}", saved.getId(), postId, currentUser.getUserId());

        ReplyDto dto = toReplyDto(saved);
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/posts/" + postId + "/replies", dto);
        return dto;
    }

    public void deleteReply(Long replyId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found with id: " + replyId));

        if (!reply.getAuthorId().equals(currentUser.getUserId()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Only the reply author or an admin can delete this reply");
        }

        replyRepository.delete(reply);
        log.info("Reply deleted: id={}", replyId);
    }

    // ---- Mappers ----

    /** Summary DTO: no reply bodies, just the count — used in list endpoints. */
    private PostDto toDtoSummary(Post post) {
        long count = replyRepository.countByPostId(post.getId());
        return PostDto.builder()
                .id(post.getId())
                .groupId(post.getGroupId())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .replies(List.of())
                .replyCount((int) count)
                .build();
    }

    /** Full DTO with replies — used in single-post detail endpoint. */
    private PostDto toDtoWithReplies(Post post, List<Reply> replies) {
        return PostDto.builder()
                .id(post.getId())
                .groupId(post.getGroupId())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .replies(replies.stream().map(this::toReplyDto).collect(Collectors.toList()))
                .replyCount(replies.size())
                .build();
    }

    private ReplyDto toReplyDto(Reply reply) {
        return ReplyDto.builder()
                .id(reply.getId())
                .postId(reply.getPost().getId())
                .authorId(reply.getAuthorId())
                .authorName(reply.getAuthorName())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}
