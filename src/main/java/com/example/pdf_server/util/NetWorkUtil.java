package com.example.pdf_server.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetWorkUtil {

    public static String getLocalIp() throws Exception{
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while(interfaces.hasMoreElements()){
            NetworkInterface networkInterface = interfaces.nextElement();

            if(!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()){
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

            while(addresses.hasMoreElements()){
                InetAddress address = addresses.nextElement();

                if(address instanceof Inet4Address){
                    return address.getHostAddress();
                }
            }
        }

        throw new RuntimeException("로컬 IP를 찾을 수 없습니다.");

    }

    public static String getWifiIp() throws Exception {

        Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {

            NetworkInterface ni = interfaces.nextElement();

            // Wi-Fi 인터페이스만
            if (!ni.getDisplayName().toLowerCase().contains("wi-fi")
                    && !ni.getName().toLowerCase().contains("wi-fi")) {
                continue;
            }

            if (!ni.isUp() || ni.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();

            while (addresses.hasMoreElements()) {

                InetAddress address = addresses.nextElement();

                if (address instanceof Inet4Address) {
                    return address.getHostAddress();
                }
            }
        }

        throw new RuntimeException("Wi-Fi IP를 찾을 수 없습니다.");
    }

}
