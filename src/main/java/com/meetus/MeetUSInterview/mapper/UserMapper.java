package com.meetus.MeetUSInterview.mapper;

import com.meetus.MeetUSInterview.dto.request.auth.RegisterRequest;
import com.meetus.MeetUSInterview.dto.response.auth.UserResponse;
import com.meetus.MeetUSInterview.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);


    UserResponse toResponse(User user);
}
