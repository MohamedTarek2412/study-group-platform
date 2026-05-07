package com.studygroup.discussion.repository;

import com.studygroup.discussion.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Page<Material> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);
    long countByGroupId(Long groupId);
}
