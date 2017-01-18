package microservicecommons.interservicecommunication.model;

import java.util.function.Function;

/**
 * Created by flobe on 24/12/2016.
 */
public class RetryOptions {

    public enum RetryFunctionType {
        LINEAR, EXPONENTIAL
    }

    private final Integer maxAmountRetries;

    private final RetryFunctionType retryFunctionType;

    private final Function<Integer,Void> onEachRetry;

    public RetryOptions(Integer maxAmountRetries, RetryFunctionType retryFunctionType) {
        this(maxAmountRetries, retryFunctionType,(Integer retryCount) -> null);
    }

    public RetryOptions(Integer maxAmountRetries, RetryFunctionType retryFunctionType, Function<Integer,Void> onEachRetry) {
        this.maxAmountRetries = maxAmountRetries;
        this.retryFunctionType = retryFunctionType;
        this.onEachRetry = onEachRetry;
    }

    public Integer getMaxAmountRetries() {
        return maxAmountRetries;
    }

    public RetryFunctionType getRetryFunctionType() {
        return retryFunctionType;
    }

    public Function<Integer, Void> getOnEachRetry() {
        return onEachRetry;
    }
}
