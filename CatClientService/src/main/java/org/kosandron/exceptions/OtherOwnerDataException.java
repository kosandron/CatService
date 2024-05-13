package org.kosandron.exceptions;

public class OtherOwnerDataException extends RuntimeException {
    public OtherOwnerDataException() {
        super("This data of other user");
    }
}
