package com.stackoverflow.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserDetailsDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String profilePicture;

}
