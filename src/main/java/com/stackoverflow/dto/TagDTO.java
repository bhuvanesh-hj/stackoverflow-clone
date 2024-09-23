package com.stackoverflow.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {

    private String name;
    private int questionCount;

}
