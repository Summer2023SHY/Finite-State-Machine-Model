package datagathering;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.*;

public class DataGatheringManager implements TestReset{

    private static long TIME_OUT_LONG =  360000L;
    private static long TIME_OUT_SHORT = 120000L;

    private static Logger logger = LogManager.getLogger();

    private Timer clockTimer;
    private TestTimer clock;
    private TestRunner thread;

    private AtomicLong timeOutPeriod;

    public static AtomicBoolean check = new AtomicBoolean(false);

    public void runTest() {
        clockTimer = new Timer();
        setTimeOutShort();
        Timer timer = new Timer();
        DataGathering data = null;
        while(data == null || !data.getFinished()) {
            data = new DataGathering(this);
            thread = new TestRunner(data);
            resetClock();
            timer.schedule(thread, 0);
            while(!check.get()) {

            }
            if(!data.getFinished()) {
                thread.cancel();

                data.markTestUnfinished();
                System.gc();
                Runtime.getRuntime().gc();
            }
        }
    }

    public void resetTests() {
        check.set(false);
    }

    public void resetClock() {
        if(clock != null)
            clock.cancel();
        clock = new TestTimer();
        System.gc();
        Runtime.getRuntime().gc();
        clockTimer.schedule(clock,  timeOutPeriod.get());
    }

    public void setTimeOutShort() {
        timeOutPeriod.set(TIME_OUT_SHORT);
    }

    public void setTimeOutLong() {
        timeOutPeriod.set(TIME_OUT_LONG);
    }

    class TestRunner extends TimerTask{

        private DataGathering data;

        public TestRunner(DataGathering in) {
            data = in;
        }

        @Override
        public void run() {
            try {
                data.allInOneRunTests();
            }
            catch(RuntimeException e) {
                logger.catching(e);
            }
        }


    }

    class TestTimer extends TimerTask {

        public TestTimer() {
            super();
            check.set(false);
        }

        @Override
        public void run() {
            check.set(true);
            System.out.println("Time Out");
        }



    }

}
