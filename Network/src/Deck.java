import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Deck {

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
        for (int i=0; i<cA.length; i+=1) {
            if ((cA[i] + cB[i] + cC[i]) % 3 != 0)
                return false;
        }
        return true;
    }

    public int getSize() {
        return this.cards.size();
    }

}
