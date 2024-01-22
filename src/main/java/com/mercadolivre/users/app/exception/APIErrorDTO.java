package com.mercadolivre.users.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class APIErrorDTO {

    private String code;
    private Map<String, String> message;

}
