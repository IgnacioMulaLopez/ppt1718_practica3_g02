
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
public class MainServer {

    /**
     * @param args the command line arguments
     */
    
     public static final String MSG_HANDSHAKE="Iniciando conexion con el servidor\r\n";
     
    private static ServerSocket mMainServer= null;
    
    //public static final String MSG_HANDSHAKE="200 bienvenido\r\n";
    
    public static void main(String[] args)  {
  
       // new Thread((Runnable) new Cliente("1")).start();
       

        try {
            mMainServer= new ServerSocket(80);
            System.out.println(MSG_HANDSHAKE);
            while(true) {
                Socket socket =mMainServer.accept();
                 System.out.println("Conexión entrande desde: "+socket.getInetAddress().toString());
                 Thread connection= new Thread(new HTTPSocketConnection(socket));
                 connection.start();
            }
        } catch (java.net.BindException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex2){
            System.err.println(ex2.getMessage());
        }
    }  
}
