package com.stackoverflow.dto;

import com.stackoverflow.dto.user.UserDTO;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentDetailsDTO {

    private String commentText;

    private Question question;

    private Answer answer;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserDTO author;
}