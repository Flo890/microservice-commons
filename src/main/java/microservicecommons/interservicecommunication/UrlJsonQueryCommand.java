package microservicecommons.interservicecommunication;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import microservicecommons.interservicecommunication.exception.ApiCommunicationException;
import microservicecommons.interservicecommunication.model.RetryOptions;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * abstract command class for any queries that are an url that returns some json
 * can be extended for use with other Microservices, external APIs, ...
 */
public abstract class UrlJsonQueryCommand<T> extends HystrixCommand<T> {

    private static final Logger LOGGER = LogManager.getLogger(UrlJsonQueryCommand.class);

    private static final Integer DEFAULT_TIMEOUT = 20000;
    private final String commandName;
    private final URL queryUrl;
    private final Class jsonMappingClass;
    private boolean cachingEnabled;
    private final Map<String,String> headers;
    private final String method;
    private final String postData;
    private RetryOptions retryOptions;

    public UrlJsonQueryCommand(String aCommandName, URL aQueryUrl, Class aJsonMappingClass, boolean aCachingEnabled, String commandGroup){
        this(aCommandName,aQueryUrl,aJsonMappingClass,aCachingEnabled,commandGroup,new RetryOptions(0, RetryOptions.RetryFunctionType.LINEAR),DEFAULT_TIMEOUT);
    }

    public UrlJsonQueryCommand(String aCommandName, URL aQueryUrl, Class aJsonMappingClass, boolean aCachingEnabled, String commandGroup, RetryOptions aRetryOptions, Integer aTimeout){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroup))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withRequestLogEnabled(false)
                        .withExecutionTimeoutInMilliseconds(aTimeout)));
        commandName = aCommandName;
        queryUrl = aQueryUrl;
        jsonMappingClass = aJsonMappingClass;
        cachingEnabled = aCachingEnabled;
        headers = new HashMap<>();
        method = "GET";
        postData = "";
        retryOptions = aRetryOptions;
    }

    public UrlJsonQueryCommand(String aCommandName, URL aQueryUrl, Map<String,String> aHeaders, String aMethod, Class aJsonMappingClass, boolean aCachingEnabled, String commandGroup, String aPostData){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroup))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withRequestLogEnabled(false)
                        .withExecutionTimeoutInMilliseconds(DEFAULT_TIMEOUT)));
        commandName = aCommandName;
        queryUrl = aQueryUrl;
        jsonMappingClass = aJsonMappingClass;
        cachingEnabled = aCachingEnabled;
        headers = aHeaders;
        method = aMethod;
        postData = aPostData;
        retryOptions = new RetryOptions(0, RetryOptions.RetryFunctionType.LINEAR);
    }

    @Override
    protected T run() throws Exception {
        //TODO the timeout should be counted for a single request, not for all tries together
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        for(int i = 0; i<retryOptions.getMaxAmountRetries()+1; i++) {
            try {
                connection = (HttpURLConnection) queryUrl.openConnection();
                connection.setRequestMethod(method);
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
                if (method.equals("POST")) {
                    byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postDataBytes);
                    outputStream.flush();
                    outputStream.close();
                }
                inputStream = connection.getInputStream();
                break;//no more tries
            } catch (IOException e) {
                if(i>=retryOptions.getMaxAmountRetries()) {
                    LOGGER.warn("retry exhausted after "+i+1+" tries.");

                    Integer responseCode = null;
                    String errorResponse = "";
                    try {
                        // try to get response code
                        if(connection != null) {
                            responseCode = connection.getResponseCode();
                        }
                        // try to get message from server
                        StringWriter errorStringWriter = new StringWriter();
                        IOUtils.copy(connection.getErrorStream(), errorStringWriter, Charset.defaultCharset());
                        errorResponse = errorStringWriter.toString();
                    } catch (Exception e2){}

                    if(responseCode != null) {
                            // throw exception depending on error reason
                            switch (responseCode) {
                                case 501:
                                    throw new ApiCommunicationException(
                                            e.getMessage()+". Server returned: "+errorResponse,
                                            e,
                                            ApiCommunicationException.ProblemReason.SERVER_UNABLE
                                    );
                                case 400:
                                    throw new ApiCommunicationException(
                                            e.getMessage()+". Server returned: "+errorResponse,
                                            e,
                                            ApiCommunicationException.ProblemReason.CLIENT_MISTAKE
                                    );
                                case 500:
                                    throw new ApiCommunicationException(
                                            e.getMessage()+". Server returned: "+errorResponse,
                                            e,
                                            ApiCommunicationException.ProblemReason.SERVER_ERROR
                                    );
                        }
                    }
                    // throw default exception if reason could not be determined
                    throw new ApiCommunicationException(
                            e.getMessage()+". Server returned: "+errorResponse,
                            e,
                            ApiCommunicationException.ProblemReason.CONNECTION_IMPOSSIBLE
                    );
                } else {
                    //make another try
                    LOGGER.debug("command "+commandName+" failed due to exception: "+e.getMessage()+". Starting retry soon...");
                    LOGGER.debug(e);
                    retryOptions.getOnEachRetry().apply(i+1);
                    if(retryOptions.getRetryFunctionType() == RetryOptions.RetryFunctionType.EXPONENTIAL){
                        //quadratische Steigerung der Wartezeit, beginnend mit 500ms
                        Thread.sleep(new Double((500/2)*Math.pow(2,i+1)).longValue());
                    } else { //type LINEAR
                        Thread.sleep(500);
                    }
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        T result =  (T) mapper.readValue(inputStream, jsonMappingClass);

        return result;
    }

    @Override
    protected T getFallback() {
        return super.getFallback();//TODO implement fallback
    }

    @Override
    protected String getCacheKey() {
        if(cachingEnabled) {
            if(HystrixRequestContext.isCurrentThreadInitialized()) {
                return queryUrl.toString();
            } else {
                LOGGER.warn("hystrix caching is enabled but not initialized! So caching does not work.");
            }
        }
        return null;
    }
}
