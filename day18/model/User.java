package mocks.day18.model;

public record User(String userName) {
    public User {
        if(userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("userName cannot be null or empty");
        }
    }
}
