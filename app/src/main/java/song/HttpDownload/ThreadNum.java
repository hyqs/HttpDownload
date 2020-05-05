package song.HttpDownload;
/**     用于描述:
 *          线程对象,及其方法
 *          synchronized修饰方法:同一时刻只能有一个线程在方法中,保证线程安全
 *
 * */
public class ThreadNum {
    //当前线程数
    private static int num = 0;
    //直接初始化对象
    private static ThreadNum INSTANCE = new ThreadNum();
    //加
    synchronized static void numAdd(){
        num++;
    }
    //减
    synchronized static void numLess(){
        num--;
    }
    //获取线程数
    static int getNum(){
        return num;
    }
    //私有化构造器
    private ThreadNum(){}
    //暴露对象
    public static ThreadNum getInstance(){
        return INSTANCE;
    }
}
