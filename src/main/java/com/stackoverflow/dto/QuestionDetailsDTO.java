package com.stackoverflow.dto;

import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class QuestionDetailsDTO {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer upvotes;
    private Integer downvotes;
    private Integer answersCount;
    private Integer views;

    private UserDTO authorDTO;

    private List<TagDTO> tags;
    private Set<Answer> answers;
    private Set<Comment> comments;
}

