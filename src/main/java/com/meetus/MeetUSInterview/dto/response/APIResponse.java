package com.meetus.MeetUSInterview.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
    
    private boolean success;
    private String message;
    private T body;
    private Object errors;
    

    public static <T> APIResponse<T> success(T body, String message) {
        return APIResponse.<T>builder()
                .success(true)
                .message(message)
                .body(body)
                .errors(null)
                .build();
    }
    

    public static <T> APIResponse<T> success(T body) {
        return success(body, "Operation completed successfully");
    }
    

    public static <T> APIResponse<T> error(String message, Object errors) {
        return APIResponse.<T>builder()
                .success(false)
                .message(message)
                .body(null)
                .errors(errors)
                .build();
    }
    
    
    public static <T> APIResponse<T> error(String message) {
        return error(message, null);
    }
}
