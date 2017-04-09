import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class ConnectionList {
    PrintWriter out;
    String login;
    ConnectionList tail;

    ConnectionList(String l, PrintWriter h, ConnectionList tl) {
        login = l;
        out = h;
        tail = tl;
    }

    public void println(String message) {
        this.out.println(message);
    }
}

public class Server {

    public static String GOOD_SET_SIGN = "GOODSET";
    public static String DELETION_SIGN = "DELETION";
    public static String SEND_SIGN = "SEND";
    public static String LOGOUT_SIGN = "LOGOUT";
    public static String LOGIN_SIGN = "LOGIN";
    public static String SET_SIGN = "SET";
    public static String INIT_SIGN = "INIT";
    public static String ADD_SIGN = "ADD";

    public static int CARD_INIT_NUM = 12;

    static HashSet<Integer> handledCards;

    static Lock lock = new ReentrantLock();

    static void sleepSeconds(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
        }
    }

    static public ServerSocket createServer(int server_port) {
        try {
            return new ServerSocket(server_port);
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'attendre sur le port " + server_port);
        }
    }

    static Socket acceptConnection(ServerSocket s) {
        try {
            return s.accept();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de recevoir une connection");
        }
    }

    static Socket establishConnection(String ip, int port) {
        try {
            return new Socket(ip, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Impossible de resoudre l'adresse");
        } catch (IOException e) {
            throw new RuntimeException("Impossible de se connecter a l'adresse");
        }
    }

    static PrintWriter connectionOut(Socket s) {
        try {
            return new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'extraire le flux sortant");
        }
    }

    static BufferedReader connectionIn(Socket s) {
        try {
            return new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'extraire le flux entrant");
        }
    }

    static ConnectionList outs = null;
    static boolean killed = false;
    public static final int SERVER_PORT = 1344;

    static void print_all(String message) {
        ConnectionList cl = outs;

        while (cl != null) {
            cl.out.println(message);
            cl = cl.tail;
        }
    }

    /**
     * to verify whether a set contains cards deleted
     * @param values : cards' values
     * @return : legal set or not
     */
    static boolean readCardSet(ArrayList<String> values){
        for (String value : values) {
            System.out.println("verify : " + value);
            if (handledCards.contains(Integer.parseInt(value))) {
                return false;
            }
        }
        return true;
    }

    static void addSet(ArrayList<String> values) {
        for (String value : values) {
            handledCards.add(Integer.parseInt(value));
        }
    }

    static void broadcastDeletion(String info, String login) {
        ArrayList<String> values = SetGameData.stringToValue(info);
        for (String value : values) {
            SetGameData.deleteCardByValue(value);
        }
        ConnectionList cl = outs;
        while (cl != null) {
            if (cl.login.equals(login)) {
                cl.println(GOOD_SET_SIGN + " " + info);
                System.out.println(GOOD_SET_SIGN + " " + info);
            } else {
                cl.println(DELETION_SIGN + " " + info);
                System.out.println(DELETION_SIGN + " " + info);
            }
            cl = cl.tail;
        }
    }

    public static void main(String args[]) {

        handledCards = new HashSet<>();
        SetGameData.init();

        /** create server socket on the port SERVER_PORT*/
        ServerSocket server = createServer(SERVER_PORT);

        try {
            InetAddress iAddress = InetAddress.getLocalHost();
            String client_IP = iAddress.getHostAddress();
            System.out.println("Current IP address : " + client_IP);
        } catch (UnknownHostException e) {}

        while (!killed) {

            /** client that connects to server socket */
            final Socket s = acceptConnection(server);

            System.out.println("Established a connection with socket : ");
            System.out.println(s.toString());

            /** establish input and output stream for
             * this client socket
             */
            final PrintWriter s_out = connectionOut(s);
            final BufferedReader s_in = connectionIn(s);

            /** create a thread to listen spontaneously
             * to output and input streams
             */
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String my_login = null;
                    try {
                        while (true) {

                            String line;

                            /** read one line from input reader */
                            try {
                                line = s_in.readLine();
                                System.out.println(my_login + " : " + line);
                            } catch (IOException e) {
                                throw new RuntimeException("Cannot read from socket");
                            }

                            Scanner sc = new Scanner(line);
                            sc.useDelimiter(" ");

                            String token = sc.next();
                            if (my_login != null) {
                                if (token.equals(SEND_SIGN)) {
                                    sc.useDelimiter("\n");
                                    String message = sc.next();
                                    print_all(my_login + ":" + message);
                                } else if (token.equals(LOGOUT_SIGN)) {
                                    s_out.println("SHUT DOWN : " + my_login);
                                    throw new RuntimeException("Requested by user");
                                } else if (token.equals(SET_SIGN)){
                                    /** a sent set is necessarily a legal one, we need to
                                     * verify if any card in this set is already deleted by
                                     * other players
                                     */
                                    String info = sc.next();
                                    System.out.println("SET DELETION REQUEST : " + info + " --- by user : " + my_login);
                                    /** we enter into critic segment, only one thread can manipulate
                                     * HashSet handledCards.
                                     */
                                    lock.lock();
                                    try {
                                        System.out.println("Begin to verify " + my_login + "'s request.");
                                        ArrayList<String> values = SetGameData.stringToValue(sc.next());
                                        System.out.println("Decoded request : " + values.toString());
                                        if (readCardSet(values)) {
                                            broadcastDeletion(info, my_login);
                                            addSet(values);
                                        } else {
                                            s_out.println("TOO LATE!");
                                        }

                                        /** if there is not set in the cards, add cards until
                                         * deck is empty or till cards size is 15
                                         */
                                        if (!SetGameData.existenceOfSet()) {
                                            SetGameData.addCards(15 - SetGameData.getCards().size());
                                        }
                                    }finally {
                                        lock.unlock();
                                    }
                                } else
                                    throw new RuntimeException("Unknown Command");
                            } else if (token.equals(LOGIN_SIGN)) {
                                /** initiate an account for user*/
                                my_login = sc.next();
                                /** create a connection info list for this socket*/
                                ConnectionList cl = outs;
                                while (cl != null) {
                                    if (cl.login.equals(my_login)) {
                                        my_login = null;
                                        throw new RuntimeException("Login already used");
                                    }
                                    cl = cl.tail;
                                }
                                outs = new ConnectionList(my_login, s_out, outs);
                                print_all("Welcome " + my_login);
                                /** send initial cards to all terminals*/
                                s_out.println(INIT_SIGN + " "+ SetGameData.cardsToString());
                            } else if (token.equals("KILL")) {
                                killed = true;
                                throw new RuntimeException("Waiting for next connection to kill");
                            } else {
                                throw new RuntimeException("Expecting LOGIN command");
                            }
                        }
                    } catch (RuntimeException error) {
                        s_out.println("DISCONNECTED: exn " + error);
                        s_out.flush();
                        try {
                            s.close();
                        } catch (IOException e) { }
                        if (my_login != null) {
                            ConnectionList cl = null;
                            while (outs != null) {
                                if (outs.out == s_out) {
                                } else {
                                    cl = new ConnectionList(outs.login, outs.out, cl);
                                }
                                outs = outs.tail;
                            }
                            outs = cl;
                            print_all(my_login + " left.");
                        }
                    }
                }
            });
            t.start();
        }
        System.exit(0);
    }

}
