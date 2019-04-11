package mbluenet.zac.com.gattsever.utils.exception;

import umbluenet.zac.com.umbluenet.base.BaseTask;
import umbluenet.zac.com.umbluenet.utils.FileUtils;
import umbluenet.zac.com.umbluenet.utils.Log;
import umbluenet.zac.com.umbluenet.utils.UtilHelper;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final Log log = Log.getInstance();
    private static CrashHandler instance = null;
    private static final String OUTPUT_CRASH = "System shutdown by the exception!"
            + FileUtils.NEW_LINE;

    private boolean isExit = false;
    private CrashListener crashListener;

    public void setCrashListener(CrashListener crashListener) {
        this.crashListener = crashListener;
    }

    public synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public CrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.e(UtilHelper.getExceptionLog(t, OUTPUT_CRASH, e));
        // crash op,
        if (crashListener != null) {
            crashListener.crash();
        } else {
            errorExit();
        }
    }

    /**
     * System Exit Task.
     *
     * @param arg
     */
    private void exit(final int arg) {
        new BaseTask() {

            @Override
            public boolean runTask() throws Exception {
                isExit = true;
                // wait for sending crash broadcast.
                Thread.sleep(3000);
                // Received Exception, clear all system resource.
                //TODO

                log.d(
                        String.format("the system has been terminated by [%s]",
                                arg == 0 ? "Normal State" : "Error State"));
                System.exit(arg);
                return false;
            }
        }.start();
    }

    public void exit() {
        exit(0);
    }

    public void errorExit() {
        exit(-1);
    }
}
