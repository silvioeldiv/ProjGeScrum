package com.projectinnovacion.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_updates")
public class DailyUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_scrum_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DailyScrum dailyScrum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "yesterday_work", columnDefinition = "TEXT")
    private String yesterdayWork;

    @Column(name = "today_plan", columnDefinition = "TEXT")
    private String todayPlan;

    @Column(columnDefinition = "TEXT")
    private String blockers;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}