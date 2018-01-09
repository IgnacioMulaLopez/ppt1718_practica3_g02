/*
 * 
 * Prácticas de Protocolos de Transporte
 * Grado en Ingeniería Telemática
 * Universidad de Jaén
 * 
 * Implementación de un servidor socket TCP para un protocolo de aplicación
 * estándar en Java.
 * 
 * @author Ignacio Mula Lopez
 * @author Maria Josefa Fernández Guillén.
 */

/* A continuación se incluyen todos paquetes que serán necesarios para el correcto
 * funcionamiento del código.
 */

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


public class HTTPSocketConnection implements Runnable{    
    
    public static final String HTTP_Ok="200";
    private Socket mSocket=null;
    /*
     * Se recibe el socket conectado con el cliente
     * @param s Socket conectado con el cliente
     */
    public HTTPSocketConnection(Socket s){
        mSocket = s;
    }
    
    public void run() {
        
        /*
         * A continuación se va a definir y rellenar la variabe "fecha" con la 
         * que recogeremos la hora, en el formato utilizado por la cabecera date
         * de HTTP, que será presentada más adelante.
         */
        
        Instant instant = Instant.now();  
        String fecha = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(instant);
        
        /*
         * A continuación, vamos a definir las distintas variables que serán
         * utilizadas en el código.
         */
        
        Random r = new Random(System.currentTimeMillis());                                      // Un número aleatorio "r".
        int n=r.nextInt();                                                                      // Un entero "n".
        String request_line="";                                                                 // Un string "request_line".
        String Fech="Date: "+fecha;                                                             // El string "Fech" para dicha cabecera.
        String Conn="Connection: Closed";                                                       // El string "Conn" para dicha cabecera.
        String C_type="";                                                                       // El string "C_type" para dicha cabecera.
        String C_leng="";                                                                       // El string "C_leng" para dicha cabecera.
        String Allow="Allow: GET";                                                              // El string "" para dicha cabecera.
        String Server="Server: Practica3PPTT1718";                                              // El string "Server" para dicha cabecera.
        BufferedReader input;                                                                   // La variable "input" para las lecturas del buffer.
        DataOutputStream output;                                                                // La variable "output" para las salidas de datos.
        FileInputStream input_file;                                                             // La varible "input_file" para poder leer ficheros.
        
        /*
         * Aquí da comienzo propiamente dicha, nuestra función para comprobar
         * las peticiones del cliente.
         */
        
        try {
            byte[] outstatus=null;                                                              // Definimos la variable "outstatus".
            byte[] outheader=null;                                                              // Definimos la variable "outheader".
            byte[] outdata=null;                                                                // Definimos la variable "outdata".
            String status_line="";                                                              // Definimos el string "status_line".
            String header="";                                                                   // Definimos el string "header".
            String outmesg="";                                                                  // Definimos el String "outmesg".
            input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));        // Le pasamos a "input" el contenido del stream que se recibe.
            output = new DataOutputStream(mSocket.getOutputStream());                           // A "output" le pasamos el formato de los datos de salida.
            do{                                                                                 // Implementamos un "do", que se ejecutará mientras "request_line" no esté vacía.

                request_line= input.readLine();                                                 // Le pasamos a "request_line", la linea de petición del cliente.
                if(request_line.startsWith("GET")){                                             // En el caso de que esta linea empiece con "GET".
                    
                    String resourceFile="";                                                     // Definimos el string "resourceFile", y lo inicializamos a vacío.
                    String parts[]=request_line.split(" ");                                     // Definimos la variable "parts", que será igual al contenido de "request_line" dividido en partes.
                    
                    if(parts.length!=3){                                                        // En el caso de que el número de partes sea distinto de tres.
                        status_line="HTTP/1.1 400 Error\r\n";                                   // Definmos la línea de estado.
                        header=Fech+"\r\n"+Server+"\r\n"+Allow+"\r\n"+Conn+"\r\n";              // Definimos las cabeceras necesarias.
                        outmesg="Error de formato\r\n";                                         // Definimos el mensaje "outmesg".
                        //outstatus=status_line.getBytes();
                        //outheader=header.getBytes();
                        outdata=outmesg.getBytes();                                             // La pasamos a "outdata" los bytes de "outmesg".
                        System.out.println(status_line);                                        // Mostramos por pantalla la linea de estado.
                        System.out.println(header);                                             // Mostramos por pantalla las cabeceras.
                    }                                                                           // Fin del caso de error 400.
                    else{                                                                       // En el caso de que el nuumero de partes sea igual a tres.  
                        if(parts[0].equalsIgnoreCase("GET")){                                   // Comprobamos si la primera parte es igual a "GET".
                                                                                         
                            if(parts[1].equalsIgnoreCase("/")){                                 // Comprobamos si la segunda parte no indica ningun fichero.
                                resourceFile="index.html";                                      // Le pasamos a "resourceFile", el fichero "index.html"
                                C_type="Content Type: text/html";                               // Le pasamos a la cabecera "C_type" el tipo del fichero.   
                            }                                                                   // Fin del caso de fichero no especificado.
                            
                            else {                                                              // En el caso de que se especifique un fichero en concreto.                             
                                resourceFile="";                                                // Vaciamos la variable resourceFile.
                                String recur = parts[1];                                        // Le pasamos al String "recur", el nombre del fichero.
                                char[] aRecurso = recur.toCharArray();                          // Creamos el vector aRecurso, que se llenará con los caracteres de recur.
                                for (int x=1; x<aRecurso.length; x++){                          // Creamos un bucle for que recorra el vector aRecurso.
                                    resourceFile=resourceFile+aRecurso[x];                      // Rellenamos la variable resourceFile, con el contenido del vector aRecurso.
                                }                                                               // Fin del bucle for.
                                 if(resourceFile.endsWith(".html")){                            // En el caso de que el fichero termine con ".html".
                                    C_type="Content Type: text/html";                           // Le pasamos a la cabecera "C_type", el tipo del fichero.
                                } else if(resourceFile.endsWith(".txt")){                       // En el caso de que el fichero termine con ".txt".
                                    C_type="Content Type: text";                                // Le pasamos a la cabecera "C_type", el tipo del fichero.
                                } else if(resourceFile.endsWith(".jpg")){                       // En el caso de que el fichero termine con ".jpg".
                                    C_type="Content Type: image";                               // Le pasamos a la cabecera "C_type", el tipo del fichero.
                                }                                                               // Fin de la comprobacion del tipo de fichero.
                            }                                                                   // Fin del caso de fichero especificado.
                            
                            outdata=leerRecurso(resourceFile);                                  // LLamamos a la función "leerRecurso" con el nombre del fichero a leer, para rellenar "outdata".
                            
                            if(outdata==null){                                                  // En el caso de que no se haya encontrado el archivo, y por tanto "outdata" esté vacío.
                                status_line="HTTP/1.1 404 Error\r\n";                           // Definimos la linea de estado.
                                header=Fech+"\r\n"+Server+"\r\n"+Allow+"\r\n"+Conn+"\r\n";      // Definimos las cabeceras necesarias.
                                outmesg="<html><body><h1>Error 404. Archivo no encontrado</h1></body></html>"; // Definimos el mensaje outmesg.
                                //outstatus=status_line.getBytes();
                                //outheader=header.getBytes();
                                outdata=outmesg.getBytes();                                     // La pasamos a "outdata" los bytes de "outmesg".
                                System.out.println(status_line);                                // Mostramos por pantalla la linea de estado.
                                System.out.println(header);                                     // Mostramos por pantalla las cabeceras.
                            }                                                                   // Fin del caso de archivo no encontrado.
                            
                            else{                                                               // En el caso de que si se encuentre el fichero.       
                                C_leng="Content Length: "+outdata.length+"";                    // Rellenamos la cabecera "Content Length".
                                status_line="HTTP/1.1 200 OK\r\n";                              // Definimos la linea de estado.
                                header=Fech+"\r\n"+Server+"\r\n"+Allow+"\r\n"+C_type+"\r\n"+C_leng+"\r\n"+Conn+"\r\n"; // Definimos las cabeceras necesarias.
                                //outmesg=status_line+header+""+outdata+"";                                          // Enviamos el contenido del fichero al cliente.
                                //outstatus=status_line.getBytes();
                                //outheader=header.getBytes();
                                System.out.println(status_line);                                // Mostramos por pantalla la linea de estado.
                                System.out.println(header);                                     // Mostramos por pantalla las cabeceras.
                            }                                                                   // Fin del caso de fichero encontrado.
                            
                            if(parts[2].equalsIgnoreCase("HTTP/2")){                            // En el caso de que la tercera parte sea "HTTP/2".
                                                                                                // Se informará al cliente, de que está utilizando una versión incorrecta.
                                status_line="HTTP/1.1 505 Error\r\n";                           // Definmos la línea de estado.
                                header=Fech+"\r\n"+Server+"\r\n"+Allow+"\r\n"+Conn+"\r\n";      // Definimos las cabeceras necesarias.
                                outmesg="Version del protocolo no compatible\r\n";              // Definimos el mensaje "outmesg".
                                //outstatus=status_line.getBytes();
                                //outheader=header.getBytes();
                                outdata=outmesg.getBytes();                                     // La pasamos a "outdata" los bytes de "outmesg".
                                System.out.println(status_line);                                // Mostramos por pantalla la linea de estado.
                                System.out.println(header);                                     // Mostramos por pantalla las cabeceras.
                            }                                                                   // Fin del caso de versión incorrecta.

                        }                                                                       // Fin de la condición de que la primera parte sea "GET"
                    
                    }
                
                }else if(request_line.startsWith("POST") || request_line.startsWith("HEAD") || request_line.startsWith("OPTIONS") || request_line.startsWith("PUT") || request_line.startsWith("DELETE") || request_line.startsWith("TRACE") || request_line.startsWith("CONNECT")){
                                                                                                // Se informará al cliente de que está usando un método no permitido.
                    status_line="HTTP/1.1 405 Error\r\n";                                       // Definmos la línea de estado.
                    header=Fech+"\r\n"+Server+"\r\n"+Allow+"\r\n"+Conn+"\r\n";                  // Definimos las cabeceras necesarias.
                    outmesg="<html><body><h1>Error 405. Metodo no permitido</h1></body></html>";// Definimos el mensaje "outmesg".
                    //outstatus=status_line.getBytes();
                    //outheader=header.getBytes();
                    outdata=outmesg.getBytes();                                                 // La pasamos a "outdata" los bytes de "outmesg".
                    System.out.println(status_line);                                            // Mostramos por pantalla la linea de estado.
                    System.out.println(header);                                                 // Mostramos por pantalla las cabeceras.
                }                                                                               // Fin del caso de método incorrecto.                
            }while(request_line.compareTo("")!=0);                                              // Implementamos la condición para que se repita el "do".
    
