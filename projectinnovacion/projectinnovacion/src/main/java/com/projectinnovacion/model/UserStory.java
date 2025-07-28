    package com.projectinnovacion.model;

    import com.projectinnovacion.model.enums.StoryPriority;
    import com.projectinnovacion.model.enums.StoryStatus;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "user_stories")
    public class UserStory {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 200)
        private String title;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Column(name = "acceptance_criteria", columnDefinition = "TEXT")
        private String acceptanceCriteria;

        @Column(name = "story_points")
        private Integer storyPoints;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private StoryPriority priority;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private StoryStatus status;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "project_id", nullable = false)
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Project project;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "assignee_id")
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private User assignee;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "reporter_id", nullable = false)
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private User reporter;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "sprint_id")
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Sprint sprint;

        @Column(name = "order_index")
        private Integer orderIndex;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Column(name = "completed_at")
        private LocalDateTime completedAt;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
            if (status == null) {
                status = StoryStatus.BACKLOG;
            }
            if (priority == null) {
                priority = StoryPriority.MEDIUM;
            }
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
            if (status == StoryStatus.DONE && completedAt == null) {
                completedAt = LocalDateTime.now();
            }
        }
    }
