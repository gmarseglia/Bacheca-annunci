package DAO;

public class BatchResult {
    private int[] singlesResult;
    private int batchResult;

    public BatchResult(int[] singlesResult) {
        this.singlesResult = singlesResult;
        this.batchResult = 0;
        if (singlesResult != null) {
            for (int singleResult : this.singlesResult) this.batchResult += singleResult;
        }
    }

    public int[] getSinglesResult() {
        return singlesResult;
    }

    public void setSinglesResult(int[] singlesResult) {
        this.singlesResult = singlesResult;
    }

    public int getBatchResult() {
        return batchResult;
    }

    public void setBatchResult(int batchResult) {
        this.batchResult = batchResult;
    }

    public boolean getAllTrue() {
        boolean finalResult = true;
        int batchSize = this.singlesResult.length;
        for (int batchIndex = 0; finalResult && batchIndex < batchSize - 1; batchIndex++)
            finalResult = finalResult && this.singlesResult[batchIndex] == 1;
        return finalResult;
    }
}
