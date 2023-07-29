package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.TagDto;

import java.util.List;

public interface TagService {
    TagDto createTag(TagDto tagDto);
    List<TagDto> getAllTags();
    TagDto getTagById(Long id);
    TagDto updateTag(Long id, TagDto tagDto);
    void deleteTag(Long id);
}
