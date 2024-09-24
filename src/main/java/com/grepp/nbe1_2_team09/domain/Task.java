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

    //비즈니스 메서드

    @Builder
    public Task(String taskName, Group group) {
        this.taskName = taskName;
        this.group = group;
    }

    public void updateTaskName(String newName){
        this.taskName = newName;
    }

    public void check(){
        this.isCompleted = true;
    }
    public void uncheck(){
        this.isCompleted = false;
    }
}
