package song.HttpDownload.Net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class NetSpeedTimer {
    private long defaultDelay = 1000;
    private long defaultPeriod = 1000;
    private static final int ERROR_CODE = -101011010;
    private int mMsgWhat = ERROR_CODE;
    private NetSpeed mNetSpeed;
    private Handler mHandler;
    private Context mContext;
    private SpeedTimerTask mSpeedTimerTask;

    public static final int NET_SPEED_TIMER_DEFAULT = 101010;
    //网速计时器(构造器)
    public NetSpeedTimer(Context context, NetSpeed netSpeed, Handler handler) {
        this.mContext = context;
        this.mNetSpeed = netSpeed;
        this.mHandler = handler;
    }
    //设置延迟时间
    public NetSpeedTimer setDelayTime(long delay) {
        this.defaultDelay = delay;
        return this;
    }
    //设置周期时间
    public NetSpeedTimer setPeriodTime(long period) {
        this.defaultPeriod = period;
        return this;
    }
    //信息发送
    public NetSpeedTimer setHanderWhat(int what) {
        this.mMsgWhat = what;
        return this;
    }

    /*
     * 开启获取网速定时器
     */
    public void startSpeedTimer() {
        Timer timer = new Timer();
        mSpeedTimerTask = new SpeedTimerTask(mContext, mNetSpeed, mHandler,
                mMsgWhat);
        timer.schedule(mSpeedTimerTask, defaultDelay, defaultPeriod);
    }

    /*
     * 关闭定时器
     */
    public void stopSpeedTimer() {
        if (null != mSpeedTimerTask) {
            mSpeedTimerTask.cancel();
        }
    }

    /*
     * @author
     * 静态内部类
     */
    private static class SpeedTimerTask extends TimerTask {
        private int mMsgWhat;
        private NetSpeed mNetSpeed;
        private Handler mHandler;
        private Context mContext;

        public SpeedTimerTask(Context context, NetSpeed netSpeed,
                              Handler handler, int what) {
            this.mContext = context;
            this.mHandler = handler;
            this.mNetSpeed = netSpeed;
            this.mMsgWhat = what;
        }
        //定时创建线程发送
        @Override
        public void run() {
            if (null != mNetSpeed && null != mHandler) {
                Message obtainMessage = mHandler.obtainMessage();
                if (mMsgWhat != ERROR_CODE) {
                    obtainMessage.what = mMsgWhat;
                } else {
                    obtainMessage.what = NET_SPEED_TIMER_DEFAULT;
                }
                obtainMessage.obj = mNetSpeed.getNetSpeed(mContext
                        .getApplicationInfo().uid);
                mHandler.sendMessage(obtainMessage);
            }
        }
    }
}