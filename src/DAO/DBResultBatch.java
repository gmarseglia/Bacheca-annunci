package DAO;

import java.util.Arrays;

public class DBResultBatch {
    private int[] batchResult;
    private String batchMessage;
    private Boolean extraResult;
    private String extraMessage;

    public DBResultBatch(int[] batchResult) {
        this.batchResult = batchResult;
    }

    public DBResultBatch(boolean extraResult) {
        this.extraResult = extraResult;
    }

    public int[] getBatchResult() {
        return batchResult;
    }

    public void setBatchResult(int[] batchResult) {
        this.batchResult = batchResult;
    }

    public Boolean getExtraResult() {
        return extraResult;
    }

    public void setExtraResult(boolean extraResult) {
        this.extraResult = extraResult;
    }

    public String getBatchMessage() {
        return batchMessage;
    }

    public void setBatchMessage(String batchMessage) {
        this.batchMessage = batchMessage;
    }

    public boolean getAllTrue() {
        boolean result = true;
        if (this.getExtraResult() != null) {
            result &= this.getExtraResult();
        }
        if (this.batchResult != null) {
            for (int index = 0; index < this.getBatchResult().length && result; index++) {
                result &= this.getBatchResult()[index] == 1;
            }
        }
        return result;
    }

    public String getExtraMessage() {
        return extraMessage;
    }

    public void setExtraMessage(String extraMessage) {
        this.extraMessage = extraMessage;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "batchResult=" + Arrays.toString(batchResult) +
                ", batchMessage='" + batchMessage + '\'' +
                ", extraResult=" + extraResult +
                ", extraMessage='" + extraMessage + '\'' +
                '}';
    }
}
