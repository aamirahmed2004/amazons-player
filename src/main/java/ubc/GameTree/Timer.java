package ubc.GameTree;

public class Timer {
    private final int MAX_DEPTH = 10;
    private final int GRACE_PERIOD = 100;

    private long timeLimit = 27000;  // 27 seconds by default
    private long startTime;

    public Timer(){
        this.startTime = System.currentTimeMillis();
    }

    public Timer(long timeLimit){
        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit;
    }

    public boolean sufficientTimeForNextMove(){
        long timeElapsed = System.currentTimeMillis() - startTime;
        return timeElapsed < (timeLimit - GRACE_PERIOD);
    }
}
