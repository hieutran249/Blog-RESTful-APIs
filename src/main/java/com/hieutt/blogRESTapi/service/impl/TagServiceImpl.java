package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.TagDto;
import com.hieutt.blogRESTapi.entity.Tag;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.TagRepository;
import com.hieutt.blogRESTapi.service.TagService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ModelMapper mapper;

    public TagServiceImpl(TagRepository tagRepository, ModelMapper mapper) {
        this.tagRepository = tagRepository;
        this.mapper = mapper;
    }

    @Override
    public TagDto createTag(TagDto tagDto) {
        Tag tag = mapToEntity(tagDto);
        return mapToDto(tagRepository.save(tag));
    }

    @Override
    public List<TagDto> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> mapToDto(tag))
                .collect(Collectors.toList());
    }

    @Override
    public TagDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        return mapToDto(tag);
    }

    @Override
    public TagDto updateTag(Long id, TagDto tagDto) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tag.setName(tagDto.getName());
        tag.setQuantity(tag.getQuantity());
        tagRepository.save(tag);
        return mapToDto(tag);
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tagRepository.delete(tag);
    }

    private TagDto mapToDto(Tag tag) {
        return mapper.map(tag, TagDto.class);
    }

    private Tag mapToEntity(TagDto tagDto) {
        return mapper.map(tagDto, Tag.class);
    }
}
