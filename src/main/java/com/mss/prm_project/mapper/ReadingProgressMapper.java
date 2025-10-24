package com.mss.prm_project.mapper;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.entity.ReadingProgress;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses =  {UserMapper.class, PaperMapper.class})
public interface ReadingProgressMapper {
    ReadingProgressMapper INSTANCE = Mappers.getMapper(ReadingProgressMapper.class);

    ReadingProgress toEntity(ReadingProgressDTO dto);

    ReadingProgressDTO toDTO(ReadingProgress entity);
}
