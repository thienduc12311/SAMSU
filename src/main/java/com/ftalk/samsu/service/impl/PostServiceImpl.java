package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.Category;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.repository.CategoryRepository;
import com.ftalk.samsu.repository.PostRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.service.PostService;
import com.ftalk.samsu.utils.AppConstants;
import com.ftalk.samsu.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ftalk.samsu.utils.AppConstants.CATEGORY;
import static com.ftalk.samsu.utils.AppConstants.CREATED_AT;
import static com.ftalk.samsu.utils.AppConstants.ID;
import static com.ftalk.samsu.utils.AppConstants.POST;
import static com.ftalk.samsu.utils.AppConstants.TAG;
import static com.ftalk.samsu.utils.AppConstants.USER;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

//    @Autowired
//	private TagRepository tagRepository;

    @Override
    public PagedResponse<Post> getAllPosts(int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size) {
        validatePageNumberAndSize(page, size);
        User user = userRepository.getUserByName(username);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Post> posts = postRepository.findById(user.getId(), pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PagedResponse<Post> getPostsByCategory(Long id, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, ID, id));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Post> posts = postRepository.findByCategory(category.getId(), pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PagedResponse<Post> getPostsByTag(Long id, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

//		Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TAG, ID, id));

//		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

//		Page<Post> posts = postRepository.findByTags(Collections.singletonList(tag), pageable);

//		List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();
//
//		return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
//				posts.getTotalPages(), posts.isLast());
        return null;
    }

    @Override
    public Post updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST, ID, id));
        Category category = categoryRepository.findById(newPostRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, ID, newPostRequest.getCategoryId()));
        if (post.getUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            post.setTitle(newPostRequest.getTitle());
            post.setBody(newPostRequest.getBody());
            post.setCategory(category);
            return postRepository.save(post);
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this post");

        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse deletePost(Long id, UserPrincipal currentUser) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST, ID, id));
        if (post.getUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            postRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted post");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this post");

        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public PostResponse addPost(PostRequest postRequest, UserPrincipal currentUser) {
        User user = userRepository.findById(Long.valueOf(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, 1L));
        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, ID, postRequest.getCategoryId()));

        List<Tag> tags = new ArrayList<>(postRequest.getTags().size());

        for (String name : postRequest.getTags()) {
//			Tag tag = tagRepository.findByName(name);
//			tag = tag == null ? tagRepository.save(new Tag(name)) : tag;

//			tags.add(tag);
        }

        Post post = new Post();
        post.setBody(postRequest.getBody());
        post.setTitle(postRequest.getTitle());
        post.setCategory(category);
        post.setUser(user);
        post.setTags(tags);

        Post newPost = postRepository.save(post);

        PostResponse postResponse = new PostResponse();

        postResponse.setTitle(newPost.getTitle());
        postResponse.setBody(newPost.getBody());
        postResponse.setCategory(newPost.getCategory().getName());

        List<String> tagNames = new ArrayList<>(newPost.getTags().size());

        for (Tag tag : newPost.getTags()) {
            tagNames.add(tag.getName());
        }

        postResponse.setTags(tagNames);

        return postResponse;
    }

    @Override
    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST, ID, id));
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
