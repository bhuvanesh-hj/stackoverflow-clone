package com.stackoverflow.dto;

import com.stackoverflow.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class QuestionDetailsDTO {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer upvotes = 0;
    private Integer downvotes = 0;
    private Integer answersCount = 0;
    private Integer views;

    private UserDTO authorDTO;

    private List<TagDTO> tags;
    private Set<AnswerDetailsDTO> answers;
    private Set<CommentDetailsDTO> comments;
}

