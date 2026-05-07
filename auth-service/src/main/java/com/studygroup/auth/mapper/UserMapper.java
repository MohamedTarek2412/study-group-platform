package com.studygroup.auth.mapper;

import com.studygroup.auth.dto.UserDTO;
import com.studygroup.auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for User entity to DTO conversions.
 *
 * <p>Handles bidirectional mapping between {@link User} and {@link UserDTO}.
 * Configuration: componentModel = "spring" allows Spring to inject this as a bean.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convert User JPA entity to UserDTO.
     * Role enum is converted to its string representation.
     */
    @Mapping(source = "role", target = "role")
    UserDTO toDto(User user);

    /**
     * Convert UserDTO to User JPA entity.
     * Role string is expected to be a valid Role enum value.
     */
    @Mapping(source = "role", target = "role")
    User toEntity(UserDTO userDTO);
}
