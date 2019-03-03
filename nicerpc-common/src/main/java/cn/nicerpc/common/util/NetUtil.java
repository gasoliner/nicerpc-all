package cn.nicerpc.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {

    private static InetAddress inetAddress;

    static {
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String getHostAddr() {
        return inetAddress.getHostAddress();
    }

}
