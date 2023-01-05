package Controller;

public class TimerController {
    private long startTime;
    private long stopTime;

    public TimerController() {
    }

    public long start(){
        this.startTime = System.nanoTime();
        return this.startTime;
    }

    public long stop(){
        stopTime = System.nanoTime();
        return this.stopTime;
    }

    public long now(){
        return System.nanoTime();
    }

    public long getDurationInSec(){
        return (this.stopTime - this.startTime) / 1000000000;
    }

}
