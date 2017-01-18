package microservicecommons.interservicecommunication;

import microservicecommons.interservicecommunication.model.RetryOptions;

import java.net.URL;
import java.util.Map;

/**
 * Created by Flo on 13/05/2016.
 */
public class MicroserviceQueryCommand<T> extends UrlJsonQueryCommand<T> {

    private static final String COMMAND_GROUP = "MicroserviceQueryCommand";

    public MicroserviceQueryCommand(String aCommandName, URL aQueryUrl, Class aJsonMappingClass, boolean aCachingEnabled) {
        super(aCommandName, aQueryUrl, aJsonMappingClass, aCachingEnabled, COMMAND_GROUP);
    }

    public MicroserviceQueryCommand(String aCommandName, URL aQueryUrl, Class aJsonMappingClass, boolean aCachingEnabled, RetryOptions retryOptions, Integer aTimeout) {
        super(aCommandName, aQueryUrl, aJsonMappingClass, aCachingEnabled, COMMAND_GROUP, retryOptions, aTimeout);
    }

    public MicroserviceQueryCommand(String aCommandName, URL aQueryUrl, String method, Map<String,String> headers, Class aJsonMappingClass, boolean aCachingEnabled, String postData) {
        super(aCommandName, aQueryUrl, headers, method, aJsonMappingClass, aCachingEnabled, COMMAND_GROUP, postData);
    }
}
