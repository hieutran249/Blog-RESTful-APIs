package com.hieutt.blogRESTapi.repository;

import com.hieutt.blogRESTapi.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t from Token t INNER JOIN User u on t.user.id = u.id " +
            "WHERE u.id = :userId and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findByToken(String token);
}
