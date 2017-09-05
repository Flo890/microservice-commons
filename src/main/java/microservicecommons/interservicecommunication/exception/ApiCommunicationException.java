package microservicecommons.interservicecommunication.exception;

import java.io.IOException;

/**
 * Created by Flo on 09/04/2016.
 * for all problems that are occuring with api communication
 */
public class ApiCommunicationException extends IOException {

    public enum ProblemReason {

        CONNECTION_IMPOSSIBLE(true), SERVER_UNABLE(false), SERVER_ERROR(true), CLIENT_MISTAKE(true), UNKNOWN(true);
        boolean unexpectedProblem;

        ProblemReason(boolean isUnexpectedProblem) {
            unexpectedProblem = isUnexpectedProblem;
        }

        public boolean isUnexpectedProblem() {
            return unexpectedProblem;
        }
    }

    private final ProblemReason problemReason;

    public ApiCommunicationException(String message, Exception exception, ProblemReason problemReason) {
        super(message,exception);
        this.problemReason = problemReason;
    }

    public ApiCommunicationException(String message, ProblemReason problemReason) {
        super(message);
        this.problemReason = problemReason;
    }

    public ApiCommunicationException(String message) {
        super(message);
        this.problemReason = ProblemReason.UNKNOWN;
    }

    public ProblemReason getProblemReason() {
        return problemReason;
    }
}
