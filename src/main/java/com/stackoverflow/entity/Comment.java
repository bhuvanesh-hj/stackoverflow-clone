package com.stackoverflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "comments")
public class Comment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "comment_id")
        private Long id;

        @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
        private String commentText;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "question_id")
        private Question question;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "answer_id")
        private Answer answer;
}


