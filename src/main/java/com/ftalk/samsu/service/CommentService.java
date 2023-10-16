package com.ftalk.samsu.service;

import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.Comment;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.CommentRequest;
import com.ftalk.samsu.payload.PagedResponse;

public interface CommentService {

	PagedResponse<Comment> getAllComments(Long postId, int page, int size);

	Comment addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser);

	Comment getComment(Long postId, Long id);

	Comment updateComment(Long postId, Long id, CommentRequest commentRequest, UserPrincipal currentUser);

	ApiResponse deleteComment(Long postId, Long id, UserPrincipal currentUser);

}
