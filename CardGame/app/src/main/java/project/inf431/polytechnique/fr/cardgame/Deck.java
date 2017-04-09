package project.inf431.polytechnique.fr.cardgame;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Deck {

    private static final String TAG = "DECK_TAG";
    private LinkedList<Card> cards;
    public final static int DECK_SIZE = 81;

    public Deck() {
        this.cards = new LinkedList<>();
        for(int i=0; i<DECK_SIZE; i+=1) {
            cards.add(new Card(i));
        }
        Collections.shuffle(cards);
    }

    public ArrayList<Card> popCards(int num) {
        int sz = Math.min(num, this.cards.size());
        ArrayList<Card> gameSet = new ArrayList<>();

        for (int i=0; i<sz; i+=1) {
            gameSet.add(this.cards.remove());
        }

        return gameSet;
    }

    static boolean isSet(Card a, Card b, Card c) {
        int[] cA = a.characteristics();
        int[] cB = b.characteristics();
        int[] cC = c.characteristics();
        Log.v(TAG, "check " + a.toString() + b.toString() + c.toString());
        Log.v(TAG, arrayToString(cA) + "----" + arrayToString(cB) + "----" + arrayToString(cC));
        for (int i=0; i<cA.length; i+=1) {
            if ((cA[i] + cB[i] + cC[i]) % 3 != 0)
                return false;
        }
        return true;
    }

    public int getSize() {
        return this.cards.size();
    }

    public static String arrayToString(int[] a) {
        String res = "{";
        for (int value : a) {
            res += "-" + value;
        }
        res += "}";
        return res;
    }

}
