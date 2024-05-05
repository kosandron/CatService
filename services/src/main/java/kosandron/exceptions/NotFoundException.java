package kosandron.exceptions;

public class NotFoundException extends RuntimeException {
    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException catNotFound(Long id) {
        return new NotFoundException("Cat with id %s was not found!".formatted(id.toString()));
    }

    public static NotFoundException userNotFound(String login) {
        return new NotFoundException("User with login %s was not found!".formatted(login));
    }

    public static NotFoundException ownerNotFound(Long id) {
        return new NotFoundException("Owner with id %s was not found!".formatted(id.toString()));
    }
}
