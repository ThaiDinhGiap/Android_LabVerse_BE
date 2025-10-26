package com.mss.prm_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ReadingProgressDTO {
    UserDTO user;
    PaperDTO paper;
    int latestPage;
    String progressStatus;
    BigDecimal completionPercent;
}
