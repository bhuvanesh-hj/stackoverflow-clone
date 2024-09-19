package com.stackoverflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "answers")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(cascade = {
            CascadeType.ALL})
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();


    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerVote> answerVote = new ArrayList<>();

    public Answer(Long answerId) {
        super();
    }
}
