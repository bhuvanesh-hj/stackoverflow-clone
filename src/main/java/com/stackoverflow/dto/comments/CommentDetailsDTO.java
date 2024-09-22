package com.stackoverflow.dto.comments;

import com.stackoverflow.dto.users.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class CommentDetailsDTO {

    private Long id;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserDTO author;

    private List<CommentDetailsDTO> comments;

}
