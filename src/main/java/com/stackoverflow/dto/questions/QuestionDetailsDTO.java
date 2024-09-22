package com.stackoverflow.dto.questions;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.comments.CommentDetailsDTO;
import com.stackoverflow.dto.TagDTO;
import com.stackoverflow.dto.users.UserDTO;
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
    boolean upvoted = false;
    boolean downvoted = false;
    private Boolean isSaved = false;
    private Long id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer upvotes = 0;
    private Integer downvotes = 0;
    private Integer answersCount = 0;
    private Integer views;
    private UserDTO author;
    private List<TagDTO> tags;
    private Set<AnswerDetailsDTO> answers;
    private Set<CommentDetailsDTO> comments;
}

