package project.inf431.polytechnique.fr.cardgame;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder>{

    public final static String TAG = "CardAdapter TAG";

    private List<Card> cards;
    private GameActivity gameActivity;
    /** flag to mark selected items */
    private SparseBooleanArray selectedItems;

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            if (getSelectedItemCount() >= 3) {
                Log.v(TAG, "Already 3 selected cards! ");
                return;
            }
            selectedItems.put(pos, true);
        }

        /** mark selected card */
        Card c = getCard(pos);
        if(selectedItems.get(pos, false)) {
            c.setChosen();
        } else {
            c.setInit();
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /** get selected items in decreased order
     * @return : selected items' positions
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = selectedItems.size() - 1; i >=0 ; i-=1) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    /** insert a card at position given
     * @param card : target card
     * @param position : location
     */
    public void addCard(Card card, int position) {
        this.cards.add(position, card);
        notifyItemInserted(position);
    }

    /** add cards from the deck, the amount is num
     * @param num : num of cards to add from deck
     */
    public void addCards(int num) {
        List<Card> l = SetGameData.getDeck().popCards(num);
        for (Card c : l) {
            addCard(c, 0);
        }
    }

    /** add cards by server's command
     * @param cards : list of cards to add
     */
    public void addCards(List<Card> cards) {
        for(Card c : cards) {
            int position = cards.size() - 1;
            addCard(c, position);
        }
    }

    public void removeCard(int position) {
        Log.v(TAG, "remove card : " + position);
        if (gameActivity.isOnline()) {
            /** with internet connexion */
            this.cards.remove(position);
            selectedItems.delete(position);
            Log.v(TAG, "remove card "+position+" !");
        } else {
            /** without internet connexion */
            cards.remove(position);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cards.size());
    }

    public void removeSet(List<Integer> positions) {
        for (int pos : positions) {
            removeCard(pos);
        }
        clearSelections();
    }

    private void addItemToList(Card card) {
        int position = ((GridLayoutManager) gameActivity.getCardListView().getLayoutManager()).
                findFirstVisibleItemPosition();
        SetGameData.addItemToList(card, position);
        notifyItemInserted(position);
    }

    public Card getCard(int position) {
        return cards.get(position);
    }

    public CardAdapter(List<Card> list, GameActivity gameActivity) {
        this.cards = list;
        selectedItems = new SparseBooleanArray();
        this.gameActivity = gameActivity;
    }

    public void deleteCardByValue(String value) {
        for(int i=0; i<cards.size(); i+=1) {
            if (cards.get(i).getValue() == Integer.parseInt(value)) {
                removeCard(i);
                break;
            }
        }
    }

    public String positionsToString(List<Integer> positions) {
        String res = "";
        for (int pos : positions) {
            res = res + getCard(pos).toString();
        }
        return res;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_element,viewGroup,false);
        return new CardViewHolder(view, gameActivity);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, int position) {
        Card c = cards.get(position);
        cardViewHolder.getCustomCardView().setCard(c);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

}
