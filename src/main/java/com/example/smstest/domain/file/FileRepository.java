package com.example.smstest.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * File 테이블과 상호작용
 */
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> deleteAllBySupportId(Long supportId);
    void deleteAllByTaskId(Long taskId);

}