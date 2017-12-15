
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;
import java.io.FileReader;

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
        /*
        StringBuilder contentBuilder = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\ignac\\Documents\\GitHub\\PPT_Practica3\\src\\ppt1718_practica3_g02\\index.html"));
                String str;
                while ((str = in.readLine()) != null) {
                    contentBuilder.append(str);
                }
                in.close();
            }   catch (IOException e) {
                }   
        String content = contentBuilder.toString();
        */
        
        Random r = new Random(System.currentTimeMillis());
        int n=r.nextInt();
        String request_line="";
        BufferedReader input;
        DataOutputStream output;
        FileInputStream input_file;
        try {
            byte[] outdata=null;
            String outmesg="";
            input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            output = new DataOutputStream(mSocket.getOutputStream());
            do{
                
                request_line= input.readLine();
                //Esto se deberíasacar del bucle.
                if(request_line.startsWith("GET")){
                    
                    String resourceFile="";
                    String parts[]=request_line.split(" ");
                    if(parts.length==3){
                        
                        //Para content-type, mirar la extensión del archivo.
                        
                      if(parts[0].equalsIgnoreCase("GET")){
                      }else{
                          outmesg="HTTP/1.1 405\r\nContent-type:text/html\r\n\r\n <html><body><h1>Metodo incorrecto</h1></body></html>";
                      }
                      
                      if(parts[1].equalsIgnoreCase("/")){
                          outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n";
                          resourceFile="index.html";
                          System.out.println("Content-Type = text/html");
                      }else if (parts[1].equalsIgnoreCase("/imagen.jpg")){
                          outmesg="HTTP/1.1 200 OK \r\nContent-type:image \r\n\r\n";
                          resourceFile="imagen.jpg";
                          System.out.println("Content-Type = Image");
                      }
                      
                          
                          outdata=leerRecurso(resourceFile);
                          System.out.println("Content-Length: "+outdata.length+"");
                          //cabecera contentllength con .length
                        if(outdata==null){
                          outmesg="HTTP/1.1 404r\nContent-type:text/html\r\n\r\n <html><body><h1>No encontrado</h1></body></html>";
                          outdata=outmesg.getBytes();
                      }else{
                            
                        outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n"+outdata+"";
                        }
                    
                      
                      if(parts[2].equalsIgnoreCase("HTTP/2")){
                          outmesg="HTTP/1.1 505\r\nContent-type:text/html\r\n\r\n <html><body><h1>Version del protocolo no compatible</h1></body></html>";
                          outdata=outmesg.getBytes();
                      }
                    }else{
                        outmesg="HTTP/1.1 400\r\n";
                        outdata=outmesg.getBytes();
                      }
                } else if(request_line.startsWith("Connection")){
                    request_line="Connection = Closed";
                } else if (request_line.startsWith("Archivo")){
                    outmesg="HTTP/1.1 404r\nContent-type:text/html\r\n\r\n <html><body><h1>No encontrado</h1></body></html>";
                          outdata=outmesg.getBytes();
                }
                
                System.out.println(request_line);   
            }while(request_line.compareTo("")!=0);
            
            //CABECERAS
           
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
                        System.out.println(b.toString());
                }catch(IOException e){
                        System.out.println("Error al leer");
                }
        }catch(FileNotFoundException e){
                System.out.println("Archivo no existe");
        }
        
        return b;
    }

}
