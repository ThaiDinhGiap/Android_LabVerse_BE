// File: ResponseObject.java
package com.mss.prm_project.model; // Or wherever you keep your DTOs

import lombok.Builder;
import lombok.Data;

@Data // Generates getters, setters, toString, equals, and hashCode
@Builder // Allows easy creation using the Builder pattern
public class ResponseObject {

    private String message; // E.g., "Paper successfully removed."

    // This field can hold the actual data entity (e.g., a Paper, Collection, or a List)
    // We use Object to make it flexible for any return type.
    private Object data;
}