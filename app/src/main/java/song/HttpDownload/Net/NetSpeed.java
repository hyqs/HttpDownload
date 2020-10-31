package song.HttpDownload.Net;

import android.net.TrafficStats;

import java.text.DecimalFormat;

/*
    *       网速类
    * 作用:每调用一次getNetSpeed()方法,返回前一秒与当前秒的流量差
    * 设计:应该使用定时器每隔一秒调用一次      转到该类 --> NetSpeedTimer
    * */
public class NetSpeed {
    private static final String TAG = NetSpeed.class.getSimpleName();
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    DecimalFormat df = new DecimalFormat("0.00");//小数格式,保留两位

    public String getNetSpeed(int uid) {
        long nowTotalRxBytes = getTotalRxBytes(uid);//总接收字节
        long nowTimeStamp = System.currentTimeMillis();//当前时间
        //(当前接收字节数-上次接收字节数)/(当前时间-上次时间  ms)*1000   单位:KB/s
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        if(speed>1024){
            return df.format(speed / 1024.00) + " MB/s";
        }
        return speed + " KB/s";
    }


    //getApplicationInfo().uid
    public long getTotalRxBytes(int uid) {
        //TrafficStats  判断是否支持流量统计api, 不支持:0  支持:返回总接收字节,单位KB

        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }
}