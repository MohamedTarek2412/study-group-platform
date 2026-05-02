package com.studygroup.group.service;

import com.studygroup.group.dto.CreateJoinRequestDto;
import com.studygroup.group.dto.JoinRequestDto;
import com.studygroup.group.exception.ResourceNotFoundException;
import com.studygroup.group.exception.UnauthorizedException;
import com.studygroup.group.kafka.GroupEventProducer;
import com.studygroup.group.model.Group;
import com.studygroup.group.model.GroupMember;
import com.studygroup.group.model.GroupStatus;
import com.studygroup.group.model.JoinRequest;
import com.studygroup.group.model.RequestStatus;
import com.studygroup.group.repository.GroupMemberRepository;
import com.studygroup.group.repository.GroupRepository;
import com.studygroup.group.repository.JoinRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupEventProducer groupEventProducer;

    public JoinRequestDto createJoinRequest(Long groupId, Long userId, String userName, CreateJoinRequestDto request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (group.getStatus() != GroupStatus.APPROVED) {
            throw new IllegalArgumentException("Join requests are only allowed for approved groups");
        }

        joinRequestRepository.findByGroupIdAndUserIdAndStatus(groupId, userId, RequestStatus.PENDING)
                .ifPresent(jr -> {
                    throw new IllegalArgumentException("User already has a pending request for this group");
                });

        // Check if user is already a member
        if (groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        JoinRequest joinRequest = JoinRequest.builder()
                .groupId(groupId)
                .userId(userId)
                .userName(userName)
                .message(request.getMessage())
                .status(RequestStatus.PENDING)
                .build();

        JoinRequest savedRequest = joinRequestRepository.save(joinRequest);
        log.info("Join request created for user {} to group {}", userId, groupId);
        return convertToDto(savedRequest);
    }

    public List<JoinRequestDto> getJoinRequestsForGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        return joinRequestRepository.findByGroupId(groupId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<JoinRequestDto> getPendingJoinRequestsForGroup(Long groupId, Long requesterId, boolean isAdmin) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (!isAdmin && !group.getCreatorId().equals(requesterId)) {
            throw new UnauthorizedException("Only the group creator or an admin can view join requests");
        }

        return joinRequestRepository.findByGroupIdAndStatus(groupId, RequestStatus.PENDING).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public JoinRequestDto acceptJoinRequest(Long groupId, Long requestId, Long requesterId, boolean isAdmin) {
        Group group = groupRepository.findByIdForUpdate(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (!isAdmin && !group.getCreatorId().equals(requesterId)) {
            throw new UnauthorizedException("Only the group creator can accept join requests");
        }

        JoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + requestId));

        if (!joinRequest.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("Join request does not belong to this group");
        }

        if (joinRequest.getStatus() == RequestStatus.ACCEPTED) {
            return convertToDto(joinRequest);
        }

        if (joinRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending join requests can be accepted");
        }

        // Check if group is full
        Integer currentMembers = groupMemberRepository.countByGroupId(groupId);
        if (currentMembers >= group.getMaxMembers()) {
            throw new IllegalArgumentException("Group is full");
        }

        // Add user as member
        GroupMember member = GroupMember.builder()
                .groupId(groupId)
                .userId(joinRequest.getUserId())
                .userName(joinRequest.getUserName())
                .build();
        groupMemberRepository.save(member);

        // Update join request status
        joinRequest.setStatus(RequestStatus.ACCEPTED);
        JoinRequest updatedRequest = joinRequestRepository.save(joinRequest);
        log.info("Join request {} accepted for group {}", requestId, groupId);

        // Publish event
        groupEventProducer.publishMemberJoinedEvent(groupId, joinRequest.getUserId(), 
                joinRequest.getUserName(), group.getName());

        // Reject all other pending requests from this user for this group
        rejectOtherPendingRequests(groupId, joinRequest.getUserId(), requestId);

        return convertToDto(updatedRequest);
    }

    public JoinRequestDto rejectJoinRequest(Long groupId, Long requestId, Long requesterId, boolean isAdmin) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (!isAdmin && !group.getCreatorId().equals(requesterId)) {
            throw new UnauthorizedException("Only the group creator can reject join requests");
        }

        JoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + requestId));

        if (!joinRequest.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("Join request does not belong to this group");
        }

        if (joinRequest.getStatus() == RequestStatus.REJECTED) {
            return convertToDto(joinRequest);
        }

        if (joinRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending join requests can be rejected");
        }

        joinRequest.setStatus(RequestStatus.REJECTED);
        JoinRequest updatedRequest = joinRequestRepository.save(joinRequest);
        log.info("Join request {} rejected for group {}", requestId, groupId);
        return convertToDto(updatedRequest);
    }

    private void rejectOtherPendingRequests(Long groupId, Long userId, Long excludeRequestId) {
        List<JoinRequest> otherRequests = joinRequestRepository.findByGroupIdAndStatus(groupId, RequestStatus.PENDING).stream()
                .filter(jr -> jr.getUserId().equals(userId) && !jr.getId().equals(excludeRequestId))
                .collect(Collectors.toList());

        otherRequests.forEach(jr -> {
            jr.setStatus(RequestStatus.REJECTED);
            joinRequestRepository.save(jr);
        });
    }

    private JoinRequestDto convertToDto(JoinRequest joinRequest) {
        return JoinRequestDto.builder()
                .id(joinRequest.getId())
                .groupId(joinRequest.getGroupId())
                .userId(joinRequest.getUserId())
                .userName(joinRequest.getUserName())
                .status(joinRequest.getStatus())
                .message(joinRequest.getMessage())
                .createdAt(joinRequest.getCreatedAt())
                .updatedAt(joinRequest.getUpdatedAt())
                .build();
    }
}
