package project.inf431.polytechnique.fr.cardgame.sync;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import project.inf431.polytechnique.fr.cardgame.CardAdapter;

public class Client extends AsyncTask<Void, Void, Void>{

    private static String TAG = "Client";
    private static long MAX_TIME = 100;

    private static String LOGIN_SIGN = "LOGIN";

    private String serverIP;
    private int serverPort;
    private BufferedReader s_in;
    private PrintWriter s_out;
    private PrintWriter server_out;
    private String id;
    private CardAdapter mCardAdapter;
    private boolean flag;

    public Client(String serverIP, int serverPort, String id, CardAdapter cardAdapter) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.id = id;
        this.s_in = null;
        this.s_out = null;
        this.server_out = null;
        this.mCardAdapter = cardAdapter;
        this.flag = true;
    }

    public void send(String message) {
        s_out.println(id + " " + message);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Socket s = null;
        long startTime = System.currentTimeMillis();
        long connexionTimeCost;
        while(s == null) {
            connexionTimeCost = System.currentTimeMillis() - startTime;
            /** if connection time is longer than
             * MAX_TIME limit end the connexion request
             */
            if(connexionTimeCost > MAX_TIME) {
                this.flag = false;
                break;
            }
            try {
                s = new Socket(serverIP, serverPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (flag) {
            /** setup input and output */
            s_in = Net.connectionIn(s);
            s_out = Net.connectionOut(s);
            /** send login request */
            s_out.println(LOGIN_SIGN + " " + id);
            String line;
            try {
                /** rend the first welcome message from server */
                line = s_in.readLine();
            } catch (IOException e) {
                throw new RuntimeException("in readLine");
            }
            Log.v(TAG, line);
            Scanner sc = new Scanner(line);
            sc.useDelimiter(" ");
            if (sc.next().equals("Welcome")) {
                Log.v(TAG, "ACCEPTED BY SERVER!");
                server_out = s_out;
            }
            this.flag = true;
        }
        return null;
    }

    public void sendSetInfo(List<Integer> positions) {
        String set = " ";
        for (int pos : positions) {
            set += mCardAdapter.getCard(pos).getValue() + " ";
        }
        this.server_out.print(set);
    }

    /** getters to private variables */
    public BufferedReader getS_in() {
        return s_in;
    }

    public PrintWriter getS_out() {
        return s_out;
    }

    public PrintWriter getServer_out() {
        return server_out;
    }

    public String getId() {
        return id;
    }

    public boolean getFlag() {return flag;}
}
