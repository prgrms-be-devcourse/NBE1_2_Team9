package com.grepp.nbe1_2_team09.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_tb")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 100)
    private String taskName;

    @Column(nullable = false)
    private boolean isCompleted = false;
}
