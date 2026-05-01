package com.studygroup.group.repository;

import com.studygroup.group.model.JoinRequest;
import com.studygroup.group.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    List<JoinRequest> findByGroupIdAndStatus(Long groupId, RequestStatus status);

    List<JoinRequest> findByGroupId(Long groupId);

    Optional<JoinRequest> findByGroupIdAndUserId(Long groupId, Long userId);

    List<JoinRequest> findByUserId(Long userId);
}
