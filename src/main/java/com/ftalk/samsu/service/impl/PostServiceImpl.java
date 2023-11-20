package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.repository.EventRepository;
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

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    @Override
    public PagedResponse<Post> getAllPosts(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PagedResponse<Post> getPostsByCreatedBy(String rollnumber, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        User user = userRepository.getUserByRollnumber(rollnumber);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Post> posts = postRepository.findById(user.getId(), pageable);
        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();
        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }


    @Override
    public Post updatePost(Integer id, PostRequest newPostRequest, UserPrincipal currentUser) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", ID, id));
        Event event = eventRepository.findById(newPostRequest.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Events", ID, newPostRequest.getEventId()));
        User user = userRepository.getUser(currentUser);

//        if (post.getUser().getId().equals(currentUser.getId())
//                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
        post.setTitle(newPostRequest.getTitle());
        post.setBody(newPostRequest.getBody());
        post.setUser(user);
        post.setEvent(event);
        post.setKudos(newPostRequest.getKudos());
        post.setStatus(newPostRequest.getStatus());
        post.setFile_urls(newPostRequest.getFile_urls());
        post.setImage_urls(newPostRequest.getImage_urls());
        return postRepository.save(post);
//        }
//        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this post");

//        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse deletePost(Integer id, UserPrincipal currentUser) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
        if (post.getUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            postRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted post");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this post");

        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public Post addPost(PostRequest postRequest, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, 1L));
        Event event = eventRepository.findById(postRequest.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Events", ID, postRequest.getEventId()));
        Post post = new Post(postRequest);
        post.setUser(user);
        post.setEvent(event);
        return postRepository.save(post);
    }

    @Override
    public Post getPost(Integer id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", ID, id));
    }


}
