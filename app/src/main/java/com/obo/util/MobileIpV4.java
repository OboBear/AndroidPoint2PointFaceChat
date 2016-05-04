package com.obo.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class MobileIpV4 {
	
	public static String getLocalIpAddress(){ 
        
        try{ 
             for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
                 NetworkInterface intf = en.nextElement();   
                    for (Enumeration<InetAddress> enumIpAddr = intf   
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {   
                        InetAddress inetAddress = enumIpAddr.nextElement();   
                        if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {   
                             
                            return inetAddress.getHostAddress().toString();   
                        }   
                    }   
             } 
        }catch (SocketException e) { 
            // TODO: handle exception 
            Log.i("", "WifiPreference IpAddress---error-" + e.toString());
        } 
        
        
        
         
        return null;  
    } 

}
