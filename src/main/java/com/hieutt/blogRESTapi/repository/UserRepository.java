package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(
            value = "select 1 from user_followers " +
                    "where exists(select * from user_followers uf " +
                    "where uf.user_id = ?1 " +
                    "and uf.follower_id = ?2 " +
                    "limit 1)",
            nativeQuery = true
    )
    String followedUser(Long userId, Long followerId);

    // No need to use query to insert/delete, just directly manipulate the object then save it
//    @Transactional
//    @Modifying
//    @Query(
//            value = "delete from user_followers " +
//                    "where user_id = ?1 " +
//                    "and follower_id = ?2",
//            nativeQuery = true
//    )
//    void unfollowUser(Long userId, Long followerId);
//
//    @Transactional
//    @Modifying
//    @Query(
//            value = "insert into user_followers (user_id, follower_id) " +
//                    "values (?1, ?2)",
//            nativeQuery = true
//    )
//    void saveFollower(Long userId, Long followerId);

    @Query(
            value = "select u2.id, u2.displayed_name, u2.email, u2.username, u2.role " +
                    "from Users u1 " +
                    "inner join user_followers uf " +
                    "on u1.id = uf.user_id " +
                    "inner join Users u2 " +
                    "on u2.id = uf.follower_id " +
                    "where u1.id = ?1",
            nativeQuery = true
    )
    List<User> findFollowers(Long userId);

    @Query(
            value = "select u.id, u.displayed_name, u.email, u.username, u.role " +
                    "from users u " +
                    "inner join user_followers uf " +
                    "on u.id = uf.user_id " +
                    "where uf.follower_id = ?1",
            nativeQuery = true
    )
    List<User> findFollowings(Long userId);

}
