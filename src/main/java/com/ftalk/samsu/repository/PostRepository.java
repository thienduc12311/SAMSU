package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findByCreatedBy(Integer userId, Pageable pageable);

	Page<Post> findByCategory(Long categoryId, Pageable pageable);

	Page<Post> findByTags(List<Tag> tags, Pageable pageable);

	Long countByCreatedBy(Integer userId);
}
