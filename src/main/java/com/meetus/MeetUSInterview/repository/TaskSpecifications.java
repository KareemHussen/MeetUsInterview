package com.meetus.MeetUSInterview.repository;

import com.meetus.MeetUSInterview.dto.request.task.TaskSearchRequest;
import com.meetus.MeetUSInterview.entity.Task;
import com.meetus.MeetUSInterview.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;


public class TaskSpecifications {

    private TaskSpecifications() {
    }


    public static Specification<Task> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() : cb.equal(root.get("userId"), userId);
    }


    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }


    public static Specification<Task> hasDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) return cb.conjunction();

            if (fromDate != null && toDate != null) {
                return cb.between(root.get("createdAt"), fromDate, toDate);
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
            } else {
                return cb.lessThanOrEqualTo(root.get("createdAt"), toDate);
            }
        };
    }


    public static Specification<Task> searchByTitleOrDescription(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            
            String searchPattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), searchPattern),
                cb.like(cb.lower(root.get("description")), searchPattern)
            );
        };
    }

    public static Specification<Task> build(Long userId, TaskSearchRequest request) {
        Specification<Task> spec = Specification.unrestricted();

        spec = spec.and(hasUserId(userId));

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            spec = spec.and(hasStatus(TaskStatus.fromString(request.getStatus())));
        }

        spec = spec.and(hasDateRange(request.getFromDate(), request.getToDate()));

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            spec = spec.and(searchByTitleOrDescription(request.getSearch()));
        }

        return spec;
    }
}
