import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;


public class ServerSimple {

    protected static String SERVER_IP = "129.104.99.29";
    protected static int SERVER_PORT = 1344;

    public static void main(String[] args) throws IOException {

        printSocketInfo();

        ServerSocket server = new ServerSocket(SERVER_PORT);
        while(true) {
            try {
                System.out.println("Waiting for a client socket!");
                Socket s = server.accept();
                System.out.println("One client is connected !");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            Thread receive = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (true) {
                                            String msg = in.readLine();
                                            System.out.println(msg);
                                        }
                                    }catch (IOException ie) {
                                        System.out.println("ERROR in reading messages from server !");
                                    }
                                }
                            });

                            receive.run();
                        }catch (IOException ie) {
                        }
                    }
                });

                t.start();
            } catch (IOException e) {
            }
        }
    }

    static void printSocketInfo() {
        try {
            InetAddress iAddress = InetAddress.getLocalHost();
            String client_IP = iAddress.getHostAddress();
            System.out.println("Current Client IP address : " + client_IP);
        }catch (UnknownHostException e) {
            System.out.println("FATAL ERROR!");
        }
    }

}