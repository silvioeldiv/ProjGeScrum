package com.projectinnovacion.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sprint_reviews")
public class SprintReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Sprint sprint;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "demo_notes", columnDefinition = "TEXT")
    private String demoNotes;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "client_comments", columnDefinition = "TEXT")
    private String clientComments;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "sprint_review_attendees", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "attendee_email")
    private Set<String> attendees = new HashSet<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "sprint_review_deliverables", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "deliverable_url")
    private Set<String> deliverableUrls = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}