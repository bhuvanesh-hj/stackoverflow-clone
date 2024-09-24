package com.stackoverflow.dto.users;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String profilePicture;
    private Integer reputations;

}
