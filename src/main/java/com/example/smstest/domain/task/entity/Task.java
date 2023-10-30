package com.example.smstest.domain.task.entity;

import com.example.smstest.domain.wbs.entity.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


/**
 * WBS 작업 일정 Entity
 */
@Entity
@Getter
@Setter
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

    private Double progress;

    public Double getProgress() {
        if (actualStartDate != null && actualEndDate != null) {
            long totalDuration = estimatedEndDate.getTime() - estimatedStartDate.getTime();
            long actualDuration = actualEndDate.getTime() - actualStartDate.getTime();
            double progress = (double) actualDuration / totalDuration * 100;
            progress = Math.round(progress * 10.0) / 10.0; // 반올림하여 소수점 한 자리까지 표시
            return progress;
        } else {
            return null;
        }
    }

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
