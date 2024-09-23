package com.stackoverflow.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserViewDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private Long questionCount;
    private Long answerCount;
    private String profilePicture;

}
