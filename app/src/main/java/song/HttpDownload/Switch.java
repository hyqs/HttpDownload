package song.HttpDownload;
//用于获取开关实例.
public class Switch {
    //开关状态
    private boolean statu = false;
    //获取状态
    synchronized public boolean getStatu() {
        return statu;
    }
    //设置状态
    synchronized public void setStatu(boolean statu) {
        this.statu = statu;
    }

    //单例设计模式
    private static Switch INSTANCE = new Switch();
    private Switch(){};
    public static Switch getInstance(){
        return INSTANCE;
    };
}
