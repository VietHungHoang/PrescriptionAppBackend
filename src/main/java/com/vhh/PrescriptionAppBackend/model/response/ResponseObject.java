package com.vhh.PrescriptionAppBackend.model.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseObject<T> {
    private String message;
    private HttpStatus status;

    private T data;
}
