package song.HttpDownload;

import android.os.Build;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FontDownload {

    //@RequiresApi(api = Build.VERSION_CODES.N) :只有达到指定api级别,该方法才会被执行
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void download(int i){
        String str = "";
        String text = "";
        switch (i){
            case 0: str = Constants.font_00_url;
                    text = Constants.font_00;
                break;
            case 1: str = Constants.font_01_url;
                    text = Constants.font_01;
                break;
            default:
                Log.d("FontDownload", "选项越界!");break;
        }

        try {
            URL url = new URL(str);// url
            URLConnection coon = url.openConnection();

            Log.d("FontDownload","文件MIME类型:"+coon.getContentType());
            Log.d("FontDownload","文件长度:"+coon.getContentLength());

            // Zip流,注意解码
            ZipInputStream zip = new ZipInputStream(url.openStream(),Charset.forName("GBK"));
            ZipEntry zipEntry = null;//键值对
            //迭代zip中的条目
            while ((zipEntry = zip.getNextEntry()) != null) {
                //如果当前文件是AndroidManifest.xml,打印出来
                if (text.equals(zipEntry.getName())) {
                    Log.d("FontDownload","进入if,写出文件...");
                    //文件输出流
                    File dir = new File(MainActivity.mFiles,"fonts");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir,zipEntry.getName());
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    OutputStream out = new FileOutputStream(file);

                    //读取内容
                    byte[] buffer = new byte[(int)zipEntry.getSize()];
                    int len = 0;

                    /** zipInputStream中的read()方法说明:
                     *	参数1:缓冲流
                     * 	参数2:开始读取目标阵列时的偏移值
                     * 	参数3:读取的最大字节数 即:缓冲流大小
                     * */
                    //解码正常,编码错误
                    while((len = zip.read(buffer,0,buffer.length)) != -1) {
                        out.write(buffer,0,len);
                    }
                    out.close();
                }
                Log.d("FontDownload",zipEntry.getName());
                double d = (double)0;
                DecimalFormat df = new DecimalFormat("0.00");  //控制小数显示位数
                String unit = "";
                if(zipEntry.getSize() != -1 && zipEntry.getSize()>1024) {
                    d = (double)zipEntry.getSize()/1024;
                    unit = "KB";
                    if(d>1024) {
                        d = (double)d/1024;
                        unit = "MB";
                    }
                }
                Log.d("FontDownload","文件大小:"+df.format(d)+unit);
                Log.d("FontDownload","-----------------------");
                //关闭当前zipEntry
                zip.closeEntry();
            }
            //关闭此输入流并释放与流相关联的任何系统资源
            zip.close();
            //告诉主线程字体下载完成
            Message msg = new Message();
            msg.what = 200;
            MainActivity.mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
