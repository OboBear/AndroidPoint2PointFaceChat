package com.obo.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import android.util.Log;

public class MobileIpV4 {
	
	public static String getLocalIpAddress(){ 
        
        try {
             for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
                 NetworkInterface intf = en.nextElement();   
                    for (Enumeration<InetAddress> enumIpAddr = intf   
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                             
                            return inetAddress.getHostAddress();
                        }   
                    }   
             } 
        } catch (SocketException e) {
            // TODO: handle exception 
            Log.i("", "WifiPreference IpAddress---error-" + e.toString());
        }

        return null;  
    } 

}
