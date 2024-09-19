package com.stackoverflow.dto;

import com.stackoverflow.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class AnswerDetailsDTO {

    boolean upvoted = false;
    boolean downvoted = false;
    private Long Id;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<CommentDetailsDTO> comments = new HashSet<>();
    private UserDTO author;
    private Integer upvotes = 0;
    private Integer downvotes = 0;

}
