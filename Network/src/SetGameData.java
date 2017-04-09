
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class SetGameData {

    private static final String TAG = "DATA_SET_TAG";
    private static ArrayList<Card> mCards;
    private static Deck deck;
    private static HashMap<Integer, Card> mCardMap;
    private static int NUM_CARDS = Server.CARD_INIT_NUM;

    public static void init() {
        deck = new Deck();
        mCards = new ArrayList<>();
        mCardMap = new HashMap<>();
        int position = 0;
        for (Card c : deck.popCards(NUM_CARDS)) {
            addItemToList(c, position);
            position += 1;
        }
    }

    /** add a card in a certain position
     * @param card : card to add
     * @param position : position to insert
     * @return : cards changed
     */
    public static final ArrayList<Card> addItemToList(Card card, int position) {
        mCards.add(position, card);
        mCardMap.put(card.getValue(), card);
        return new ArrayList<>(mCards);
    }

    /** remove item from cards in a
     * certain position
     * @param position : location of card to delete
     * @return : cards changed
     */
    public static final ArrayList<Card> removeItemFromList(int position) {
        mCardMap.remove(mCards.get(position).getValue());
        mCards.remove(position);
        return new ArrayList<>(mCards);
    }

    /** convert cards list to string chain
     * Ex : {card(10), card(11), card(13)} => -10-11-13
     * @return : string chain
     */
    public static String cardsToString() {
        return cardsToString(mCards);
    }

    /** check the existence of valid set in
     * the cards displayed
     * @return : existence flag
     */
    public static boolean existenceOfSet(ArrayList<Card> cards) {
        for (int i=0; i<cards.size(); i+=1) {
            for (int j=i+1; j<cards.size(); j+=1) {
                for (int k=j+1; k<cards.size(); k+=1) {
                    if (Deck.isSet(cards.get(i), cards.get(j), cards.get(k))) {
                        System.out.println("Find a valid set : { " + i + " " + j + " " + k + " }");
                        return true;
                    }
                }
            }
        }
        System.out.println("There is no valid sets in cards! ");
        return false;
    }

    public static boolean existenceOfSet() {
        return existenceOfSet(mCards);
    }

    /** add several cards from deck
     * @param num : amount of cards to add
     */
    public static ArrayList<Card> addCards(int num) {
        ArrayList<Card> cardsToAdd = deck.popCards(num);
        for (Card c : cardsToAdd) {
            addItemToList(c, mCards.size());
        }
        return cardsToAdd;
    }

    /** remove cards from attributed cards
     * @param cardsToRemove : target cards
     */
    public static void removeCards(ArrayList<Card> cardsToRemove) {
        for (Card c : cardsToRemove) {
            for (int i=0; i<mCards.size(); i+=1) {
                if (c.equals(mCards.get(i))) {
                    removeItemFromList(i);
                }
            }
        }
    }

    /** Encodes cards list to String
     * Ex : {card(1), card(2), card(3)} => "-1-2-3"
     * @param cardsToEncode
     * @return
     */
    public static String cardsToString(ArrayList<Card> cardsToEncode) {
        String res = "";
        for (Card c : cardsToEncode) {
            res = res + "-" + c.getValue();
        }
        return res;
    }

    /** convert string chain to string set
     * Ex : "-10-22-33" to {"10", "22", "33"}
     * @param info : set chain connected by '-'
     * @return : separated card string value
     */
    public static ArrayList<String> stringToValue(String info) {
        Scanner scanner = new Scanner(info);
        scanner.useDelimiter("-");
        ArrayList<String> values = new ArrayList<>();
        while(scanner.hasNext()) {
            values.add(scanner.next());
        }
        return values;
    }

    /** delete a card by its value
     * @param value : card value
     */
    public static void deleteCardByValue(String value) {
        for(int i=0; i<mCards.size(); i+=1) {
            if (mCards.get(i).getValue() == Integer.parseInt(value)) {
                removeItemFromList(i);
                break;
            }
        }
    }

    public static Card getItemAtCards(int position) {
        return mCards.get(position);
    }

    /** convert string chain to card set
     * Ex : "-10-22-33" to {card(10), card(22), card(33)}
     * @param info : card value chain connected by '-'
     * @return : card list
     */
    public static ArrayList<Card> stringToCardList(String info) {
        ArrayList<String> values = stringToValue(info);
        ArrayList<Card> l = new ArrayList<>();
        for (String value : values) {
            l.add(new Card(Integer.parseInt(value)));
        }
        return l;
    }

    /** getters and setters */
    public static final ArrayList<Card> getCards() {
        return new ArrayList<>(mCards);
    }

    public static Deck getDeck() {
        return deck;
    }

}
