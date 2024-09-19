package com.stackoverflow.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    private String name;
    private Long questionCount;
}
