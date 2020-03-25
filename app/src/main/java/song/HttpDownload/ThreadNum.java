package song.HttpDownload;

public class ThreadNum {
    //当前线程数
    private static int num = 0;
    private static ThreadNum INSTANCE = new ThreadNum();
    //加
    synchronized public void numAdd(){
        num++;
    }
    //减
    synchronized public void numLess(){
        num--;
    }
    //获取线程数
    synchronized public int getNum(){
        return num;
    }
    private ThreadNum(){}
    public static ThreadNum getInstance(){
        return INSTANCE;
    };
}
