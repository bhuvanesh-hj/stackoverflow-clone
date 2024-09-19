package com.stackoverflow.dto;

import com.stackoverflow.dto.user.UserDTO;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter@ToString
public class AnswerDetailsDTO {

    private Long Id;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<CommentDetailsDTO> comments = new HashSet<>();

    private UserDTO author;

    private Integer upvotes = 0;

    private Integer downvotes = 0;

}
