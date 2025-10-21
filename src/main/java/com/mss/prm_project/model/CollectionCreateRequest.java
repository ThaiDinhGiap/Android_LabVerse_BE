package com.mss.prm_project.model;

import lombok.Data;
import lombok.Builder;
import lombok.NonNull;

@Data
@Builder
public class CollectionCreateRequest {

    @NonNull
    private String name;
}
