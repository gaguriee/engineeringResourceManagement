package com.example.smstest.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * File 테이블과 상호작용
 */
public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * [ 지원내역 삭제 시 함께 등록된 모든 File 삭제 ]
     * @param supportId
     */
    void deleteAllBySupportId(Long supportId);

    /**
     * [ 일정 삭제 시 함께 등록된 모든 File 삭제 ]
     * @param taskId
     */
    void deleteAllByTaskId(Long taskId);

}