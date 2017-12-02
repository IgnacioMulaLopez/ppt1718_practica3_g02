
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author usuario
 */
public class Cliente implements Runnable{
 
    private String mId="";
    
    public Cliente(String id){
        mId=id;
    }
    

    public synchronized void run() {
   
        try{       
            InetAddress destination = InetAddress.getByName("localhost");
            System.out.println("-------------------\r\nIniciando cliente "+mId+"\r\n--------------------");
            
            System.out.println("Conectando con socket "+destination.toString());
            
            Socket socket = new Socket(destination,80);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write("GET / HTTP/1.1\r\nhost:www10.ujaen.es\r\nConnection:close\r\n\r\n".getBytes());
            String line="";
            int i=0;
            while((line=input.readLine())!=null) {
                if(i==0)
                    System.out.println("<"+mId+"> "+line);
                i++;
            }   
            
        }catch (UnknownHostException e) {
            System.out.println("\tUnabletofindaddressfor");
        } catch(IOException ex){
        
            System.out.println("\tError: " +ex.getMessage()+"\r\n"+ex.getStackTrace());
        }
    }
    
}

