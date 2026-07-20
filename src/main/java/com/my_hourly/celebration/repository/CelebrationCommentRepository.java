package com.my_hourly.celebration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_hourly.celebration.entity.CelebrationComment;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrationCommentRepository extends JpaRepository<CelebrationComment,Long>{
    long countByCelebrationPostId(Long postId);

}