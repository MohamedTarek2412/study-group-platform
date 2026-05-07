package com.studygroup.discussion.aspect;

import com.studygroup.discussion.exception.ForbiddenException;
import com.studygroup.discussion.repository.GroupMemberCacheRepository;
import com.studygroup.discussion.security.AuthenticatedUser;
import com.studygroup.discussion.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipCheckAspect {

    private final GroupMemberCacheRepository groupMemberCacheRepository;

    /**
     * Intercepts any method annotated with @RequireMembership.
     * Expects the FIRST parameter of the annotated method to be the groupId (Long).
     */
    @Before("@annotation(com.studygroup.discussion.aspect.RequireMembership)")
    public void checkMembership(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || !(args[0] instanceof Long groupId)) {
            log.warn("@RequireMembership used on method without Long groupId as first parameter: {}",
                    joinPoint.getSignature().getName());
            return;
        }

        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ForbiddenException("Authentication required");
        }

        // Admins bypass membership check
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }

        boolean isMember = groupMemberCacheRepository.existsByGroupIdAndUserId(groupId, currentUser.getUserId());
        if (!isMember) {
            log.warn("User {} attempted to access group {} without membership",
                    currentUser.getUserId(), groupId);
            throw new ForbiddenException("You must be a member of this group to perform this action");
        }
    }
}
