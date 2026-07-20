package com.my_hourly.celebration.repository;

import java.util.List;

import com.my_hourly.celebration.entity.CelebrationType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.my_hourly.celebration.entity.CelebrationPost;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrationRepository extends JpaRepository<CelebrationPost,Long>{
    List<CelebrationPost> findByCelebrationType(CelebrationType celebrationType);

}