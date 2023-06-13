package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.CommentDto;
import com.hieutt.blogRESTapi.entity.Comment;
import com.hieutt.blogRESTapi.entity.Post;
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

import java.util.List;
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
    public CommentDto createComment(Long postId, CommentDto commentDto, Authentication authentication) {
        // convert DTO into Entity
        Comment comment = mapToEntity(commentDto);

        // retrieve Post entity by id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );

        // get the principle of the current logged-in user
        UserDetails currentUserPrinciple = (UserDetails) authentication.getPrincipal();
        // get the email of the current user
        String email = currentUserPrinciple.getUsername();
        // set the current User
        comment.setUser(userRepository.findByEmail(email).get());

        // set Post to Comment entity
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
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        Comment comment = checkExistence(postId, commentId);

        comment.setBody(commentDto.getBody());

        Comment updatedComment = commentRepository.save(comment);

        return mapToDto(updatedComment);

    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = checkExistence(postId, commentId);

        commentRepository.delete(comment);
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
}
