package song.HttpDownload;
import android.os.Message;
import android.util.Log;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static song.HttpDownload.MainActivity.mAction;
import static song.HttpDownload.MainActivity.mHandler;
import static song.HttpDownload.MainActivity.mUrl;

//工具类,单例模式-恶汉式
public class Download implements Runnable{

    private static final Download INSTANCE = new Download();
    @Override
    public void run() {
        try {
            Log.d("线程"+Thread.currentThread().getName(),"开始!");

            URL url = new URL(mUrl);
            //打开链接
            HttpURLConnection coon = (HttpURLConnection) url.openConnection();
            //设置超时
            coon.setConnectTimeout(3000);
            //打开输入流
            InputStream is = coon.getInputStream();
            //获得长度
            coon.getContentLength();
            //创建字节流
            byte[] bs = new byte[1024];
//            int len = 0;
            //遍历数据
            while ((is.read(bs)) != -1){
                //一旦下令停止,终止线程
                if(!Switch.getInstance().getStatu()){
                    //当关闭按钮时,结束线程
                    Thread.currentThread().interrupt();
                }
            }
            is.close();
        } catch (Exception e) {
            Switch.getInstance().setStatu(false);
            e.printStackTrace();
            Message msg = new Message();
            msg.what=100;
            if(e instanceof java.net.ConnectException){
                msg.obj = "网络异常:java.net.ConnectException";
            }else if(e instanceof java.io.InterruptedIOException){
                msg.obj = "强行停止线程!";
            }else{
                msg.obj = e.toString();
            }
            mHandler.sendMessage(msg);
        }finally {
            //正常/异常都要执行的代码
            //线程结束,num--
            ThreadNum.getInstance().numLess();
            Log.d("线程"+Thread.currentThread().getName(),"下载完成!");
        }
    }
    //私有化构造器
    private Download(){};
    //暴露获取对象的方法
    public static Download getInstance(){
        return INSTANCE;
    }
}
