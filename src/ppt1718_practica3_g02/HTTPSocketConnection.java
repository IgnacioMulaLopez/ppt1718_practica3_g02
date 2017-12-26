
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Date;

/**
 * Clase de atención de un servidor TCP sencillo
 * Prácticas de Protocolos de Transporte
 * Grado en Ingeniería Telemática
 * Universidad de Jaén
 * 
 * @author Juan Carlos Cuevas Martínez
 */
public class HTTPSocketConnection implements Runnable{    
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
        
        Instant instant = Instant.now();
        String fecha = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(instant);
        
        Random r = new Random(System.currentTimeMillis());
        int n=r.nextInt();
        String request_line="";
        String Conn="",C_type="",C_leng="",Allow="",Server="";
        BufferedReader input;
        DataOutputStream output;
        FileInputStream input_file;
        try {
            byte[] outdata=null;
            String outmesg="";
            input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            output = new DataOutputStream(mSocket.getOutputStream());
            do{
                Allow="GET";
                Server="??";
                request_line= input.readLine();
                if(request_line.startsWith("GET")){
                    
                    String resourceFile="";
                    String parts[]=request_line.split(" ");
                    if(parts.length==3){
                        
                        if(parts[0].equalsIgnoreCase("GET")){ 
                            
                            if(parts[1].equalsIgnoreCase("/") || parts[1].equalsIgnoreCase("/index.html")){                                
                                outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n";
                                resourceFile="index.html";
                                System.out.println("HTTP/1.1 200 OK");
                                C_type="text/html";
                                
                            }
                            
                            else if (parts[1].equalsIgnoreCase("/imagen.jpg")){                                
                                outmesg="HTTP/1.1 200 OK \r\nContent-type:image \r\n\r\n";
                                resourceFile="imagen.jpg";
                                System.out.println("HTTP/1.1 200 OK");
                                C_type="Image";
                            }
                            
                            outdata=leerRecurso(resourceFile);
                            
                            if(outdata==null){
                                outmesg="HTTP/1.1 404r\nContent-type:text/html\r\n\r\n <html><body><h1>No encontrado</h1></body></html>";
                                outdata=outmesg.getBytes();
                                System.out.println("HTTP/1.1 404 Error");
                            }
                            
                            else{          
                                outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n"+outdata+"";
                            }
                            C_leng=""+outdata.length+"";
                            
                            if(parts[2].equalsIgnoreCase("HTTP/2")){
                                outmesg="HTTP/1.1 505\r\nContent-type:text/html\r\n\r\n <html><body><h1>Version del protocolo no compatible</h1></body></html>";
                                outdata=outmesg.getBytes();
                                System.out.println("HTTP/1.1 505 Error");
                            } 
                        }
                    }else{
                        outmesg="HTTP/1.1 400\r\n";
                        outdata=outmesg.getBytes();
                        System.out.println("HTTP/1.1 400 Error");
                    }
                }else if(request_line.startsWith("Connection")){
                    Conn="Closed";
                }else if(request_line.startsWith("POST")){
                    outmesg="HTTP/1.1 405\r\nContent-type:text/html\r\n\r\n <html><body><h1>Metodo incorrecto</h1></body></html>";
                    outdata=outmesg.getBytes();
                    System.out.println("HTTP/1.1 405 Error");
                }
                
            //System.out.println(request_line);    
            }while(request_line.compareTo("")!=0);
            
            //CABECERAS
            System.out.println("Date: "+fecha+"");
            System.out.println("Server: "+Server+"");
            System.out.println("Allow: "+Allow+"");
            System.out.println("Content-Type: "+C_type+"");
            System.out.println("Content-Length: "+C_leng+"");
            System.out.println("Connection: "+Conn+""); 
            System.out.println(" ");
            
        output.write(outdata);
        input.close();
        output.close();
        mSocket.close();
        n++;
        } catch (IOException e) {
            System.err.println("Exception" + e.getMessage());
        }
    }
        /**
     * Método para leer un recurso del disco
     * @param resourceFile
     * @return los bytes del archio o null si éste no existe
     */
    private byte[] leerRecurso(String resourceFile) {
        //Se debe comprobar que existe
        
        File f = new File ("C:\\\\Users\\\\ignac\\\\Documents\\\\GitHub\\\\PPT_Practica3\\\\src\\\\ppt1718_practica3_g02\\\\"+resourceFile+"");        
        FileInputStream miArch;
        long s;
        String datos;
        int i=0;
         s= f.length();
         
        int z = (int) s;
        
        byte b[] = new byte[z];
        try{
                miArch = new FileInputStream(f);
                try{                    //leer 24 bytes ,  Siempre???????
                        while( (i= miArch.read(b)) !=-1 ){
                                datos= new String(b,0,0,i);
                                //System.out.println(datos);
                        }
                        //System.out.println(b.toString());
                }catch(IOException e){
                        System.out.println("Error al leer");
                }
        }catch(FileNotFoundException e){
                System.out.println("Archivo no existe");
                b=null;
        }
        
        return b;
    }

}
