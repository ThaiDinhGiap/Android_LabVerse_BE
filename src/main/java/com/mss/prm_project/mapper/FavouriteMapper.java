package com.mss.prm_project.mapper;

import com.mss.prm_project.dto.FavoritePaperDTO;
import com.mss.prm_project.entity.FavoritePaper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses =  {UserMapper.class, PaperMapper.class})
public interface FavouriteMapper {
    FavouriteMapper INSTANCE = Mappers.getMapper(FavouriteMapper.class);

    FavoritePaperDTO toDTO(FavoritePaper favoritePaper);

    FavoritePaper toEntity(FavoritePaperDTO dto);
}
