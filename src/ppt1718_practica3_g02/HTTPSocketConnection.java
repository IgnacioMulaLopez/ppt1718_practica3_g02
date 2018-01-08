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
        
        Random r = new Random(System.currentTimeMillis());                                      //Un número aleatorio "r".
        int n=r.nextInt();                                                                      // Un entero "n".
        String request_line="";                                                                 // Un string "request_line".
        String Conn="",C_type="",C_leng="",Allow="",Server="";                                  // Los string que se necesitan para las cabeceras.
        BufferedReader input;                                                                   // La variable "input" para las lecturas del buffer.
        DataOutputStream output;                                                                //La variable "output" para las salidas de datos.
        FileInputStream input_file;                                                             //La varible "input_file" para poder leer ficheros.
        
        /*
         * Aquí da comienzo propiamente dicha, nuestra función para comprobar
         * las peticiones del cliente.
         */
        
        try {
            byte[] outdata=null;                                                                // Definimos la variable "outdata".
            String outmesg="";                                                                  // Definimos el String "outmesg".
            input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));        // Le pasamos a "input" el contenido del stream que se recibe.
            output = new DataOutputStream(mSocket.getOutputStream());                           // A "output" le pasamos el formato de los datos de salida.
            do{                                                                                 // Implementamos un "do", que se ejecutará mientras "request_line" no esté vacía.
                Allow="GET";                                                                    // Establecemos para la cabecera "Allow" que solo se permite el método "GET".
                Server="??";                                                                    // Establecemos para la cabecera "Server", que el servidor utilizado es "".
                request_line= input.readLine();                                                 // Le pasamos a "request_line", la linea de petición del cliente.
                if(request_line.startsWith("GET")){                                             // En el caso de que esta linea empiece con "GET".
                    
                    String resourceFile="";                                                     // Definimos el string "resourceFile", y lo inicializamos a vacío.
                    String parts[]=request_line.split(" ");                                     // Definimos la variable "parts", que será igual al contenido de "request_line" dividido en partes.
                    if(parts.length==3){                                                        // En el caso de que el número de partes sea igual a tres.
                        
                        if(parts[0].equalsIgnoreCase("GET")){                                   // Comprobamos si la primera parte es igual a "GET".
                                                                                                // Comprobamos que la segunda parte sea igual al fichero que tenemos.  
                            if(parts[1].equalsIgnoreCase("/")){                              
                                outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n";   // Le pasamos a "outmesg" el mensaje correspondiente.
                                resourceFile="index.html";                                      // Le pasamos a "resourceFile", el nombre del fichero a abrir.
                                System.out.println("HTTP/1.1 200 OK");                          // Mostramos por pantalla un mensaje de OK.
                                C_type="text/html";                                             // Le pasamos a la cabecera "C_type" el tipo del fichero.   
                            }                                                                   // Fin de la comprobación del fichero "index.html".
                            
                            else {                                                              //En el caso de que el archivo solicitado sea "imagen.jpg".                             
                                outmesg="HTTP/1.1 200 OK \r\nContent-type:image \r\n\r\n";      // Le pasamos a "outmesg" el mensaje correspondiente.
                                resourceFile="";                                                // Vaciamos la variable resourceFile.
                                
                                String recur = parts[1];                                        // Le pasamos al String recur, la parte uno de requestLine.
                                char[] aRecurso = recur.toCharArray();                          //Creamos el vector aRecurso, que se llenará con los caracteres de recur.
                                for (int x=1; x<aRecurso.length; x++){                          // Creamos un bucle for que recorra el vector aRecurso.
                                    resourceFile=resourceFile+aRecurso[x];                      // Rellenamos la variable resourceFile, con el contenido del vector aRecurso.
                                }                                                               // Fin del bucle for.
                                                                                                // Le pasamos a "resourceFile", el nombre del fichero a abrir.
                                System.out.println("HTTP/1.1 200 OK");                          // Mostramos por pantalla un mensaje de OK.
                                C_type="Image";                                                 // Le pasamos a la cabecera "C_type" el tipo del fichero.
                            }                                                                   // Fin de la comprobación del fichero "imagen.jpg".
                            
                            outdata=leerRecurso(resourceFile);                                  // LLamamos a la función "leerRecurso" con el nombre del fichero a leer, para rellenar "outdata".
                            
                            if(outdata==null){                                                  // En el caso de que no se haya encontrado el archivo, y por tanto "outdata" esté vacío.
                                outmesg="HTTP/1.1 404\r\nContent-type:text/html\r\n\r\n <html><body><h1>No encontrado</h1></body></html>"; // Informamos al cliente.
                                outdata=outmesg.getBytes();                                     // La pasamos a "outdata" los bytes de "outmesg".
                                System.out.println("HTTP/1.1 404 Error");                       // Informamos por pantalla del error producido.
                            }                                                                   // Fin del caso de archivo no encontrado.
                            
                            else{                                                               // En el caso de que si se encuentre el fichero.       
                                outmesg="HTTP/1.1 200 OK \r\nContent-type:text/html\r\n\r\n"+outdata+""; //Este se le enviará al cliente.
                            }
                            
                            C_leng=""+outdata.length+"";                                        // Obtenemos el tamaño del recurso solicitado por el cliente, para rellenar la cabecera "C_leng".
                            
                            if(parts[2].equalsIgnoreCase("HTTP/2")){                            // En el caso de que la tercera parte sea "HTTP/2".
                                                                                                // Se informará al cliente, de que está utilizando una versión incorrecta.
                                outmesg="HTTP/1.1 505\r\nContent-type:text/html\r\n\r\n Version del protocolo no compatible";
                                outdata=outmesg.getBytes();                                     // La pasamos a "outdata" los bytes de "outmesg".
                                System.out.println("HTTP/1.1 505 Error");                       // Mostramos el mensaje de error por pantalla.
                            }                                                                   // Fin del caso de versión incorrecta.
                        }                                                                       // Fin de la condición de que la primera parte sea "GET"
                    
                    }else{                                                                      // En el caso de que el número de partes no sea igual a tres.
                        outmesg="HTTP/1.1 400\r\n";                                             // Informamos al cliente del error que se ha producido.
                        outdata=outmesg.getBytes();                                             // La pasamos a "outdata" los bytes de "outmesg".
                        System.out.println("HTTP/1.1 400 Error");                               // Mostramos por pantalla el error que se ha producido.
                    }                                                                           // Fin del caso de error 400.
                
                }else if(request_line.startsWith("Connection")){                                // En el caso de que la línea de petición empiece con "Connection".
                    Conn="Closed";                                                              // Se cambiará la cabecera "Conn" a "Closed".
                
                                                                                                // En el caso de que se intente utilizar un método no permitido en la aplicación.
                }else if(request_line.startsWith("POST") || request_line.startsWith("HEAD") || request_line.startsWith("OPTIONS") || request_line.startsWith("PUT") || request_line.startsWith("DELETE") || request_line.startsWith("TRACE") || request_line.startsWith("CONNECT")){
                                                                                                // Se informará al cliente de que está usando un método no permitido.
                    outmesg="HTTP/1.1 405\r\nContent-type:text/html\r\n\r\n Metodo incorrecto";
                    outdata=outmesg.getBytes();                                                 // La pasamos a "outdata" los bytes de "outmesg".
                    System.out.println("HTTP/1.1 405 Error");                                   // Mostramos por pantalla el error que se ha producido.
                }                                                                               // Fin del caso de método incorrecto.   
            //System.out.println(request_line);    
            }while(request_line.compareTo("")!=0);                                              // Implementamos la condición para que se repita el "do".
            
            // CABECERAS.
            System.out.println("Date: "+fecha+"");                                              // Mostramos por pantalla la cabecera "Date".
            System.out.println("Server: "+Server+"");                                           // Mostramos por pantalla la cabecera "Server".
            System.out.println("Allow: "+Allow+"");                                             // Mostramos por pantalla la cabecera "Allow".
            System.out.println("Content-Type: "+C_type+"");                                     // Mostramos por pantalla la cabecera "Content Type".
            System.out.println("Content-Length: "+C_leng+"");                                   // Mostramos por pantalla la cabecera "Content Length".
            System.out.println("Connection: "+Conn+"");                                         // Mostramos por pantalla la cabecera "Connection".
            System.out.println(" ");                                                            // Añadimos una línea vacía para estilizar la respuesta.
            
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
                               
                i= miArch.read(b);                                     // Se realizará por tanto.
               //         while( ( !=-1 ){                      // Mientras el dato leido de "miArch" no sea menos uno.
                               // datos= new String(b,0,0,i);                     // Vamos rellenando la variable "datos".;
                 //       }                                                       // Fin de la condición del "while".
                        //System.out.println(b.toString());
               
        }catch(FileNotFoundException e){                                        // En el caso de que no se encuentre el archivo.
                System.out.println("Archivo no existe");                        //Informamos por pantalla de lo ocurrido.
                b=null;                                                         // Igualamos la variable "b" a null.
        }  
        catch(IOException e){                                          // En el caso de que se produzca un error.
               System.out.println("Error al leer");   
                b= null;// Informaremos por pantalla del mismo.
        } finally{
            return b; 
        }// Para terminar devolvemos el valor de "b".
    }                                                                           // Fin de la función "leerRecurso".
}                                                                               // Fin de la clase HTTPSocketConnection.