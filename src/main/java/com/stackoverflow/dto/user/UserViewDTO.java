package com.stackoverflow.dto.user;

import com.stackoverflow.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserViewDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private Long questionCount;

    private Long answerCount;


}
