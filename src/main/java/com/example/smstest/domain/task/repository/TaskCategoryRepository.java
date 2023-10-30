package com.example.smstest.domain.task.repository;

import com.example.smstest.domain.task.entity.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Integer> {

}
