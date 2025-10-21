package com.mss.prm_project.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionMemberId implements Serializable {

    private int collectionId;
    private int userId;

    // equals va hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionMemberId that = (CollectionMemberId) o;
        return collectionId == that.collectionId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, userId);
    }
}