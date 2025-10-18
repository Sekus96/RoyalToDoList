package com.recruitment.mapper;

import com.recruitment.dto.UserRequest;
import com.recruitment.dto.UserResponse;
import com.recruitment.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entity and User DTOs.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the User entity
     * @return the UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        return response;
    }

    /**
     * Converts a UserRequest DTO to a User entity.
     *
     * @param request the UserRequest DTO
     * @return the User entity
     */
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        return user;
    }
}
