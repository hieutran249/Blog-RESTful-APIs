package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.FormattedPost;
import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import com.hieutt.blogRESTapi.entity.Category;
import com.hieutt.blogRESTapi.entity.Post;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.CategoryRepository;
import com.hieutt.blogRESTapi.repository.PostRepository;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, UserRepository userRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto, String categories, Authentication authentication) {
        // convert dto to entity
        Post post = mapToEntity(postDto);
        
        // convert String to List
        List<Category> categoryList = toCategoryList(categories);
        post.setCategories(categoryList);

        // get the principle of the current logged-in user
        UserDetails currentUserPrinciple = (UserDetails) authentication.getPrincipal();
        // get the email of the current user
        String email = currentUserPrinciple.getUsername();
        // set the current User
        post.setUser(userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email)));
        
        // save post into DB
        Post newPost = postRepository.save(post);

        // convert entity to dto
        PostDto postResponse = mapToDto(newPost);

        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir, String categories) {
        // Pagination & Sorting
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // check if there is any provided categories
        Page<Post> posts;
        if (categories.equals("")) {
            posts = postRepository.findAll(pageable);
        }
        // filtering does not work yet
        else {
            // convert string param to list
            List<Category> categoryList = toCategoryList(categories);
            posts = postRepository.findAllByCategories(categoryList, pageable);
        }

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

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id, String categories) {
        // get post by id from the db
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        // convert String to List
        List<Category> categoryList = toCategoryList(categories);
        post.setCategories(categoryList);

        Post updatedPost = postRepository.save(post);

        return mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        post.setCategories(null);
        postRepository.delete(post);
    }

    @Override
    public List<FormattedPost> getPostsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        List<Post> posts = user.getPosts();
        return posts.stream()
                .map(post -> FormattedPost.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    // convert Entity into DTO
    private PostDto mapToDto(Post post) {
        // Mapping using ModelMapper
        PostDto postDto = mapper.map(post, PostDto.class);

//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setContent(post.getContent());

        return postDto;
    }

    // convert DTO into Entity
    private Post mapToEntity(PostDto postDto) {
        // Mapping using ModelMapper
        Post post = mapper.map(postDto, Post.class);

//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());

        return post;
    }

    // convert categories parameter into a List
    private List<Category> toCategoryList(String categories) {
        List<Category> categoryList = new ArrayList<>();
        if (categories.contains(",")) {
            String[] categoryArr = categories.split(",");
            categoryList = Arrays.stream(categoryArr)
                    .map(cate -> categoryRepository.findByName(cate))
                    .collect(Collectors.toList());
        }
        else categoryList.add(categoryRepository.findByName(categories));

        return categoryList;
    }
}
