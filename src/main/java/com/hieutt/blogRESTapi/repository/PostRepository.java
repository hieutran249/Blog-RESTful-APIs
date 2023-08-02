package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.Category;
import com.hieutt.blogRESTapi.entity.Post;
import com.hieutt.blogRESTapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findByAuthor(User user, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.author.displayedName like %?1% or p.title like %?1%")
    Page<Post> searchByTitleOrAuthor(String keyword, Pageable pageable);

    @Query(
            value = "select p.id, p.content, p.created_at, p.likes, p.title, p.updated_at, p.views, p.user_id, p.category_id " +
                    "from Posts p " +
                    "inner join post_tags pt on pt.post_id = p.id " +
                    "where pt.tag_id = ?1",
            nativeQuery = true
    )
    Page<Post> findByTag(Long tagId, Pageable pageable);

    @Query(
            value = "select p.id, p.content, p.created_at, p.likes, p.title, p.updated_at, p.views, p.user_id, p.category_id " +
                    "from Posts p " +
                    "inner join user_bookmarks ub on ub.post_id = p.id " +
                    "where ub.user_id = ?1",
            nativeQuery = true
    )
    Page<Post> findBookmarksByAuthor(Long userId, Pageable pageable);

    @Query(
            value = "select 1 from user_bookmarks " +
                    "where exists(select * from user_bookmarks ub " +
                    "where ub.user_id = ?1 " +
                    "and ub.post_id = ?2 " +
                    "limit 1)",
            nativeQuery = true
    )
    String bookmarkedPost(Long userId, Long postId);

    @Transactional
    @Modifying
    @Query(
            value = "delete from user_bookmarks " +
                    "where user_id = ?1 " +
                    "and post_id = ?2",
            nativeQuery = true
    )
    void removeBookmark(Long userId, Long postId);

    @Transactional
    @Modifying
    @Query(
            value = "insert into user_bookmarks (user_id, post_id) " +
                    "values (?1, ?2)",
            nativeQuery = true
    )
    void saveBookmarkedPost(Long userId, Long postId);

    @Query(
            value = "select 1 from user_likeds " +
                    "where exists(select * from user_likeds ul " +
                    "where ul.user_id = ?1 " +
                    "and ul.post_id = ?2 " +
                    "limit 1)",
            nativeQuery = true
    )
    String likedPost(Long userId, Long postId);

    @Transactional
    @Modifying
    @Query(
            value = "delete from user_likeds " +
                    "where user_id = ?1 " +
                    "and post_id = ?2 ",
            nativeQuery = true
    )
    void removeLike(Long userId, Long postId);

    @Transactional
    @Modifying
    @Query(
            value = "insert into user_likeds (user_id, post_id) " +
                    "values (?1, ?2)",
            nativeQuery = true
    )
    void saveLikedPost(Long userId, Long postId);

    @Query(
            value = "select p.id, p.content, p.created_at, p.likes, p.title, p.updated_at, p.views, p.user_id, p.category_id " +
                    "from Posts p " +
                    "inner join user_followers uf " +
                    "on p.user_id = uf.user_id " +
                    "where uf.follower_id = ?1",
            nativeQuery = true
    )
    Page<Post> findPostsByFollowings(Long userId, Pageable pageable);
}
