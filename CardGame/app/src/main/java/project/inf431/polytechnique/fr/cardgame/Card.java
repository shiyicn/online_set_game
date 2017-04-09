package project.inf431.polytechnique.fr.cardgame;

public class Card {

    private String label;
    private int number;
    private int color;
    private int filling;
    private int shape;
    private int value; // value = number + 3 * (color + 3 * (filling + 3 * shape))
    private int selected;

    Card(int value) {
        this.value = value;
        // compute the other attributes from value
        number = value%3 + 1;
        color = (value/3)%3 + 1;
        filling = (value/9)%3 + 1;
        shape = (value/27)%3 + 1;
        this.selected = -2;
    }

    // equality test on SetCards
    @Override
    public boolean equals(Object o) {
        Card c = (Card) o;
        return (value == c.value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    public String toString() {
        return "-" + this.value;
    }

    // return the characteristics of the card
    int[] characteristics() {
        return new int[] {number, color, filling, shape};
    }

    /** getters to private variables */
    public int getNumber() {
        return number;
    }

    public int getColor() {
        return color;
    }

    public int getFilling() {
        return filling;
    }

    public int getShape() {
        return shape;
    }

    public int getValue() {
        return value;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setChosen() {
        this.selected = -1;
    }

    public void setGoodSet() {
        this.selected = 0;
    }

    public void setBadSet() {
        this.selected = 1;
    }

    public void setInit() {
        this.selected = -2;
    }
}