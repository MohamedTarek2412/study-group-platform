package com.studygroup.discussion.repository;

import com.studygroup.discussion.model.GroupMemberCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberCacheRepository extends JpaRepository<GroupMemberCache, Long> {
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    Optional<GroupMemberCache> findByGroupIdAndUserId(Long groupId, Long userId);
    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
