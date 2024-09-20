package com.stackoverflow.dto.user;

import com.stackoverflow.dto.QuestionDetailsDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Setter
@Getter
@ToString
public class UserDetailsDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

}
