package ubc.GameTree;

public class Timer {
    private final int MAX_DEPTH = 10;
    private final int GRACE_PERIOD = 100;

    private long timeLimit = 27000;  // 27 seconds by default
    private long startTime;

    public Timer(){

    }

    public Timer(long timeLimit){
        this.timeLimit = timeLimit;
    }

    public void startTimer(){
        startTime = System.currentTimeMillis();
    }

    public void resetTimer(){
        startTime = 0;
    }

    public long getTimeElapsed(){
        return System.currentTimeMillis() - startTime;
    }

    public long getRemainingTime(){
        long timeElapsed = System.currentTimeMillis() - startTime;
        return timeLimit - timeElapsed;
    }

    public boolean timeOut(){
        long timeElapsed = System.currentTimeMillis() - startTime;
        return timeElapsed > (timeLimit - GRACE_PERIOD);
    }
}
