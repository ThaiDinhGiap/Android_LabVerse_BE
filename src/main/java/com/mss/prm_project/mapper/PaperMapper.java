package com.mss.prm_project.mapper;

import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses =  {UserMapper.class, FileMapper.class, CollectionMapper.class})
public interface PaperMapper {
    PaperMapper INSTANCE = Mappers.getMapper(PaperMapper.class);

    PaperDTO toDTO(Paper paper);
    Paper toEntity(PaperDTO dto);
}
