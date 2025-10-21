package com.mss.prm_project.model;


import com.mss.prm_project.entity.CollectionMember;
import lombok.Data;

@Data
public class CollectionResponse {

        private int collectionId;
        private String name;
        private String owner;
        private int paperCount;
        private int memberCount;
}
