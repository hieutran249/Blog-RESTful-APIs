package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.CommentDto;
import com.hieutt.blogRESTapi.entity.Comment;
import com.hieutt.blogRESTapi.entity.Post;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.BlogAPIException;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.CommentRepository;
import com.hieutt.blogRESTapi.repository.PostRepository;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto, Long repliedCmtId, Authentication authentication) {
        // convert DTO into Entity
        Comment comment = mapToEntity(commentDto);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikes(0);
        if (repliedCmtId == null) {
            comment.setReplyToComment(null);
        }
        else {
            Comment repliedComment = commentRepository.findById(repliedCmtId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", repliedCmtId));
            comment.setReplyToComment(repliedComment);
        }

        // retrieve Post entity by id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );

        // get the principle of the current logged-in user
        UserDetails currentUserPrinciple = (UserDetails) authentication.getPrincipal();
        // get the email of the current user
        String email = currentUserPrinciple.getUsername();
        // set the current User
        comment.setAuthor(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email)));

        // set Post
        comment.setPost(post);

        // save Comment entity to db
        Comment newComment = commentRepository.save(comment);

        // return converted Entity into DTO
        return mapToDto(newComment);
    }

    @Override
    public List<CommentDto> getAllComments(Long postId) {
        // retrieve comments by postId
        List<Comment> comments = commentRepository.findByPostId(postId);

        // return converted comments
        return comments
                .stream()
                .map((comment -> mapToDto(comment)))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        Comment comment = checkExistence(postId, commentId);

        return mapToDto(comment);
    }

    @Override
    public List<CommentDto> getReplyComments(Long postId, Long commentId) {
        checkExistence(postId, commentId);
        List<Comment> replyComments = commentRepository.findReplyComments(commentId);

        return replyComments.stream()
                .map((comment -> mapToDto(comment)))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto, Authentication authentication) {
        // get current user
        User user = getCurrentUser(authentication);

        // check comment existence and get comment
        Comment comment = checkExistence(postId, commentId);

        // check if comment belongs to user
        if (comment.getAuthor().equals(user)) {
            if (Objects.nonNull(commentDto.getContent()) &&
                    !"".equalsIgnoreCase(comment.getContent())) {
                comment.setContent(commentDto.getContent());
            }
            comment.setUpdatedAt(LocalDateTime.now());
        }
        else throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This comment does not belong to this user");

        Comment updatedComment = commentRepository.save(comment);

        return mapToDto(updatedComment);

    }

    @Override
    public void deleteComment(Long postId, Long commentId, Authentication authentication) {
        // get current user
        User user = getCurrentUser(authentication);

        // check comment existence and get comment
        Comment comment = checkExistence(postId, commentId);

        // check if comment belongs to user
        if (comment.getAuthor().equals(user)) {
            commentRepository.delete(comment);
        }
        else throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This comment does not belong to this user");
    }

    // convert Entity into DTO
    private CommentDto mapToDto(Comment comment) {
        // Mapping using ModelMapper
        CommentDto commentDto = mapper.map(comment, CommentDto.class);

//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(comment.getId());
//        commentDto.setBody(comment.getBody());

        return commentDto;
    }

    // convert DTO into Entity
    private Comment mapToEntity(CommentDto commentDto) {
        // Mapping using ModelMapper
        Comment comment = mapper.map(commentDto, Comment.class);

//        Comment comment = new Comment();
//        comment.setBody(commentDto.getBody());

        return comment;
    }

    private Comment checkExistence(Long postId, Long commentId) {
        // retrieve Post entity by id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );

        // retrieve Comment by id
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId)
        );

        // check if comment belongs to post
        if(!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This comment does not belong to this post!");
        }

        return comment;
    }

    private User getCurrentUser(Authentication authentication) {
        // get current user
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }
}
