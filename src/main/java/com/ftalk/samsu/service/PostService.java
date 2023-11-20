package com.ftalk.samsu.service;

import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;

public interface PostService {

	PagedResponse<Post> getAllPosts(int page, int size);

	PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size);

	Post updatePost(Integer id, PostRequest newPostRequest, UserPrincipal currentUser);

	ApiResponse deletePost(Integer id, UserPrincipal currentUser);

	Post addPost(PostRequest postRequest, UserPrincipal currentUser);

	Post getPost(Integer id);

}
