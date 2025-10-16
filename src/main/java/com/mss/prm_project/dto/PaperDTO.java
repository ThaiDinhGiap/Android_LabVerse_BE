package com.mss.prm_project.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PaperDTO extends BaseDTO{
    String title;
    String author;
    String journal;
    String publisher;
    LocalDateTime publishDate;
    boolean isOffline;
    int priority;
    FileDTO file;
}
