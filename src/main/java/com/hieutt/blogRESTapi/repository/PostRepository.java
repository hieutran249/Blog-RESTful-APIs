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
    @Query("SELECT p FROM Post p WHERE p.categories IN :categories")
    Page<Post> findAllByCategories(@Param("categories") List<Category> categories, Pageable pageable);
}
