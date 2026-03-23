package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // General search: name, skill, language
    List<User> findBySkillsHaveContainingIgnoreCaseOrNameContainingIgnoreCaseOrLanguagesContainingIgnoreCase(
            String skill, String name, String language);

    // Advanced filtered search
    @Query("""
        SELECT u FROM User u
        WHERE (:q      IS NULL OR :q      = '' OR
               LOWER(u.name)       LIKE LOWER(CONCAT('%',:q,'%')) OR
               LOWER(u.skillsHave) LIKE LOWER(CONCAT('%',:q,'%')) OR
               LOWER(u.skillsWant) LIKE LOWER(CONCAT('%',:q,'%')))
          AND (:lang     IS NULL OR :lang     = '' OR LOWER(u.languages) LIKE LOWER(CONCAT('%',:lang,'%')))
          AND (:location IS NULL OR :location = '' OR LOWER(u.location)  LIKE LOWER(CONCAT('%',:location,'%')))
          AND (:skill    IS NULL OR :skill    = '' OR LOWER(u.skillsHave) LIKE LOWER(CONCAT('%',:skill,'%')))
    """)
    List<User> advancedSearch(
            @Param("q")        String q,
            @Param("lang")     String lang,
            @Param("location") String location,
            @Param("skill")    String skill
    );
}
