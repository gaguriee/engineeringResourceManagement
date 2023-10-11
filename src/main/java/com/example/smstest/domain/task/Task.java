package com.example.smstest.domain.task;

import com.example.smstest.domain.project.entity.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TaskCategory category;

    @Column(name = "estimated_start_date")
    @Temporal(TemporalType.DATE)
    private Date estimatedStartDate;

    @Column(name = "estimated_end_date")
    @Temporal(TemporalType.DATE)
    private Date estimatedEndDate;

    @Column(name = "actual_start_date")
    @Temporal(TemporalType.DATE)
    private Date actualStartDate;

    @Column(name = "actual_end_date")
    @Temporal(TemporalType.DATE)
    private Date actualEndDate;

    @Column(name = "actual_output")
    private String actualOutput;

    @Column(name = "name")
    private String taskName;

    @Builder
    public Task(Project project, TaskCategory category, Date estimatedStartDate, Date estimatedEndDate, Date actualStartDate, Date actualEndDate, String actualOutput, String taskName) {
        this.project = project;
        this.category = category;
        this.estimatedStartDate = estimatedStartDate;
        this.estimatedEndDate = estimatedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.actualOutput = actualOutput;
        this.taskName = taskName;
    }
}
