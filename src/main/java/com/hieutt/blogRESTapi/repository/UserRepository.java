package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(
            value = "select 1 from user_followers uf" +
                    "where uf.user_id = ?1" +
                    "and uf.follower_id = ?2" +
                    "limit 1",
            nativeQuery = true
    )
    int followedUser(Long userId, Long followerId);

    @Query(
            value = "delete from user_followers" +
                    "where user_id = ?1" +
                    "and follower_id = ?2",
            nativeQuery = true
    )
    void unfollowUser(Long userId, Long followerId);

    @Query(
            value = "insert into user_followers (user_id, follower_id)" +
                    "values (?1, ?2)",
            nativeQuery = true
    )
    void saveFollower(Long userId, Long followerId);

    @Query(
            value = "select * from User u1" +
                    "inner join user_followers uf" +
                    "on u1.user_id = uf.user_id" +
                    "inner join User u2" +
                    "on u2.user_id = uf.follower_id" +
                    "where u.user_id = ?1",
            nativeQuery = true
    )
    List<User> findFollowers(Long userId);

    @Query(
            value = "select * from User u" +
                    "inner join user_followers uf" +
                    "on u.user_id = uf.follower_id" +
                    "where u.user_id = ?1",
            nativeQuery = true
    )
    List<User> findFollowings(Long userId);

}
