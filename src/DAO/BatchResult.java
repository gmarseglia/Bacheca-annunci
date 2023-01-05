package DAO;

public class BatchResult {
    private int[] singlesResult;
    private int batchResult;

    public BatchResult(int[] singlesResult) {
        this.singlesResult = singlesResult;
        this.batchResult = 0;
        if(singlesResult != null){
            for(int singleResult : this.singlesResult) this.batchResult += singleResult;
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
}
