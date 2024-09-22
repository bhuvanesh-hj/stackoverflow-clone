package com.stackoverflow.dto.answers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerRequestDTO {

    private Long Id;

    @NotBlank(message = "Answer content cannot be empty")
    @Size(min = 10, max = 5000, message = "Answer content must be between 10 and 5000 characters")
    private String body;

}
