package song.HttpDownload;
import android.os.Message;
import android.util.Log;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static song.HttpDownload.MainActivity.mHandler;
import static song.HttpDownload.MainActivity.mUrl;

//工具类,单例模式-恶汉式
public class Download implements Runnable{

    private static final Download INSTANCE = new Download();
    @Override
    public void run() {
        try
        {
            Log.d("线程"+Thread.currentThread().getName(),"开始!");
            URL url = new URL(mUrl);
            //打开链接
            HttpURLConnection coon = (HttpURLConnection) url.openConnection();
            //设置超时:2s
            coon.setConnectTimeout(2000);
            //获取输入流
            InputStream in = coon.getInputStream();
            //获得长度
            Log.d("长度", String.valueOf(coon.getContentLength()));
            //创建字节流
            byte[] bs = new byte[1024];
            //遍历数据
            while ((in.read(bs)) != -1){
                //一旦Switch对象为false,终止线程
                if(!Switch.getInstance().getStatu()){
                    //当关闭按钮时,中断当前线程。
                    in.close();//释放服务器资源
                    Thread.currentThread().interrupt();
                }
            }
            in.close();
        } catch (Exception e) {
            //设置开关状态
            Switch.getInstance().setStatu(false);
            Message msg = new Message();
            msg.what=100;
            if(e instanceof java.net.UnknownHostException){
                msg.obj = "未知主机异常:java.net.UnknownHostException";
            }else if(e instanceof java.net.ConnectException){
                msg.obj = "网络连接异常:java.net.ConnectException";
            }else if(e instanceof java.net.SocketTimeoutException){
                msg.obj = "网络连接超时(2s):java.net.SocketTimeoutException";
            }else if(e instanceof java.io.IOException){
                msg.obj = "已停止测试!";
            }else{
                msg.obj = e.toString();
            }
//            e.printStackTrace();
            mHandler.sendMessage(msg);
        }finally {
            //正常/异常都要执行的代码
            //线程结束,num--
            MainActivity.mNetSpeedTimer.stopSpeedTimer();//关闭网速
            ThreadNum.getInstance().numLess();
            Log.d("线程"+Thread.currentThread().getName(),"结束!");
        }
    }
    //私有化构造器
    private Download(){};
    //暴露获取对象的方法
    public static Download getInstance(){
        return INSTANCE;
    }
}
