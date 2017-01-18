package microservicecommons.interservicecommunication.exception;

import java.io.IOException;

/**
 * Created by Flo on 09/04/2016.
 * for all problems that are occuring with api communication
 */
public class ApiCommunicationException extends IOException {

    public ApiCommunicationException(String message, Exception exception) {
        super(message,exception);
    }

    public ApiCommunicationException(String message) {
        super(message);
    }
}
