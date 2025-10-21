package com.mss.prm_project.mapper;

import com.mss.prm_project.dto.FileDTO;
import com.mss.prm_project.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {PaperMapper.class})
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);
    FileDTO toDTO(File file);
    File toEntity(FileDTO dto);
}
