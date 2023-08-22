package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom  {
    List<Support> findByTaskTitleContainingOrTaskSummaryContainingIgnoreCase(String keyword, String keyword2);

    List<Support> findByIssueAndStateAndProductAndCustomer(Issue issue, State state, Product product, Customer customer);
    List<Support> findByEngineerId(Long engineerId);
    List<Support> findByTeamId(Integer teamId);

    Long countByEngineerId(Long engineerId);

    Long countByTeamIdAndIssueId(Integer engineerId, Long issueId);
    Long countByTeamIdAndStateId(Integer engineerId, Long issueId);
    Long countByTeamIdAndProductId(Integer engineerId, Long issueId);

    Page<Support> findAllByEngineerIdAndCustomerIdAndProductIdAndStateId(Long engineerId, Long customerId, Long productId, Long stateId, Pageable pageable);

    @Query("SELECT s.customer.id, s.product.id, s.state.id, SUM(s.supportTypeHour) " +
            "FROM Support s " +
            "WHERE s.engineer.id = :engineerId " +
            "GROUP BY s.customer.id, s.product.id, s.state.id")
    List<Object[]> countAttributesByEngineer(@Param("engineerId") Long engineerId);

}