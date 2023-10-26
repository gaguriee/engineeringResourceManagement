package com.example.smstest.domain.support.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> deleteAllBySupportId(Long supportId);

}