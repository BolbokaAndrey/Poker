import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Player{

    private String name;
    private int money = 1000;
    private int buf_bet = 0;
    private List<Deck.Card> hand = new ArrayList<>(2);
    private Combinations combination;
    private int index_extra_bank = -1;




    Player(String name){
        this.name = name;
    }
    Player(String name, int money){
        this.name = name;
        this.money = money;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public int getBufBet() {
        return buf_bet;
    }

    public void correctBuf_bet(int correct_value) {
        buf_bet -= correct_value;
    }

    public void clearBufBet(){
        buf_bet = 0;
    }

    public List<Deck.Card> getHand() {
        return hand;
    }

    public void addCardHand(Deck.Card card){
        hand.add(card);
    }

    public void incIndexExtraBank() {
        index_extra_bank++;
    }

    public void clearIndexExtraBank() {
        index_extra_bank = -1;
    }

    public int getIndex_extra_bank() {
        return index_extra_bank;
    }

    public void clearHand(){
        hand.clear();
        buf_bet = 0;
    }

    public void bet(int m){
        if (m == money){
            System.out.println("Олл-ин!!!");
        }

        System.out.println(name + " ставит " + m);
        money -= m;
        buf_bet += m;
    }

    public void pass(){
        System.out.println("\n" + name + " пасует!");
        hand.clear();

    }

    public int betAction(int minBet){
        Scanner in = new Scanner(System.in);
        int in_bet = -1;
        while (true){
            System.out.println("\nЧтобы спасовать, введи '-' (без кавычек).");
            if (minBet < money) {
                System.out.println("Чтобы уравнять (колл " + minBet + "/чек), введи 0.");
                System.out.println("Чтобы поднять ставку, введи сумму ставки от " + (minBet - buf_bet) + " до " + money + ".");
            } else {
                System.out.println("Чтобы продолжить, придётся идти в All-in! Введи 0.");
            }
            System.out.print(name + ", твоя ставка ");
            String choice = in.next();
            System.out.println();

            if(choice.equals("-")){
                pass();
                return -1;
            } else {
                try {
                    in_bet = Integer.parseInt(choice);
                } catch (NumberFormatException e){
                    System.out.println("Некоректные данные...");
                    continue;
                }
            }

            if (minBet >= money) {
                bet(money);
                return 3;
            }

            if(in_bet == 0){
                bet(minBet - buf_bet);
                return 1;
            } else if (in_bet < minBet - buf_bet) {
                System.out.println("Слишком маленькая ставка!");
            } else {
                if (in_bet > money){
                    System.out.println("Не хватает денег!");
                } else if (in_bet == money) {
                    bet(in_bet);
                    return 2;
                } else {
                    bet(in_bet);
                    return 1;
                }

            }
        }

    }

    public void checkCombination(){
        List<Deck.Card> check_7_cards = new ArrayList<>();
        check_7_cards.addAll(Table.getCardsOnTable());
        check_7_cards.addAll(hand);
        List<Combinations> list_of_combinations = createListOfCombinations(7, 5, check_7_cards);
        combination = Collections.max(list_of_combinations);
    }

    private List<Combinations> createListOfCombinations(int n, int k, List<Deck.Card> check){
        int[] comb = new int[k + 2];
        for (int i = 0; i < k; i++) {
            comb[i] = i;
        }
        comb[k] = n;
        comb[k + 1] = 0;
        List<List<Deck.Card>> list_raw_combinations = new ArrayList<>();
        while (true){
            list_raw_combinations.add(new ArrayList<>());
            for (int i = 0; i < k; i++) {
                list_raw_combinations.getLast().add(check.get(comb[i]));
            }
            int j = 0;
            for (; comb[j] + 1 == comb[j + 1]; j++) {
                comb[j] = j;
            }
            if (j < k){
                comb[j]++;
            } else{
                break;
            }

        }

        List<Combinations> list_combinations = new ArrayList<>();
        for (List<Deck.Card> lc: list_raw_combinations){
            list_combinations.add(new Combinations(lc));
        }

        return list_combinations;
    }


    public Combinations getCombination() {
        return combination;
    }
}
