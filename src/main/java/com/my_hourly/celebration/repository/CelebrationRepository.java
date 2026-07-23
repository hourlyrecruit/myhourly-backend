package com.my_hourly.celebration.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.my_hourly.celebration.entity.CelebrationType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.my_hourly.celebration.entity.CelebrationPost;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrationRepository extends JpaRepository<CelebrationPost,Long>{
    List<CelebrationPost> findByCelebrationType(CelebrationType celebrationType);
    List<CelebrationPost> findByActiveTrueOrderByCreatedAtDesc();
    @Transactional
    @Modifying
    @Query("""
    UPDATE CelebrationPost p
    SET p.active = false
    WHERE p.active = true
      AND p.createdAt <= :time
""")
    void expirePosts(@Param("time") LocalDateTime time);

}