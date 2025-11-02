package com.meetus.MeetUSInterview.mapper;

import com.meetus.MeetUSInterview.dto.request.task.TaskCreateRequest;
import com.meetus.MeetUSInterview.dto.response.task.TaskResponse;
import com.meetus.MeetUSInterview.entity.Task;
import com.meetus.MeetUSInterview.enums.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Task toEntity(TaskCreateRequest request);


    @Mapping(target = "status", source = "status", qualifiedByName = "taskStatusToString")
    TaskResponse toResponse(Task task);


    @Named("stringToTaskStatus")
    default TaskStatus stringToTaskStatus(String statusString) {
        return TaskStatus.fromString(statusString);
    }


    @Named("taskStatusToString")
    default String taskStatusToString(TaskStatus taskStatus) {
        return taskStatus.getStringValue();
    }
}
