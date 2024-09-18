package com.stackoverflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class QuestionRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 255, message = "Title must be between 10 and 255 characters")
    private String title;


    @NotBlank(message = "Body is required")
    @Size(min = 20, message = "Body must contain at least 20 characters")
    private String body;

    private Set<String> tagsList;

}
