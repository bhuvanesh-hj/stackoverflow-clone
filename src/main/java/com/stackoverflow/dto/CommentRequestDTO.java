package com.stackoverflow.dto;

import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentRequestDTO {


    @NotNull(message = "Comment text cannot be null")
    @Size(min = 1, max = 1000, message = "Comment text must be between 1 and 1000 characters")
    private String commentText;

    @NotNull(message = "Question cannot be null")
    private Question question;

    @NotNull(message = "Answer cannot be null")
    private Answer answer;

}
