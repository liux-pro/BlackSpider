package pro._91code.blackspider.util;

public class DataUtil {
    public static int getInt(byte[] data, int low, int height) {
        return (data[height] << 8 & 0xFF00) | (data[low] & 0xFF);
    }

    public static int getInt(byte[] data, int low, int mid,int height) {
        return (data[height] << 16 & 0xFF0000) | (data[mid] << 8 & 0xFF00) | (data[low] & 0xFF);
    }

    /**
     * 将n上调至8的倍数
     * https://blog.csdn.net/junya_zhang/article/details/88414578
     * @param n 原int
     * @return  result 满足 min(result > n && result % 8 = 0)
     */
    public static int roundUp(int n){
        return (n+7)&(~7);
    }
}