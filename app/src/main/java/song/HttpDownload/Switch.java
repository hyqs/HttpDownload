package song.HttpDownload;

/**
 *      开关状态:
 * */
class Switch {
    //开关状态
    private static boolean statu = false;
    //获取状态
    static boolean getStatu() {
        return statu;
    }
    //设置状态
    static void setStatu(boolean statu) {
        INSTANCE.statu = statu;
    }

    //单例设计模式
    private static Switch INSTANCE = new Switch();
    private Switch(){};
    static Switch getInstance(){
        return INSTANCE;
    };
}
