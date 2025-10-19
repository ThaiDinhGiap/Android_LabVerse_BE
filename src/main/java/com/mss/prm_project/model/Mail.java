package com.mss.prm_project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mail {
    private String[] mailTo;
    private String mailSubject;
    private String mailContent;
}
