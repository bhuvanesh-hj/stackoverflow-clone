package com.stackoverflow.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserViewDTO {
    private String firstName;

    private String lastName;

    private Long questionCount;

    private Long answerCount;
}
