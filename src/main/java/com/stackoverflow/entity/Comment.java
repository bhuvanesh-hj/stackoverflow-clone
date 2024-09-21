package com.stackoverflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment extends BaseEntity{

        @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
        private String comment;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "question_id")
        private Question question;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "answer_id")
        private Answer answer;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "author_id", nullable = false)
        private User author;

        @Column(name = "created_at", updatable = false, nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @ManyToOne
        @JoinColumn(name = "parent_id")
        private Comment parentComment;

        @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
        private List<Comment> comments;

}


