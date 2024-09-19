package com.stackoverflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequestDTO {

    private Long Id;

    @NotBlank(message = "Answer content cannot be empty")
    @Size(min = 10, max = 5000, message = "Answer content must be between 10 and 5000 characters")
    private String body;

}