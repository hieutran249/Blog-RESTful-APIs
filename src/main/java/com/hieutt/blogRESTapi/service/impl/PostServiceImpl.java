package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import com.hieutt.blogRESTapi.entity.Category;
import com.hieutt.blogRESTapi.entity.Post;
import com.hieutt.blogRESTapi.entity.Tag;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.BlogAPIException;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.CategoryRepository;
import com.hieutt.blogRESTapi.repository.PostRepository;
import com.hieutt.blogRESTapi.repository.TagRepository;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UserRepository userRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto, String tags, Authentication authentication) {
        // convert dto to entity
        Post post = mapToEntity(postDto);
        post.setView(0);
        post.setVote(0);
        post.setCreatedAt(LocalDateTime.now());

        // remove space around the string tags
        tags = tags.trim();

        // convert tags from string to list and set tags
        List<Tag> tagList = toTagList(tags);
        post.setTags(tagList);

        // get the principle of the current logged-in user
        UserDetails currentUserPrinciple = (UserDetails) authentication.getPrincipal();
        // get the email of the current user
        String email = currentUserPrinciple.getUsername();
        // set the current User
        post.setAuthor(userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email)));

        // save post into DB
        Post newPost = postRepository.save(post);

        // convert entity to dto
        PostDto postResponse = mapToDto(newPost);

        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.findAll(pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public PostResponse getPostsByCategory(int pageNo, int pageSize, String sortBy, String sortDir, Long categoryId) {
        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Page<Post> posts = postRepository.findByCategory(category, pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public PostResponse getPostsByTag(int pageNo, int pageSize, String sortBy, String sortDir, Long tagId) {
        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.findByTag(tagId, pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id, String tags) {
        // get post by id from the db
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCategory(mapper.map(postDto.getCategory(), Category.class));
        post.setUpdatedAt(LocalDateTime.now());

        // convert String to List
        List<Tag> tagList = toTagList(tags);
        post.setTags(tagList);

        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        List<Post> posts = user.getPosts();
        return posts.stream()
                .map(post -> PostDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    // convert Entity into DTO
    private PostDto mapToDto(Post post) {
        // Mapping using ModelMapper
        return mapper.map(post, PostDto.class);
    }

    // convert DTO into Entity
    private Post mapToEntity(PostDto postDto) {
        // Mapping using ModelMapper
        return mapper.map(postDto, Post.class);
    }

    // convert categories parameter into a List
    private List<Tag> toTagList(String tags) {
        List<Tag> tagList = new ArrayList<>();
        tags = removeRedundantChar(tags);

        if (tags.contains(",") ) {
            String[] tagArr = tags.split(",");
            tagList = Arrays.stream(tagArr)
                    .map(tag -> {
                        tag = tag.trim();
                        if (hasNoneAlphabetic(tag))
                            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Tag name must not have special character");
                        if (tagRepository.existsByName(tag)) {
                            return tagRepository.findByName(tag);
                        }
                        else {
                            Tag newTag = Tag.builder()
                                    .name(tag)
                                    .build();
                            return tagRepository.save(newTag);
                        }
                    })
                    .collect(Collectors.toList());
        }
        else {
            tags = tags.trim();
            if (tagRepository.existsByName(tags)) {
                tagList.add(tagRepository.findByName(tags));
            }
            else {
                Tag newTag = Tag.builder()
                        .name(tags)
                        .build();
                tagList.add(tagRepository.save(newTag));
            }
        };

        return tagList;
    }

    private boolean hasNoneAlphabetic(String text) {
        return text.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    // delete redundant special characters at the beginning and the end of the string
    private String removeRedundantChar(String text) {
        while(!Character.isAlphabetic(text.charAt(0))) {
            text = text.substring(1);
        }
        for (int i=text.length()-1; i>=0; --i) {
            if (Character.isAlphabetic(text.charAt(i))) {
                text = text.substring(0, i + 1);
                break;
            }
        }
        return text;
    }

    private Pageable createPage(int pageNo, int pageSize, String sortBy, String sortDir) {
        // Pagination & Sorting
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return pageable;
    }

    private PostResponse getPageContent(Page<Post> posts) {
        // get content for page object
        List<Post> postList = posts.getContent();

        List<PostDto> content =  postList
                .stream()
                .map(post -> mapToDto(post))
                .collect(Collectors.toList());

        // return more info to client
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }
}
