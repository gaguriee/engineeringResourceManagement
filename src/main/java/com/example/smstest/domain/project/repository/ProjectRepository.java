package com.example.smstest.domain.project.repository;

import com.example.smstest.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

//    @Query("SELECT NEW com.example.smstest.domain.support.dto.SupportSummary( " +
//            "   EXTRACT(YEAR FROM s.supportDate) AS 년도, " +
//            "   EXTRACT(MONTH FROM s.supportDate) AS 월, " +
//            "   p.name AS 제품명, " +
//            "   e.name AS 담당엔지니어, " +
//            "   st.name AS 상태, " +
//            "   COUNT(*) AS 건수) " +
//            "FROM Support s " +
//            "JOIN s.product p " +
//            "JOIN s.engineer e " +
//            "JOIN s.state st " +
//            "WHERE s.customer.id = :customerId " +
//            "GROUP BY EXTRACT(YEAR FROM s.supportDate), EXTRACT(MONTH FROM s.supportDate), p.name, e.name, st.name " +
//            "ORDER BY EXTRACT(YEAR FROM s.supportDate), EXTRACT(MONTH FROM s.supportDate), p.name, e.name, st.name")
//    List<SupportSummary> getSupportSummaryByCustomerId(@Param("customerId") Integer customerId);
//
//    @Query("SELECT c FROM Customer c JOIN Support s ON c.id = s.customer.id GROUP BY c.id ORDER BY COUNT(s.id) DESC")
//    Page<Customer> findAllBySupportCount(Pageable pageable);
//AND p.finishDate >= :currentDate
    @Query("SELECT p FROM Project p WHERE p.name LIKE %:keyword% OR p.client.name LIKE %:keyword% OR p.uniqueCode LIKE %:keyword% ORDER BY p.uniqueCode DESC ")
    Page<Project> findByNameContainingAndFinishDateAfter(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Project p ORDER BY p.uniqueCode DESC ")
    Page<Project> findAllByFinishDateAfter( Pageable pageable);
//
//    @Query("SELECT c FROM Customer c JOIN Support s ON c.id = s.customer.id WHERE c.name LIKE %:keyword% GROUP BY c.id ORDER BY COUNT(s.id) DESC")
//    List<Customer> findByNameContainingOrderBySupportCountDesc(@Param("keyword") String keyword);
//
//    @Query("SELECT c FROM Customer c JOIN Support s ON c.id = s.customer.id GROUP BY c.id ORDER BY COUNT(s.id) DESC")
//    List<Customer> findByOrderBySupportCountDesc();
//
//    Customer findOneByName(String customerName);
//
//    boolean existsByName(String name);

    Project findByName(String name);
    Project findByUniqueCode(String uniqueCode);
    boolean existsByName(String name);
    boolean existsByUniqueCode(String uniqueCode);


}
