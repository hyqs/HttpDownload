package song.HttpDownload;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

import java.io.File;

import song.HttpDownload.Net.NetSpeed;
import song.HttpDownload.Net.NetSpeedTimer;

//写得一手烂代码
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mUrl_ET;//输入框
    public static Button mAction;//开关
    private Button mDropOut;//退出
    public static String mUrl;//URI
    public static SharedPreferences sp;
    private SharedPreferences.Editor mEdit;
    public static Handler mHandler;
    private TextView mLog;//Log
    private com.xw.repo.BubbleSeekBar mSeekBar;//seekbar
    private TextView mThreadNum;//线程数
    private TextView mInternet;//网络
    private TextView mNetSpeed;//网速
    public static File mFiles;//私有目录
    public static NetSpeedTimer mNetSpeedTimer;//

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initId();//初始化
        //回显
        mUrl_ET.setText(sp.getString("url",""));

        //用于监听消息
        mHandler = new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==100){
                    mAction_status(Switch.getInstance().getStatu());
                    String str = (String) msg.obj;
                    mLog.setText(str);
                }else if(msg.what == 200){
                    applyTypeface();
                }else if (msg.what == NetSpeedTimer.NET_SPEED_TIMER_DEFAULT){
                    String speed = (String) msg.obj;
                    mNetSpeed.setText("网速: "+speed);
                }

            }
        };
        applyTypeface();
    }
    //网速
    private void initNewWork() {
        //创建NetSpeedTimer实例
        mNetSpeedTimer = new NetSpeedTimer(this, new NetSpeed(), mHandler).setDelayTime(1000).setPeriodTime(2000);
        //在想要开始执行的地方调用该段代码
        mNetSpeedTimer.startSpeedTimer();
    }

    //使用Typeface
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void applyTypeface(){
        //自定义一个字体样式
        File f0 = new File(mFiles.toString()+"/fonts/"+Constants.font_00);
        File f1 = new File(mFiles.toString()+"/fonts/"+Constants.font_01);
        //如果ttf文件不存在,就下载
        if (!f0.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FontDownload.download(0);
                }
            }).start();
            return;
        }else if (!f1.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FontDownload.download(1);
                }
            }).start();
            return;
        }
        Typeface ttf_00 = Typeface.createFromFile(f0);
        Typeface ttf_01 = Typeface.createFromFile(f1);
        mUrl_ET.setTypeface(ttf_00);
        mThreadNum.setTypeface(ttf_00);
        mAction.setTypeface(ttf_00);
        mDropOut.setTypeface(ttf_00);
        mInternet.setTypeface(ttf_00);
        mNetSpeed.setTypeface(ttf_01);
    }
    //初始化控件
    private void initId() {
        sp = this.getSharedPreferences("config", MODE_PRIVATE);
        mFiles = this.getFilesDir();
        //uri输入框
        mUrl_ET = findViewById(R.id.inputUrl_ET);
        mUrl_ET.setOnClickListener(this);
        //网络/网速
        mInternet = findViewById(R.id.internet_TV);
        mNetSpeed = findViewById(R.id.netSpeed_TV);
        //TextView:线程数
        mThreadNum = findViewById(R.id.ThreadNum_TV);
        //seekbar 不能放在onClick()方法中,因为  mSeekBar 是自定义类型
        mSeekBar = findViewById(R.id.seek_bar_ThreadNum);
        mSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            //参数发生改变
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                mThreadNum.setText("线程数:"+((Integer)mSeekBar.getProgress()).toString());
            }
            //处于滑动状态
            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
            //停止滑动后
            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //开关
        mAction = findViewById(R.id.action_Btn);
        mAction.setOnClickListener(this);
        //退出
        mDropOut = findViewById(R.id.dropOut_Btn);
        mDropOut.setOnClickListener(this);

        //log
        mLog = findViewById(R.id.log);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_Btn :
                action();
                break;
            case R.id.dropOut_Btn :
                Switch.getInstance().setStatu(false);
                mNetSpeedTimer.stopSpeedTimer();
                finish();
                break;
            default: break;
        }
    }
    //点击开始按钮
    private void action() {
        //检测并保存
        if (saveUrl()) return;
        //取反,用于开关操作
        Switch.getInstance().setStatu(!Switch.getInstance().getStatu());
        //按钮更改(文本/背景)
        mAction_status(Switch.getInstance().getStatu());
        //如果是要启动
        if(Switch.getInstance().getStatu()){
            //启动
            Thread_manager();
        }
    }
    //方法:设置按钮显示状态
    private void mAction_status(boolean b) {
        if (b) {
            //开刷
            initNewWork();//启动网速
            mAction.setText("暂停");
            mAction.setBackgroundColor(Color.parseColor("#5BEA62"));
        }else {
            //暂停
            mNetSpeedTimer.stopSpeedTimer();//关闭网速
            mAction.setText("继续测试~");
            mAction.setBackgroundColor(Color.parseColor("#FF8080"));
        }
    }

    //线程管理器
    private void Thread_manager() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Download d = Download.getInstance();
                //for循环用于一直监听按钮状态
                for(;Switch.getInstance().getStatu();){
                    //如果当前线程数小于8,就创建线程
                    if(ThreadNum.getInstance().getNum()<mSeekBar.getProgress()){
                        //循环new线程
                        new Thread(d,String.valueOf(ThreadNum.getInstance().getNum())).start();
                        ThreadNum.getInstance().numAdd();
                    }
                }
            }
        }.start();
    }
    //保存url
    private boolean saveUrl() {
        mUrl = mUrl_ET.getText().toString().trim();
        if (mUrl ==null || mUrl.isEmpty()) {
            Toast.makeText(this, "无效链接!",Toast.LENGTH_SHORT).show();
            return true;
        }
        mEdit = sp.edit();
        mEdit.putString("url",mUrl);
        mEdit.commit();
        return false;
    }
}
