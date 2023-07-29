package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);
    Tag findByName(String name);
}
