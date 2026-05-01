package com.studygroup.group.service;

import com.studygroup.group.dto.CreateGroupRequest;
import com.studygroup.group.dto.GroupDto;
import com.studygroup.group.exception.ResourceNotFoundException;
import com.studygroup.group.exception.UnauthorizedException;
import com.studygroup.group.kafka.GroupEventProducer;
import com.studygroup.group.model.Group;
import com.studygroup.group.model.GroupMember;
import com.studygroup.group.model.GroupStatus;
import com.studygroup.group.repository.GroupMemberRepository;
import com.studygroup.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupEventProducer groupEventProducer;

    public GroupDto createGroup(Long creatorId, String creatorName, CreateGroupRequest request) {
        Group group = Group.builder()
                .name(request.getName())
                .subject(request.getSubject())
                .description(request.getDescription())
                .creatorId(creatorId)
                .creatorName(creatorName)
                .maxMembers(request.getMaxMembers())
                .meetingType(request.getMeetingType())
                .meetingSchedule(request.getMeetingSchedule())
                .location(request.getLocation())
                .status(GroupStatus.PENDING)
                .build();

        Group savedGroup = groupRepository.save(group);
        log.info("Group created with id: {}, creator: {}", savedGroup.getId(), creatorId);

        // Add creator as first member
        GroupMember member = GroupMember.builder()
                .groupId(savedGroup.getId())
                .userId(creatorId)
                .userName(creatorName)
                .build();
        groupMemberRepository.save(member);

        // Publish event
        groupEventProducer.publishGroupCreatedEvent(savedGroup.getId(), creatorId, creatorName, savedGroup.getName());

        return convertToDto(savedGroup);
    }

    public GroupDto getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
        return convertToDto(group);
    }

    public Page<GroupDto> getApprovedGroups(Pageable pageable) {
        return groupRepository.findByStatus(GroupStatus.APPROVED, pageable)
                .map(this::convertToDto);
    }

    public Page<GroupDto> searchGroups(String searchQuery, Pageable pageable) {
        return groupRepository.searchGroups(searchQuery, pageable)
                .map(this::convertToDto);
    }

    public Page<GroupDto> getPendingGroups(Pageable pageable) {
        return groupRepository.findByStatus(GroupStatus.PENDING, pageable)
                .map(this::convertToDto);
    }

    public List<GroupDto> getGroupsByCreator(Long creatorId) {
        return groupRepository.findByCreatorId(creatorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public GroupDto updateGroup(Long groupId, Long creatorId, CreateGroupRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (!group.getCreatorId().equals(creatorId)) {
            throw new UnauthorizedException("Only the group creator can update this group");
        }

        group.setName(request.getName());
        group.setSubject(request.getSubject());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        group.setMeetingType(request.getMeetingType());
        group.setMeetingSchedule(request.getMeetingSchedule());
        group.setLocation(request.getLocation());

        Group updatedGroup = groupRepository.save(group);
        log.info("Group updated with id: {}", groupId);
        return convertToDto(updatedGroup);
    }

    public void deleteGroup(Long groupId, Long creatorId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (!group.getCreatorId().equals(creatorId)) {
            throw new UnauthorizedException("Only the group creator can delete this group");
        }

        // Delete all members
        groupMemberRepository.deleteInBatch(groupMemberRepository.findByGroupId(groupId));

        groupRepository.deleteById(groupId);
        log.info("Group deleted with id: {}", groupId);
    }

    public GroupDto approveGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        group.setStatus(GroupStatus.APPROVED);
        Group updatedGroup = groupRepository.save(group);
        log.info("Group approved with id: {}", groupId);

        // Publish event
        groupEventProducer.publishGroupApprovedEvent(groupId, group.getName());

        return convertToDto(updatedGroup);
    }

    public GroupDto rejectGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        group.setStatus(GroupStatus.REJECTED);
        Group updatedGroup = groupRepository.save(group);
        log.info("Group rejected with id: {}", groupId);

        // Publish event
        groupEventProducer.publishGroupRejectedEvent(groupId, group.getName());

        return convertToDto(updatedGroup);
    }

    public List<GroupDto> getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        return groupMemberRepository.findByGroupId(groupId).stream()
                .map(member -> GroupDto.builder()
                        .id(member.getId())
                        .name(member.getUserName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private GroupDto convertToDto(Group group) {
        Integer memberCount = groupMemberRepository.countByGroupId(group.getId());
        return GroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .subject(group.getSubject())
                .description(group.getDescription())
                .creatorId(group.getCreatorId())
                .creatorName(group.getCreatorName())
                .maxMembers(group.getMaxMembers())
                .currentMembers(memberCount)
                .meetingType(group.getMeetingType())
                .meetingSchedule(group.getMeetingSchedule())
                .location(group.getLocation())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
