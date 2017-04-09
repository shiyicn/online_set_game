package project.inf431.polytechnique.fr.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import project.inf431.polytechnique.fr.cardgame.sync.Client;
import project.inf431.polytechnique.fr.cardgame.sync.Server;

import static project.inf431.polytechnique.fr.cardgame.MainActivity.EXTRA_LOGIN;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "project.inf431.polytechnique.fr.SCORE";
    private RecyclerView cardListView;

    public final static int NUM_CARDS = 12;
    public final static int NUM_MAX_CARDS = 15;

    public final static String TAG = "GAME_ACTIVITY_TAG";

    /** card data management */
    CardAdapter mCardAdapter;
    private ArrayList<Card> cards;
    int num;

    public static final String SERVER_IP = "192.168.43.11";
    public static final int SERVER_PORT = 1344;

    /** command signs for the communication between server
     * and clients
     */
    public static String DELETION_SIGN = Server.DELETION_SIGN;
    public static String GOOD_SET_SIGN = Server.GOOD_SET_SIGN;
    public static String SET_REQUEST_SIGN = Server.SET_SIGN;

    /** client and login account option */
    private String my_login;
    private Client client;
    private String connexion;
    private boolean isOnline;
    static PrintWriter server_out = null;

    /** player's score */
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        num = intent.getIntExtra(MainActivity.EXTRA_NUM_CARD, 12);
        my_login = intent.getStringExtra(EXTRA_LOGIN);
        connexion = intent.getStringExtra(MainActivity.EXTRA_CONNEXION_FLAG);


        if (connexion.equals(
                getString(R.string.offline_mode)
        )) {
            //initialise data set locally
            SetGameData.init();
            cards = SetGameData.getCards();
            if (!SetGameData.existenceOfSet(cards)) {
                Log.v(TAG, "Add several cards to build set.");
                mCardAdapter.addCards(
                        Math.min(
                                NUM_MAX_CARDS-cards.size(),
                                SetGameData.getDeck().getSize()
                        )
                );
            }
            isOnline = false;
        } else if (connexion.equals(
                getString(R.string.online_mode))) {
            /** try to build connection with centre server*/
            buildConnexion(my_login);
            if (!client.getFlag()) {
                setContentView(R.layout.connexion_error_message_activity);
                Toast.makeText(
                        getApplicationContext(),
                        "Cannot connect to server IP : " + SERVER_IP + " with port num : " + SERVER_PORT,
                        Toast.LENGTH_SHORT
                ).show();
                Toast.makeText(
                        getApplicationContext(),
                        "Please choose offline version! ",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            isOnline = true;
        }

        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise card view recycler
        cardListView = (RecyclerView) findViewById(R.id.cardList_recycler_view);
        cardListView.setLayoutManager(new GridLayoutManager(this, 3));

        //setup card list view adapter
        mCardAdapter = new CardAdapter(cards, this);
        cardListView.setAdapter(mCardAdapter);

        //setup score show panel listener
        FloatingActionButton showScore = (FloatingActionButton) findViewById(R.id.score_card_fab);

        //setup check and delete listener for selected cards
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.delete_card_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> selectedItemPositions = mCardAdapter.getSelectedItems();
                String info = mCardAdapter.positionsToString(selectedItemPositions);
                if (isSet(selectedItemPositions)) {
                    if (isOnline){
                        /** online mode, send message to server */
                        Log.v(TAG, SET_REQUEST_SIGN + " " + info);
                        sendMessage(SET_REQUEST_SIGN + " " + info);
                    } else {
                        /** offline mode, delete directly set cards */
                        mCardAdapter.removeSet(selectedItemPositions);
                        score += 1;
                        /** check locally if there exists a set in cards */
                        if (!SetGameData.existenceOfSet(cards)){
                            mCardAdapter.addCards(
                                    Math.min(
                                            NUM_MAX_CARDS-cards.size(),
                                            SetGameData.getDeck().getSize()
                                    )
                            );
                        }
                    }
                } else {
                    Log.v(TAG, "not a valid set! ");
                }
            }
        });

        /** initiate score to zero */
        score = 0;
    }

    private boolean isSet(List<Integer> cardSelected){
        if (cardSelected.size() != 3) {

            return false;
        }

        int[] items = new int[cardSelected.size()];
        int i = 0;
        for (int pos : cardSelected) {
            items[i] = pos;
            i += 1;
        }

        return SetGameData.getDeck().isSet(
                cards.get(items[0]),
                cards.get(items[1]),
                cards.get(items[2]));
    }

    /** Called when the user taps the start button
     * to build a connexion between local terminal and
     * centre server
     */
    public void buildConnexion(final String login) {
        try {

            /** for every game player, we create a client instance which
             * contains all information of input and output stream
             */
            client = new Client(SERVER_IP, SERVER_PORT, login, mCardAdapter);
            client.execute();

            if (!client.getFlag()) {
                /** failed to connect to server */
                return;
            }

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    final BufferedReader fromServer = client.getS_in();
                    final PrintWriter toServer = client.getS_out();

                    Thread receiver = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    String msg = fromServer.readLine();
                                    if (msg == null) {
                                        continue;
                                    }
                                    Log.v(TAG, "message from server : ");
                                    Log.v(TAG, msg);
                                    Scanner scanner = new Scanner(msg);
                                    scanner.useDelimiter(" ");
                                    String sign = scanner.next();

                                    if (sign.equals(Server.INIT_SIGN)) {
                                        Log.v(TAG, "initialise from server!");
                                        cards = SetGameData.stringToCardList(scanner.next());
                                    }

                                    if (sign.equals(Server.ADD_SIGN)) {
                                        final String cardsToAdd = scanner.next();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mCardAdapter.addCards(
                                                        SetGameData.stringToCardList(cardsToAdd)
                                                );
                                            }
                                        });
                                    }

                                    if (sign.equals(DELETION_SIGN) || sign.equals(GOOD_SET_SIGN)) {
                                        ArrayList<String> values = SetGameData.stringToValue(scanner.next());
                                        final ArrayList<String> set = values;
                                        /** change to UI thread to remove cards*/
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (String v : set) {
                                                    mCardAdapter.deleteCardByValue(v);
                                                }
                                            }
                                        });
                                        if (sign.equals(GOOD_SET_SIGN)) {
                                            /** good set means that player wins a point*/
                                            score += 1;
                                        }
                                    } else {
                                        throw new RuntimeException("Unknown command exception");
                                    }
                                }
                            }catch (IOException ie) {
                                System.out.println("ERROR in reading messages from server !");
                            }
                        }
                    });

                    receiver.start();
                }
            });

            t.start();

        } catch (RuntimeException msg){
            String errorMessage = "Error : " + msg;
            Toast.makeText(
                    getApplicationContext(),
                    errorMessage,
                    Toast.LENGTH_SHORT
            ).show();
            Log.v(TAG, "Error " + msg);
        }
    }

    /** send message function
     * @param message : message to send
     */
    public void sendMessage(String message) {
        final String msg = message;
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                client.getS_out().println(msg);
            }
        });
        sender.start();
        try {
            sender.join();
        }catch (InterruptedException ie) {
            Log.v(TAG, "send message : " + msg + ". ==> interrupted! ");
        }
    }

    /**
     * response function to click action
     * @param idx : item id in the list viewer
     * @return clicked item
     */
    public Card mOnItemClick(int idx) {
        // response to click action on items
        mCardAdapter.toggleSelection(idx);
        return mCardAdapter.getCard(idx);
    }

    public void showMyScore(View view) {
        Log.v(TAG, my_login);
        Intent intent = new Intent(this, ScoreShowActivity.class);
        intent.putExtra(EXTRA_LOGIN, my_login);
        intent.putExtra(EXTRA_SCORE, score);
        startActivity(intent);
    }

    /** getters and setters to private variables*/
    public Client getClient() {
        return this.client;
    }

    public RecyclerView getCardListView() {
        return cardListView;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
