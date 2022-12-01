package com.task.test.footballmanager.exception.handler;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

interface ApiSubError {

}

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
class ApiValidationError implements ApiSubError {
    private List<Object> object;
    private String field;
    private Object rejectedValue;
    private String message;

    ApiValidationError(List<Object> object, String message) {
        this.object = object;
        this.message = message;
    }
}
