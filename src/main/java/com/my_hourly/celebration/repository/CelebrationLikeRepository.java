package com.my_hourly.celebration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_hourly.celebration.entity.CelebrationLike;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrationLikeRepository extends JpaRepository<CelebrationLike,Long>{
    long countByCelebrationPostId(Long postId);
    boolean existsByCelebrationPostIdAndEmployeeId(Long postId,Long employeeId);
    void deleteByCelebrationPostIdAndEmployeeId(Long postId,Long employeeId);
}