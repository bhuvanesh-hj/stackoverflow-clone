package com.stackoverflow.dto.comments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentRequestDTO {

    @NotNull(message = "Comment text cannot be null")
    @Size(min = 1, max = 1000, message = "Comment text must be between 1 and 1000 characters")
    private String comment;

}
