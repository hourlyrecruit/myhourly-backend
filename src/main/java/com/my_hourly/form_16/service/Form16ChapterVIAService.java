package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16ChapterVIARequest;
import com.my_hourly.form_16.dto.Form16ChapterVIAResponse;

public interface Form16ChapterVIAService  {

    Form16ChapterVIAResponse createChapterVIA(
            Long form16Id,
            Form16ChapterVIARequest request
    );

    Form16ChapterVIAResponse getChapterVIAByForm16Id(
            Long form16Id
    );

    Form16ChapterVIAResponse updateChapterVIA(
            Long form16Id,
            Form16ChapterVIARequest request
    );

    Form16ChapterVIAResponse autoSaveChapterVIA(
            Long form16Id,
            Form16ChapterVIARequest request
    );

    void deleteChapterVIA(
            Long form16Id
    );
}