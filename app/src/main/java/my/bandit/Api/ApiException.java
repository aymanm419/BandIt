package my.bandit.Api;

public class ApiException extends Exception {
    private String message;

    public ApiException(String message) {
        this.message = message;
    }

    public ApiException() {
    }
}
