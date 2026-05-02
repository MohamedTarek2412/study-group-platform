package com.studygroup.auth.mapper;

import com.studygroup.auth.dto.UserDTO;
import com.studygroup.auth.model.Role;
import com.studygroup.auth.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-02T02:06:30+0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        if ( user.getRole() != null ) {
            userDTO.role( user.getRole().name() );
        }
        userDTO.email( user.getEmail() );
        userDTO.id( user.getId() );

        return userDTO.build();
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        if ( userDTO.getRole() != null ) {
            user.setRole( Enum.valueOf( Role.class, userDTO.getRole() ) );
        }
        user.setEmail( userDTO.getEmail() );
        user.setId( userDTO.getId() );

        return user;
    }
}
