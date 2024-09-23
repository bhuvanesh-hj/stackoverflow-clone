package com.stackoverflow.dto.comments;

import com.stackoverflow.dto.users.UserDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
public class CommentDetailsDTO {

    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO author;
    private List<CommentDetailsDTO> comments;

}
