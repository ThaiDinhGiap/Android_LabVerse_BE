package com.mss.prm_project.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class AddPaperRequest {
    @NonNull
    private int paperId;
}
