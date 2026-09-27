package com.fintrack.fintrackapi.expenses.repository;

import com.fintrack.fintrackapi.expenses.model.SharedExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SharedExpenseRepository extends JpaRepository<SharedExpenseEntity, UUID> {

    @Query("""
            select distinct expense from SharedExpenseEntity expense
            left join expense.participants participant
            where expense.creatorId = :userId or participant.userId = :userId
            order by expense.createdAt desc
            """)
    List<SharedExpenseEntity> findAllInvolvingUser(@Param("userId") String userId);
}
