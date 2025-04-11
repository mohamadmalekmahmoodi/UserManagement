package ir.useronlinemanagement.controller.response;

public class UpdateUserResponse {
    private String message;
    public UpdateUserResponse(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
