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
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        post.setViews(0);
        post.setLikes(0);
        post.setCreatedAt(LocalDateTime.now());

        if (tags != null) {
            // remove space around the string tags
            tags = tags.trim();

            // convert tags from string to list and set tags
            List<Tag> tagList = toTagList(tags);
            post.setTags(tagList);
        }

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
    public PostResponse getPostsByUser(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Page<Post> posts = postRepository.findByAuthor(author, pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public PostResponse getBookmarksByUser(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.findBookmarksByAuthor(userId, pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public PostResponse getPostsByFollowings(int pageNo, int pageSize, String sortBy, String sortDir, Long userId, Authentication authentication) {
        getCurrentUser(authentication);

        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        Page<Post> posts = postRepository.findPostsByFollowings(userId, pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id, String tags, Authentication authentication) {
        // get current user
        User user = getCurrentUser(authentication);

        // get post by id from the db
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );

        // check if post belong to user
        if (!post.getAuthor().equals(user)) {
            throw new BlogAPIException(HttpStatus.FORBIDDEN, "This post does not belong to this user");
        }

        update(post, postDto, tags);

        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(Long postId, Authentication authentication) {
        // get current user
        User user = getCurrentUser(authentication);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );

        // check if post belong to user
        if (!post.getAuthor().equals(user)) {
            throw new BlogAPIException(HttpStatus.FORBIDDEN, "This post does not belong to this user");
        }

        postRepository.delete(post);
    }

    @Override
    public PostResponse searchByTitleOrAuthor(int pageNo, int pageSize, String sortBy, String sortDir, String keyword) {
        Pageable pageable = createPage(pageNo, pageSize, sortBy, sortDir);

        keyword = removeRedundantChars(keyword.trim());

        Page<Post> posts = postRepository.searchByTitleOrAuthor(keyword, pageable);

        PostResponse postResponse = getPageContent(posts);

        return postResponse;
    }

    @Override
    public String likePost(Long postId, Authentication authentication) {
        String message;
        // get current user
        User user = getCurrentUser(authentication);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );

        // check if user had liked the post
        if (Objects.equals(postRepository.likedPost(user.getId(), postId), "1")) {
            post.setLikes(post.getLikes() - 1);
            postRepository.removeLike(user.getId(), postId);
            message = "unliked";
        }
        else {
            post.setLikes(post.getLikes() + 1);
            postRepository.saveLikedPost(user.getId(), postId);
            message = "liked";
        }
        postRepository.save(post);
        return message;
    }

    @Override
    public String bookmarkPost(Long postId, Authentication authentication) {
        String message;
        // get current user
        User user = getCurrentUser(authentication);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );

        // check if user had bookmarked the post
        if (Objects.equals(postRepository.bookmarkedPost(user.getId(), postId), "1")) {
            postRepository.removeBookmark(user.getId(), postId);
            message = "unbookmarked";
        }
        else {
            postRepository.saveBookmarkedPost(user.getId(), postId);
            message = "bookmarked";
        }
        return message;
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
        tags = removeRedundantChars(tags);

        if (tags.contains(",") ) {
            String[] tagArr = tags.split(",");
            tagList = Arrays.stream(tagArr)
                    .map(tag -> {
                        tag = tag.trim();
                        if (hasNoneAlphabetic(tag))
                            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Tag name must not have special character");
                        if (tagRepository.existsByName(tag)) {
                            Tag foundTag = tagRepository.findByName(tag);
                            foundTag.setQuantity(foundTag.getQuantity() + 1);
                            tagRepository.save(foundTag);
                            return foundTag;
                        }
                        else {
                            Tag newTag = Tag.builder()
                                    .name(tag)
                                    .build();
                            newTag.setQuantity(1);
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
        }

        return tagList;
    }

    private boolean hasNoneAlphabetic(String text) {
        return text.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    // delete redundant special characters at the beginning and the end of the string
    private String removeRedundantChars(String text) {
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

    private User getCurrentUser(Authentication authentication) {
        // get current user
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }

    private void update(Post post, PostDto postDto, String tags) {
        if (Objects.nonNull(postDto.getTitle()) &&
                !"".equalsIgnoreCase(postDto.getTitle())) {
            post.setTitle(postDto.getTitle());
        }
        if (Objects.nonNull(postDto.getContent()) &&
                !"".equalsIgnoreCase(postDto.getContent())) {
            post.setContent(postDto.getContent());
        }
        if (Objects.nonNull(postDto.getCategory())) {
            post.setCategory(mapper.map(postDto.getCategory(), Category.class));
        }
        post.setUpdatedAt(LocalDateTime.now());

        if (tags != null && !"".equalsIgnoreCase(tags)) {
            // convert String to List
            List<Tag> tagList = toTagList(tags);
            post.setTags(tagList);
        }
    }
}
