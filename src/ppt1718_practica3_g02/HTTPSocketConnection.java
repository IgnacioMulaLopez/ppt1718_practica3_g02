
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

/**
 * Clase de atención de un servidor TCP sencillo
 * Prácticas de Protocolos de Transporte
 * Grado en Ingeniería Telemática
 * Universidad de Jaén
 * 
 * @author Juan Carlos Cuevas Martínez
 */
public class HTTPSocketConnection implements Runnable {    
    public static final String HTTP_Ok="200";
    private Socket mSocket=null;
      /**
     * Se recibe el socket conectado con el cliente
     * @param s Socket conectado con el cliente
     */
    public HTTPSocketConnection(Socket s){
        mSocket = s;
    }
    public void run() {
        Random r = new Random(System.currentTimeMillis());
        int n=r.nextInt();
        String request_line="";
        BufferedReader input;
        DataOutputStream output;
        FileInputStream input_file;
        try {
            String outmesg="";
            input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            output = new DataOutputStream(mSocket.getOutputStream());
            do{
                request_line= input.readLine();
                //Esto se deberíasacar del bucle.
                if(request_line.startsWith("GET")){
                    String parts[]=request_line.split(" ");
                    if(parts.length==3){
                        
                      if(parts[0].equalsIgnoreCase("GET")){
                      }else{
                          outmesg="HTTP/1.1 405\r\nContent-type:text/html\r\n\r\n <html><body><h1>Metodo incorrecto</h1></body></html>";
                      }
                      
                      if(parts[1].equalsIgnoreCase("/")){
                          outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n <html><body><h1>Hola"+n+"veces</h1></body></html>";
                      }else{
                          outmesg="HTTP/1.1 404r\nContent-type:text/html\r\n\r\n <html><body><h1>No encontrado</h1></body></html>";
                      }
                      
                      if(parts[2].equalsIgnoreCase("HTTP/2")){
                          outmesg="HTTP/1.1 505\r\nContent-type:text/html\r\n\r\n <html><body><h1>Version del protocolo no compatible</h1></body></html>";
                      }
                    }else{
                        outmesg="HTTP/1.1 400\r\n";
                      }
                }
                System.out.println(request_line);   
            }while(request_line.compareTo("")!=0);
           
            output.write(outmesg.getBytes());
            input.close();
            output.close();
            mSocket.close();
            n++;
        } catch (IOException e) {
            System.err.println("Exception" + e.getMessage());
        }
    }
}
