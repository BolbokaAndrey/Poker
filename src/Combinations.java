import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combinations implements Comparable<Combinations>{

    private List<Deck.Card> combination;
    private Deck.Card kicker1;
    private Deck.Card kicker2;
    private Deck.Card kicker3;
    private Deck.Card kicker4;
    private Deck.Card kicker5;
    private Deck.Card high_in_comb1;
    private Deck.Card high_in_comb2;
    private Deck.Card high_in_comb3;
    private Deck.Card high_in_comb4;
    private Deck.Card high_in_comb5;
    private ECombinations value;
    private Deck.Values valueOfSetFromFullHouse;

    enum ECombinations{
        HIGH_CARD,
        PAIR,
        TWO_PAIRS,
        SET,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        QUADS,
        STRAIGHT_FLUSH,
        ROYAL_FLUSH;
    }

    Combinations(List<Deck.Card> combination){
        Collections.sort(combination);
        this.combination = combination;
        witchComb();
    }

    @Override
    public int compareTo(Combinations o) {
        int res_comb = value.compareTo(o.value);
        if (res_comb == 0) {
            if (!value.name().equals("ROYAL_FLUSH")){
                if (value.name().equals("HIGH_CARD")) return compareKicker(o);

                int res_h1 = high_in_comb1.compareTo(o.high_in_comb1);
                if (res_h1 == 0){
                    int res_k = 0;
                    if (!(value.name().equals("TWO_PAIRS") || value.name().equals("FULL_HOUSE"))){
                        res_k = compareKicker(o);
                        if (res_k != 0) return res_k;
                    }
                    if (high_in_comb2 == null) return 0;
                    int res_h2 = high_in_comb2.compareTo(o.high_in_comb2);
                    if (res_h2 == 0){
                        res_k = compareKicker(o);
                        if (res_k != 0) return res_k;
                        if (high_in_comb3 == null) return 0;
                        int res_h3 = high_in_comb3.compareTo(o.high_in_comb3);
                        if (res_h3 == 0){
                            if (high_in_comb4 == null) return 0;
                            int res_h4 = high_in_comb4.compareTo(o.high_in_comb4);
                            if (res_h4 == 0){
                                if (high_in_comb5 == null) return 0;
                                return high_in_comb5.compareTo(o.high_in_comb5);
                            }
                            return res_h4;
                        }
                        return res_h3;
                    }
                    return res_h2;
                }
                return res_h1;
            }
        }
        return res_comb;
    }

    @Override
    public String toString() {
        return combination.toString();
    }

    private int compareKicker(Combinations o){
        if (value.name().equals("PAIR") || value.name().equals("TWO_PAIRS") ||
                value.name().equals("SET") || value.name().equals("QUADS")) {
            int res_k1 = kicker1.compareTo(o.kicker1);
            if (res_k1 == 0){
                if (kicker2 == null) return 0;
                int res_k2 = kicker2.compareTo(o.kicker2);
                if (res_k2 == 0) {
                    if (kicker3 == null) return 0;
                    int res_k3 = kicker3.compareTo(o.kicker3);
                    if (res_k3 == 0) {
                        if (kicker4 == null) return 0;
                        int res_k4 = kicker4.compareTo(o.kicker4);
                        if (res_k4 == 0){
                            return kicker5.compareTo(o.kicker5);
                        }
                        return res_k4;
                    }
                    return res_k3;
                }
                return res_k2;
            }
            return res_k1;
        }
        return 0;
    }


    private void witchComb(){
        kicker1 = null;
        kicker2 = null;
        kicker3 = null;
        kicker4 = null;
        kicker5 = null;
        high_in_comb1 = null;
        high_in_comb2 = null;
        high_in_comb3 = null;
        high_in_comb4 = null;
        high_in_comb5 = null;

        if (isRoyalFlush()) {
            value = Combinations.ECombinations.ROYAL_FLUSH;
        } else if (isStraightFlush()) {
            value = Combinations.ECombinations.STRAIGHT_FLUSH;
        }else if (isQuads()) {
            value = Combinations.ECombinations.QUADS;
        }else if (isFullHouse()) {
            value = Combinations.ECombinations.FULL_HOUSE;
        }else if (isFlush()) {
            value = Combinations.ECombinations.FLUSH;
        }else if (isStraigth()) {
            value = Combinations.ECombinations.STRAIGHT;
        }else if (isSet()) {
            value = Combinations.ECombinations.SET;
        } else if (isTwoPair()) {
            value = Combinations.ECombinations.TWO_PAIRS;
        }else if (isPair()) {
            value = Combinations.ECombinations.PAIR;
        } else {
            kicker1 = combination.getLast();
            kicker2 = combination.get(3);
            kicker3 = combination.get(2);
            kicker4 = combination.get(1);
            kicker5 = combination.getFirst();
            value = Combinations.ECombinations.HIGH_CARD;
        }
    }





    /**
     * Проверка на флэш роял
     */
    public boolean isRoyalFlush(){
        if(isStraightFlush())
            return combination.getFirst().getValue() == Deck.Values.TEN;
        return false;
    }

    /**
     * Проверка на стрит-флэш
     */
    public boolean isStraightFlush(){
        if (isStraigth() && isFlush()){
            high_in_comb1 = combination.getLast();
            return true;
        }
        return false;
    }

    /**
     * Проверка на каре
     */
    public boolean isQuads(){
        for(int i = 0; i < 2; i++){
            Deck.Values temp = combination.get(i).getValue();
            if(temp == combination.get(i + 1).getValue() &&
                    temp == combination.get(i + 2).getValue() &&
                    temp == combination.get(i + 3).getValue()){
                if (i == 0) {
                    kicker1 = combination.getLast();
                } else {
                    kicker1 = combination.getFirst();
                }
                high_in_comb1 = combination.get(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на фул хаус
     */
    public boolean isFullHouse(){
        if(!isSet()) return false;
        if(valueOfSetFromFullHouse == combination.getFirst().getValue())
            if (combination.getLast().getValue() == combination.get(3).getValue()) {
                high_in_comb1 = combination.getFirst();
                high_in_comb2 = combination.getLast();
                return true;
            }
        if(valueOfSetFromFullHouse == combination.getLast().getValue())
            if (combination.getFirst().getValue() == combination.get(1).getValue()) {
                high_in_comb1 = combination.getLast();
                high_in_comb2 = combination.getFirst();
                return true;
            }
        return false;
    }

    /**
     * Проверка на флэш
     */
    public boolean isFlush(){
        Deck.Suits check_suit = combination.getFirst().getSuit();
        for(Deck.Card c: combination){
            if (c.getSuit() != check_suit) {
                return false;
            }
        }
        high_in_comb1 = combination.getLast();
        high_in_comb2 = combination.get(3);
        high_in_comb3 = combination.get(2);
        high_in_comb4 = combination.get(1);
        high_in_comb5 = combination.getFirst();
        return true;
    }

    /**
     * Проверка на стрит
     */
    public boolean isStraigth(){
        if(isWheel()) return true;

        int start_ordinal = combination.getFirst().getValue().ordinal();
        for(int i = 1; i < 5; i++){
            int check_ordinal = combination.get(i).getValue().ordinal();
            if(check_ordinal - start_ordinal != 1){
                return false;
            }
            start_ordinal = check_ordinal;
        }
        high_in_comb1 = combination.getLast();
        return true;
    }

    private boolean isWheel(){
        if(combination.get(0).getValue() != Deck.Values.TWO) return false;
        if(combination.get(1).getValue() != Deck.Values.THREE) return false;
        if(combination.get(2).getValue() != Deck.Values.FOUR) return false;
        if(combination.get(3).getValue() != Deck.Values.FIVE) return false;
        if(combination.get(4).getValue() != Deck.Values.ACE) return false;
        high_in_comb1 = combination.get(3);
        return true;
    }

    /**
     * проверка на сет
     */
    public boolean isSet(){
        for(int i = 0; i < 3; i++){
            Deck.Values temp = combination.get(i).getValue();
            if(temp == combination.get(i + 1).getValue() &&
                    temp == combination.get(i + 2).getValue()){
                if (i == 0) {
                    kicker1 = combination.getLast();
                    kicker2 = combination.get(3);
                } else if (i == 1) {
                    kicker1 = combination.getLast();
                    kicker2 = combination.getFirst();
                } else {
                    kicker1 = combination.get(1);
                    kicker2 = combination.getFirst();
                }
                high_in_comb1 = combination.get(i);
                valueOfSetFromFullHouse = temp;
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на две пары
     */
    public boolean isTwoPair(){
        int count = 0;
        int first = -1;
        int second = -1;
        for(int i = 0; i < 4; i++){
            if(combination.get(i).getValue() == combination.get(i + 1).getValue()){
                if (first == -1) {
                    first = i;
                } else {
                    second = i;
                }
                count++;
            }
        }

        if (first == 0) {
            if (second == 2){
                kicker1 = combination.getLast();
            } else if (second == 3) {
                kicker1 = combination.get(2);
            }
        } else if (first == 1 && second == 3) {
            kicker1 = combination.getFirst();
        }

        if (second != -1) {
            if (combination.get(first).getValue().compareTo(combination.get(second).getValue()) > 0) {
                high_in_comb1 = combination.get(first);
                high_in_comb2 = combination.get(second);
            } else {
                high_in_comb1 = combination.get(second);
                high_in_comb2 = combination.get(first);
            }
        }


        return (count == 2);
    }

    /**
     * Проверка на пару
     */
    public boolean isPair(){
        for(int i = 0; i < 4; i++){
            if(combination.get(i).getValue() == combination.get(i + 1).getValue()){
                if (i == 0) {
                    kicker1 = combination.getLast();
                    kicker2 = combination.get(3);
                    kicker3 = combination.get(2);
                } else if (i == 1) {
                    kicker1 = combination.getLast();
                    kicker2 = combination.get(3);
                    kicker3 = combination.getFirst();
                } else if (i == 2) {
                    kicker1 = combination.getLast();
                    kicker2 = combination.get(1);
                    kicker3 = combination.getFirst();
                } else {
                    kicker1 = combination.get(2);
                    kicker2 = combination.get(1);
                    kicker3 = combination.getFirst();
                }
                high_in_comb1 = combination.get(i);
                return true;

            }
        }
        return false;
    }
}
