package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.Category;
import com.hieutt.blogRESTapi.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCategory(Category category, Pageable pageable);
    @Query(
            value = "select * from p" +
                    "inner join post_tags pt on pt.post_id = p.id" +
                    "inner join tags t on pt.tag_id = t.id" +
                    "where t.id = ?1",
            nativeQuery = true
    )
    Page<Post> findByTag(Long categoryId, Pageable pageable);
}
