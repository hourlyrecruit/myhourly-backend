package com.my_hourly.celebration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_hourly.celebration.entity.CelebrationTag;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrationTagRepository extends JpaRepository<CelebrationTag,Long>{
    List<CelebrationTag> findByCelebrationPostId(Long postId);
    void deleteByCelebrationPostId(Long postId);

}