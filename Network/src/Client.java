import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client{

    private static final int serverPort = Server.SERVER_PORT;
    protected static String serverIP = Server.SERVER_IP;
    protected static String clientIP;


    public static void main(String[] args) throws IOException {
        initialNetInfo();
        Socket socket = new Socket(serverIP, serverPort);
        initialize(socket);
    }

    private static int initialize(Socket socket) throws IOException {

        final Socket s = socket;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                    OutputStream os = s.getOutputStream();
                    BufferedReader fromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                    PrintWriter toServer = new PrintWriter(os, true);

                    toServer.println("LOGIN Fake");

                    Thread receive = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    String msg = fromServer.readLine();
                                    System.out.println(msg);
                                }
                            }catch (IOException ie) {
                                System.out.println("ERROR in reading messages from server !");
                            }
                        }
                    });

                    receive.start();

                    Thread send = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    String msg = userInput.readLine();
                                    toServer.println(msg);
                                }
                            } catch (IOException ie) {
                                System.out.println("ERROR in reading user input and send message");
                            }
                        }
                    });

                    send.start();
                }catch (IOException ie) {}
            }
        });

        t.start();

        return 0;
    }

    public static void initialNetInfo() {
        try {
            InetAddress iAddress = InetAddress.getLocalHost();
            clientIP = iAddress.getHostAddress();
            System.out.println("Current Client IP address : " + clientIP);
        } catch (UnknownHostException e) {
            System.out.println("Unable to initialise network info");
        }

    }
}