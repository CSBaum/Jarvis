package net.stallbaum.jarvis.util;

import java.net.*;

public class WakeOnLan {
    
    public static final int PORT = 9;    
    
    public void wakeUp(String ipStr, String macStr) {
        
    	try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            //socket.send(packet);
            //socket.close();
            
            System.out.println("Wake-on-LAN packet sent.");
        }
    	catch (UnknownHostException uhex){
    		System.out.println("Unable to locate host: " + ipStr);
    		System.out.println(uhex.getMessage());
    	}
    	catch (IllegalArgumentException iex) {
    		System.out.println(iex.getMessage());
    	}
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet:" + e.getMessage());
        }        
    }
    
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
    
   
}