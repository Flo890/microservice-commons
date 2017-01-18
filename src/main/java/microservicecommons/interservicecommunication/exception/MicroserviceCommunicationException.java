package microservicecommons.interservicecommunication.exception;

/**
 * Created by Flo on 07/05/2016.
 */
public class MicroserviceCommunicationException extends RuntimeException {

    public MicroserviceCommunicationException(String message, Exception exception) {
        super(message,exception);
    }

    public MicroserviceCommunicationException(String message) {
        super(message);
    }
}
