package com.stackoverflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class TagDTO {
    private String name;
    private Long questionCount;
}
