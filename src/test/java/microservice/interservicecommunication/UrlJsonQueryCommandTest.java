package microservice.interservicecommunication;

import microservicecommons.interservicecommunication.UrlJsonQueryCommand;
import microservicecommons.interservicecommunication.exception.ApiCommunicationException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UrlJsonQueryCommandTest {

    class Response{
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }

    /**
     * tries to query a resource that does not exist, and checks if the right problem reason is set in the thrown ApiCommunicationException
     * @throws MalformedURLException
     */
    @Test
    public void shouldThrowExceptionWithReasonConnectionProblem() throws MalformedURLException {
        UrlJsonQueryCommand<Response> urlJsonQueryCommand = new UrlJsonQueryCommand<Response>(
                "testCommand",
                new URL("http://www.doesnotexist-afdgasfdaq.de"),
                Response.class,
                false,
                "testGroup"
        ) {};

        try {
            Response response = urlJsonQueryCommand.execute();
        } catch (Exception acx) {
            assertThat(acx.getCause(),is(instanceOf(ApiCommunicationException.class)));
            ApiCommunicationException apiCommunicationException = (ApiCommunicationException) acx.getCause();
            assertThat(apiCommunicationException.getProblemReason(),is(ApiCommunicationException.ProblemReason.CONNECTION_IMPOSSIBLE));
            assertThat(apiCommunicationException.getMessage(),containsString("www.doesnotexist-afdgasfdaq.de"));

        }
    }

    /**
     * tries to query a resource that returns 501 (not implemented), and checks if the right problem reason is set in the thrown ApiCommunicationException
     * @throws MalformedURLException
     */
    @Test
    public void shouldThrowExceptionWithReasonServerUnableMyService() throws MalformedURLException {
        UrlJsonQueryCommand<Response> urlJsonQueryCommand = new UrlJsonQueryCommand<Response>(
                "testCommand",
                new URL("http://localhost:9020/find-ways-near-point?requestData={%22lat%22:47.9832100,%22lon%22:-11.1824846}"),
                Response.class,
                false,
                "testGroup"
        ) {};

        try {
            Response response = urlJsonQueryCommand.execute();
        } catch (Exception acx) {
            assertThat(acx.getCause(),is(instanceOf(ApiCommunicationException.class)));
            ApiCommunicationException apiCommunicationException = (ApiCommunicationException) acx.getCause();
            assertThat(apiCommunicationException.getProblemReason(),is(ApiCommunicationException.ProblemReason.SERVER_UNABLE));
            assertThat(apiCommunicationException.getMessage(),endsWith("communication.FeatureNotImplementedExceptionnegativ lat or lon is not supported"));
            assertThat(apiCommunicationException.getMessage(),containsString("localhost:9020/find-ways-near-point?requestData={%22lat%22:47.9832100,%22lon%22:-11.1824846}"));
        }
    }

    /**
     * tries to query a resource that returns 501 (not implemented), and checks if the right problem reason is set in the thrown ApiCommunicationException
     * @throws MalformedURLException
     */
    @Test
    public void shouldThrowExceptionWithReasonServerUnable() throws MalformedURLException {
        UrlJsonQueryCommand<Response> urlJsonQueryCommand = new UrlJsonQueryCommand<Response>(
                "testCommand",
                new URL("http://httpstat.us/501"),
                Response.class,
                false,
                "testGroup"
        ) {};

        try {
            Response response = urlJsonQueryCommand.execute();
        } catch (Exception acx) {
            assertThat(acx.getCause(),is(instanceOf(ApiCommunicationException.class)));
            ApiCommunicationException apiCommunicationException = (ApiCommunicationException) acx.getCause();
            assertThat(apiCommunicationException.getProblemReason(),is(ApiCommunicationException.ProblemReason.SERVER_UNABLE));
            assertThat(apiCommunicationException.getMessage(),endsWith("501 Not Implemented"));
            assertThat(apiCommunicationException.getMessage(),containsString("httpstat.us/501"));
        }
    }

    /**
     * tries to query a resource that returns 501 (not implemented), and checks if the right problem reason is set in the thrown ApiCommunicationException
     * @throws MalformedURLException
     */
    @Test
    public void shouldThrowExceptionWithReasonClientMistake() throws MalformedURLException {
        UrlJsonQueryCommand<Response> urlJsonQueryCommand = new UrlJsonQueryCommand<Response>(
                "testCommand",
                new URL("http://httpstat.us/400"),
                Response.class,
                false,
                "testGroup"
        ) {};

        try {
            Response response = urlJsonQueryCommand.execute();
        } catch (Exception acx) {
            assertThat(acx.getCause(),is(instanceOf(ApiCommunicationException.class)));
            ApiCommunicationException apiCommunicationException = (ApiCommunicationException) acx.getCause();
            assertThat(apiCommunicationException.getProblemReason(),is(ApiCommunicationException.ProblemReason.CLIENT_MISTAKE));
            assertThat(apiCommunicationException.getMessage(),endsWith("400 Bad Request"));
            assertThat(apiCommunicationException.getMessage(),containsString("httpstat.us/400"));
        }
    }

    /**
     * tries to query a resource that returns 501 (not implemented), and checks if the right problem reason is set in the thrown ApiCommunicationException
     * @throws MalformedURLException
     */
    @Test
    public void shouldThrowExceptionWithReasonServerError() throws MalformedURLException {
        UrlJsonQueryCommand<Response> urlJsonQueryCommand = new UrlJsonQueryCommand<Response>(
                "testCommand",
                new URL("http://httpstat.us/500"),
                Response.class,
                false,
                "testGroup"
        ) {};

        try {
            Response response = urlJsonQueryCommand.execute();
        } catch (Exception acx) {
            assertThat(acx.getCause(),is(instanceOf(ApiCommunicationException.class)));
            ApiCommunicationException apiCommunicationException = (ApiCommunicationException) acx.getCause();
            assertThat(apiCommunicationException.getProblemReason(),is(ApiCommunicationException.ProblemReason.SERVER_ERROR));
            assertThat(apiCommunicationException.getMessage(),endsWith("500 Internal Server Error"));
            assertThat(apiCommunicationException.getMessage(),containsString("httpstat.us/500"));
        }
    }

}
