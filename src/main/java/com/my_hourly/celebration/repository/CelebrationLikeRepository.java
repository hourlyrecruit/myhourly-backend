package com.my_hourly.celebration.repository;

import com.my_hourly.celebration.entity.CelebrationLike;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CelebrationLikeRepository extends JpaRepository<CelebrationLike, Long> {

    long countByCelebrationPostId(Long postId);
    boolean existsByCelebrationPostIdAndUserId(Long postId, Long userId);
    @Transactional
    void deleteByCelebrationPostIdAndUserId(Long postId, Long userId);
    List<CelebrationLike> findByCelebrationPostId(Long postId);
}