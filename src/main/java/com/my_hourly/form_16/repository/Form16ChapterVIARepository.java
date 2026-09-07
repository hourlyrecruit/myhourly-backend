package com.my_hourly.form_16.repository;

import com.my_hourly.form_16.entity.Form16ChapterVIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Form16ChapterVIARepository
        extends JpaRepository<Form16ChapterVIA, Long> {

    Optional<Form16ChapterVIA> findByForm16Id(Long form16Id);

    boolean existsByForm16Id(Long form16Id);

    void deleteByForm16Id(Long form16Id);
}