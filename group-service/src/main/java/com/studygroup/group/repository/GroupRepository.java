package com.studygroup.group.repository;

import com.studygroup.group.model.Group;
import com.studygroup.group.model.GroupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreatorId(Long creatorId);

    List<Group> findByStatus(GroupStatus status);

    Page<Group> findByStatus(GroupStatus status, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE g.status = 'APPROVED' AND " +
           "(LOWER(g.subject) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "LOWER(g.location) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    Page<Group> searchGroups(@Param("searchQuery") String searchQuery, Pageable pageable);

    List<Group> findByStatusAndCreatorId(GroupStatus status, Long creatorId);
}
