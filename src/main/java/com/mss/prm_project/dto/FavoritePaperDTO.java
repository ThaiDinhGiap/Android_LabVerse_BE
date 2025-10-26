package com.mss.prm_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FavoritePaperDTO extends BaseDTO{
    UserDTO user;
    PaperDTO paper;
}
