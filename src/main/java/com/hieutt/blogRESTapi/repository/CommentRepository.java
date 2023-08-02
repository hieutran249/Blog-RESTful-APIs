package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.Comment;
import com.hieutt.blogRESTapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    @Query(
            value = "select * from Comments c " +
                    "where c.replied_comment_id = ?1",
            nativeQuery = true
    )
    List<Comment> findReplyComments(Long commentId);
}
