package org.dozie.common.model.response;

import java.util.Random;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public class ApiResponse {
    
    @Schema(description = "Response data payload", example = "{\"token\": \"jwt_token_here\", \"user\": {\"id\": 1, \"username\": \"john.doe\"}}")
    private Object data;
    
    @Schema(description = "Response message", example = "Login successful")
    private String message;
    
    @Schema(description = "Response code", example = "123")
    private int code;

    public ApiResponse(Object data, String message) {
        this.data = data;
        this.message = message;
        this.code = new Random().nextInt(1000); // Generate a random code
    }

    public ApiResponse(String message) {
        this(null, message);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
} 