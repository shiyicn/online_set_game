package project.inf431.polytechnique.fr.cardgame;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private DrawableCard customCardView;
    public static String TAG = "CARD_VIEW_HOLDER";
    private GameActivity gameActivity;

    public CardViewHolder(View itemView, GameActivity gameActivity) {
        super(itemView);
        customCardView = (DrawableCard) itemView.findViewById(R.id.content_card);
        this.gameActivity = gameActivity;
        itemView.setOnClickListener(this);
    }

    /**
     * fill the card view with card object
     * @param card: card to display
     */
    public void bind(Card card){
        customCardView.setCard(card);
    }

    public DrawableCard getCustomCardView() {
        return customCardView;
    }

    @Override
    public void onClick(View v) {
        int idx = getAdapterPosition();
        Log.v(TAG, "Clicked card idx : " + idx);
        Log.v(TAG, "Card Info : " + customCardView.getCard().getValue());
        gameActivity.mOnItemClick(idx);
    }
}
