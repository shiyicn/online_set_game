package project.inf431.polytechnique.fr.cardgame.sync;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

// Classe proposant des primitives pour construire facilement
// une architecture client/serveur.
public class Net {

    private final static String TAG = "Net";

    /*
     * createServer: Create a ServerSocket listening on port 'server_port'
     * 
     * @input int server_port: port attached to the server socket
     * @output ServerSocket
     * 
     */
    static public ServerSocket createServer(int server_port){ 	
    	try {
            return new ServerSocket(server_port);
    	} catch (IOException e) {
            Log.v(TAG, "Net error!");
            throw new RuntimeException("Impossible to wait on the port "
                                       + server_port);
    	}
    }

    /*
     * acceptConnection: Accept an incoming connection on the
     * ServerSocket 's' and return a dedicated Socket to communicate
     * with the client.
     * 
     * @input ServerSocket s
     * @output Socket: Socket for communication with the client.
     * 
     */
    static public Socket acceptConnection(ServerSocket s) {
        try {
            return s.accept();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de recevoir une connection");
        }
    }

    /*
     * establishConnections: Create a connection towards a server at IP 'ip'
     * listening on port 'port'. 
     * 
     * @input String ip: Destination IP address 
     * @input int port: Destination port
     * @output Socket: Socket for communication with the server
     * 
     */
    static public Socket establishConnection(String ip, int port) {
        try {
            return new Socket(ip, port);
        } 
        catch (UnknownHostException e){
            throw new RuntimeException("Impossible de resoudre l'adresse");
        }
        catch (IOException e){
            throw new RuntimeException("Impossible de se connecter a l'adresse");	
        }
    }

    /*
     * connectionOut: Create a PrintWriter object writing to the socket 's'.
     * 
     * @input Socket s: Socket to write to
     * @output PrintWriter: Object managing the writing on the socket 's'
     * 
     */
    static public PrintWriter connectionOut(Socket s){
    	try {
            return new PrintWriter(s.getOutputStream(),	true);
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'extraire le flux sortant");
        }
    }
    
    /*
     * connectionIn: Creates a BufferedReader object to read on socket 's'.
     * 
     * @input Socket s: Socket to read from
     * @output BufferedReader: Object managing the reading on the socket 's'
     * 
     */
    static public BufferedReader connectionIn(Socket s){
    	try {
            return new BufferedReader(new InputStreamReader(s.getInputStream()));
    	} catch (IOException e) {
            throw new RuntimeException("Impossible d'extraire le flux entrant");
    	}
    }

}
