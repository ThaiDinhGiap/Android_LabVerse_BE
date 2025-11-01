package com.mss.prm_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class AnnotationDTO extends BaseDTO{
    String annotationName;
    String annotationUrl;
    UserDTO ownerDTO;
    List<UserDTO> readerDTO;
    PaperDTO paperDTO;
}
