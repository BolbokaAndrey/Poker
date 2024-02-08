import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Deck {

    /**
     * Перечисление мастей карт
     */
    enum Suits {
        HEARTS,
        SPADES,
        DIAMONDS,
        CLUBS;
    }

    /**
     * Перечисление значений карт
     */
    enum Values{
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE;
    }

    /**
     * Возвращает короткое название масти
     * @param suit
     * @return
     */
    private static String getShortSuit(Suits suit){
        return switch (suit){
            case HEARTS -> "♥";
            case SPADES -> "♠";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
        };
    }

    /**
     * Возвращает короткое название значения карты
     * @param value
     * @return
     */
    private static String getShortValue(Values value){
        return switch (value){
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };
    }

    /**
     * Класс карты
     */
    public static class Card implements Comparable<Card>{
        private Values value;
        private Suits suit;

        Card(Values value, Suits suit){
            this.value = value;
            this.suit = suit;
        }

        public Values getValue() {
            return value;
        }

        public Suits getSuit() {
            return suit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Card card = (Card) o;
            return value == card.value && suit == card.suit;
        }

        public boolean equalsSuit(Card c){
            return value == c.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, suit);
        }

        @Override
        public String toString(){
            return (getShortValue(value) + getShortSuit(suit));
        }

        @Override
        public int compareTo(Card c){
            return getValue().compareTo(c.value);
        }

    }


    private List<Card> deck = new ArrayList<>();

    Deck(){
        for(Values v: Values.values()){
            for(Suits s: Suits.values()){
                deck.add(new Card(v, s));
            }
        }
    }

    public Card getCard(int index){
        return deck.get(index);
    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    @Override
    public String toString() {
        for (Card c: deck){
            System.out.print(c.toString() + " ");
        }
        return "";
    }
}
