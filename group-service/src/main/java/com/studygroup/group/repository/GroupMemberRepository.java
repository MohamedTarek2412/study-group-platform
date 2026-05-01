package com.studygroup.group.repository;

import com.studygroup.group.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupId(Long groupId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    Integer countByGroupId(Long groupId);

    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
