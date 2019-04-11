package com.example.liusheng.painboard.Other;


/**
 * Created by renshaoyuan on 2017/12/4.
 */

public class Singleton {
    static Singleton instance = null;
    public static int a = 0;
    public static int b = 0;
    public static int CurrentItem = 0;
    public static boolean isXiuGai = false;
    public static String weiyibiaoshi = null;
    public static String fenlei = "事件";
    public static String geshi = "dyiz";
    public static String gsorfl = "分类";
    public static String whosefragment = "全部";
    public static String chajiantiaozhuan = null;
    public static String yangshi = null;
    public static String wenziorbeijin = null;
    public static String beijingtu = null;
    public static int timer = 0;
    public static String currentTime = null;
    //桌面插件的数据数组

    //public static ArrayList<String> arrayList = new ArrayList<String>();

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
            System.out.println("初始化单利");
        }
        return instance;
    }

}