        output.write(status_line.getBytes());
        output.write(header.getBytes());
        output.write("\r\n".getBytes());
        output.write(outdata);                                                                  // Escribimos en "output" el contenido de "outdata".
        input.close();                                                                          // Cerramos "input".
        output.close();                                                                         // Cerramos "output".
        mSocket.close();                                                                        // Cerramos el socket.
        n++;                                                                                    // Incrementamos "n" en uno.
        
        } catch (IOException e) {                                                               // En el caso de que se produzco otro error.
            System.err.println("Exception" + e.getMessage());                                   // Lo mostraremos por pantalla.
        }                                                                                       // Fin del caso de otros errores.
    }                                                                                           // Fin del public void run.
   
    /*
     * Método para leer un recurso del disco
     * @param resourceFile
     * @return los bytes del archio o null si éste no existe
     */
    private byte[] leerRecurso(String resourceFile) {
        /*
         * a continuación se definen las variables necesarias para esta función.
         */         
        File f = new File (resourceFile);        
        FileInputStream miArch;                                                 // La variable "miArch".
        long s;                                                                 // La variable "s" de tipo long.
        String datos;                                                           // Un string llamadao "datos".
        int i=0;                                                                // Un entero "i" inicializado a cero.
        s = f.length();                                                         // La pasamos a "s" el tamaño del fichero "f".
        int z = (int) s;                                                        // Un entero "z" que será el valor de "s" pasado a entero.
        byte b[] = new byte[z];                                                 // La variable "b" de tipo byte.
        
        try{                                                                    // Comenzamos con nuestra función.
                miArch = new FileInputStream(f);                                // Le pasamos a "miArch" el contenido de "f".               
                i= miArch.read(b);                                              // En la variable "i" leeremos "b".
      
        }catch(FileNotFoundException e){                                        // En el caso de que no se encuentre el archivo.
                System.out.println("Archivo no existe");                        // Informamos por pantalla de lo ocurrido.
                b=null;                                                         // Igualamos la variable "b" a null.
        }  
        catch(IOException e){                                                   // En el caso de que se produzca un error.
               System.out.println("Error al leer");                             // Informamos por pantalla de lo ocurrido.
                b= null;                                                        // Igualamos la variable "b" a null.
        } finally{                                                              // Para terminar.
            return b;                                                           // Devolvemos la variable "b".
        }
    }                                                                           // Fin de la función "leerRecurso".
}                                                                               // Fin de la clase HTTPSocketConnection.