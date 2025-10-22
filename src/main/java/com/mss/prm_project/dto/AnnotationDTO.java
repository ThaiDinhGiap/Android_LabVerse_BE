package com.mss.prm_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class AnnotationDTO extends BaseDTO{
    String annotationName;
    String annotationUrl;
    UserDTO ownerDTO;
    Set<UserDTO> readerDTO;
    PaperDTO paperDTO;
}
