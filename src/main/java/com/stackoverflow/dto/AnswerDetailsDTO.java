package com.stackoverflow.dto;

import com.stackoverflow.entity.Comment;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AnswerDetailsDTO {

    private Long Id;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Question question;

    private Set<Comment> comments = new HashSet<>();

    private User author;

}
