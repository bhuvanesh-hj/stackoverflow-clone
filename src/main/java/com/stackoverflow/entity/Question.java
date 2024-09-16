package com.stackoverflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "questions")
public class Question {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int votes;

    // ManyToOne relationship with User (author)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "author_id", nullable = false)
//    private User author;
//
//    // OneToMany relationship with Answer
//    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Answer> answers;
//
//    // ManyToMany relationship with Tag
//    @ManyToMany
//    @JoinTable(
//            name = "question_tags",
//            joinColumns = @JoinColumn(name = "question_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id")
  //  )
//    private Set<Tag> tags;

}
