package com.example.smstest.domain.support.repository;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.entity.QSupport;
import com.example.smstest.domain.support.entity.Support;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional(readOnly = true)
public class SupportRepositoryImpl implements SupportRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;

    public SupportRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    // Query DSL 필터링 메소드
    @Override
    public Page<Support> searchSupportByFilters(SupportFilterCriteria criteria, Pageable pageable, String sort) {
        QSupport support = QSupport.support;

        // 필터링 조건 추가
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(customerNameIn(criteria.getCustomerName()));
        whereClause.and(projectNameIn(criteria.getProjectName()));
        whereClause.and(teamIdIn(criteria.getTeamId()));
        whereClause.and(productIdIn(criteria.getProductId()));
        whereClause.and(issueIdIn(criteria.getIssueId()));
        whereClause.and(stateIdIn(criteria.getStateId()));
        whereClause.and(engineerIdIn(criteria.getEngineerId()));
        whereClause.and(taskContains(criteria.getTaskKeyword()));

        if (criteria.getStartDate() != null) {
            whereClause.and(support.supportDate.goe(criteria.getStartDate()));
        }

        if (criteria.getEndDate() != null) {
            whereClause.and(support.supportDate.loe(criteria.getEndDate()));
        }

        // 정렬 조건 추가
        OrderSpecifier<?> orderSpecifier = null;
        if ("asc".equalsIgnoreCase(sort)) {
            orderSpecifier = support.supportDate.asc();
        } else {
            orderSpecifier = support.supportDate.desc();
        }

        // 필터링 결과 객체
        List<Support> result = queryFactory
                .selectFrom(support)
                .leftJoin(support.project).fetchJoin()
                .leftJoin(support.product).fetchJoin()
                .leftJoin(support.issue).fetchJoin()
                .leftJoin(support.state).fetchJoin()
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier, support.id.desc()) // 정렬 조건 추가
                .fetch();

        long totalCount = queryFactory
                .select(support.count())
                .from(support)
                .where(whereClause)
                .fetch().get(0);

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression customerNameIn(String customerName) {
        return customerName != null && !customerName.isEmpty() ? QSupport.support.project.client.name.contains(customerName) : null;
    }

    private BooleanExpression projectNameIn(String projectName) {
        return projectName != null && !projectName.isEmpty() ? QSupport.support.project.name.contains(projectName) : null;
    }


    private BooleanExpression teamIdIn(List<Integer> teamIds) {
        return teamIds != null && !teamIds.isEmpty() ? QSupport.support.engineer.team.id.in(teamIds) : null;
    }

    private BooleanExpression productIdIn(List<Long> productIds) {
        return productIds != null && !productIds.isEmpty() ? QSupport.support.product.id.in(productIds) : null;
    }

    private BooleanExpression issueIdIn(List<Long> issueIds) {
        return issueIds != null && !issueIds.isEmpty() ? QSupport.support.issue.id.in(issueIds) : null;
    }

    private BooleanExpression stateIdIn(List<Long> stateIds) {
        return stateIds != null && !stateIds.isEmpty() ? QSupport.support.state.id.in(stateIds) : null;
    }

    private BooleanExpression engineerIdIn(List<Long> engineerIds) {
        return engineerIds != null && !engineerIds.isEmpty() ? QSupport.support.engineer.id.in(engineerIds) : null;
    }

    private BooleanExpression taskContains(String keyword) {
        return keyword != null
                ? QSupport.support.taskTitle.toLowerCase().contains(keyword.toLowerCase())
                .or(QSupport.support.taskSummary.toLowerCase().contains(keyword.toLowerCase()))
                .or(QSupport.support.engineer.name.toLowerCase().contains(keyword.toLowerCase()))
                : null;
    }

}
