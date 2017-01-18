package microservicecommons.interservicecommunication.model;

/**
 * Created by flobe on 31/07/2016.
 */
public class SyncServiceResponse {

    private boolean success;

    private String message;

    private Integer affectedAmount;

    public SyncServiceResponse() {
    }

    public SyncServiceResponse(boolean success, String message) {
        this(success, message, null);
    }

    public SyncServiceResponse(boolean success, String message, Integer affectedAmount) {
        this.success = success;
        this.message = message;
        this.affectedAmount = affectedAmount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAffectedAmount() {
        return affectedAmount;
    }
}
