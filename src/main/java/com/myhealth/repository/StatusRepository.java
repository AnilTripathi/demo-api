package com.myhealth.repository;

import com.myhealth.entity.task.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Short> {
    List<Status> findByIsDone(Boolean isDone);
}