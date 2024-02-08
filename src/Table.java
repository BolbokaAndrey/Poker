import java.util.*;


public class Table {
    static final int SMALL_BLIND = 10;
    static final int BIG_BLIND = SMALL_BLIND * 2;
    private static int index_card = 0;
    private boolean isAllInn = false;



    private Deck deck = new Deck();
    private int bank;
    private List<Integer> extra_bank = new ArrayList<>();
    private Chair[] chairs = new Chair[6];
    private Chair curChair = new Chair(null);
    private static List<Deck.Card> cards_on_table = new ArrayList<>(0);
    private int cur_max_bet = 0;



    public void shuffleDeck(){
        deck.shuffle();
    }

    public Deck getDeck() {
        return deck;
    }

    public Chair[] getChairs() {
        return chairs;
    }

    public void addPlayerToChair(Player player){
        for (int i = 0; i <= chairs.length; i++) {
            if (i == chairs.length){
                System.out.println("Нет свободных мест!");
                break;
            }
            if(chairs[i] == null) {
                chairs[i] = new Chair(player);
                break;
            }

        }
    }

    public static List<Deck.Card> getCardsOnTable(){
        return cards_on_table;
    }

    public Chair nextChair(){
        for (int i = 0; i < chairs.length; i++) {
            if(chairs[i] != null){
                if(i == chairs.length - 1){
                    return chairs[0];
                }
                if(curChair == chairs[i]){
                    return chairs[i + 1];
                }
            }
        }
        return chairs[0];
    }

    public Chair prevChair(){
        for (int i = chairs.length - 1; i >= 0; i--) {
            if(chairs[i] != null){
                if(i == 0){
                    return chairs[chairs.length - 1];
                }
                if(curChair == chairs[i]){
                    return chairs[i - 1];
                }
            }
        }
        return  chairs[chairs.length - 1];
    }

    public void dealHand(){
        for (int count_card = 0; count_card < 2; count_card++){
            for (int i = 0; i < chairs.length; i++) {
                if(chairs[i] == null) continue;
                chairs[i].getPlayer().addCardHand(deck.getCard(index_card++));
            }
        }
    }

    public void printHandsAndTable(boolean bot){
        System.out.println("\n");
        for (int i = bot ? 1 : 0; i < chairs.length; i++){
            if(chairs[i] == null) continue;
            System.out.println(chairs[i].getPlayer().getName() +
                    " имеет на руках " + chairs[i].getPlayer().getHand().toString());
        }
        System.out.println("На столе " + cards_on_table.toString());
    }

    private void winRoundWithExtraBank(int index_eb){
        List<Player> next_winners = getWinners(index_eb + 1);

        for (Player winner: next_winners) {
            winner.addMoney(extra_bank.getFirst() / next_winners.size());
        }
        extra_bank.removeFirst();
        if (!extra_bank.isEmpty()) {
            winRoundWithExtraBank(index_eb + 1);
        }
    }

    public void winRound(List<Player> winners){
        for (Player winner: winners) {
            winner.addMoney(bank / winners.size());
        }

        if (!extra_bank.isEmpty()) {
            winRoundWithExtraBank(-1);
        }

        bank = 0;
        index_card = 0;
        cur_max_bet = 0;
        cards_on_table.clear();
        isAllInn = false;

        for (Chair value : chairs) {
            if (value.isDealer()) {
                curChair = value;
                curChair.setDealer(false);
                break;
            }
        }

        do {
            curChair = nextChair();
        }
        while (curChair == null);
        curChair.setDealer(true);

        for(Chair chair: chairs){
            if(chair != null){
                chair.clearFlags();
                chair.getPlayer().clearHand();
            }
        }
    }

    public void getBlinds(){

        for(Chair chair: chairs){
            if(chair.isDealer()){
                curChair = chair;
                do {
                    curChair = nextChair();
                }
                while (curChair == null);

                curChair.setSmallBlind(true);
                curChair.getPlayer().bet(SMALL_BLIND);
                break;
            }
        }

        do {
            curChair = nextChair();
        }
        while (curChair == null);

        curChair.setBigBlind(true);
        curChair.getPlayer().bet(BIG_BLIND);
        cur_max_bet = BIG_BLIND;
    }

    private void addBank(){
        if (isAllInn) {
            int min_all_in = Integer.MAX_VALUE;
            for (Chair chair: chairs) {
                if(chair != null && !chair.isPass()) {
                    int ch_buf_bet = chair.getPlayer().getBufBet();
                    if (ch_buf_bet < min_all_in) {
                        min_all_in = ch_buf_bet;
                    }
                }
            }
            for (Chair chair: chairs) {
                if(chair != null && !chair.isPass()) {
                    bank += chair.getPlayer().getBufBet() - min_all_in;
                    chair.getPlayer().correctBuf_bet(min_all_in);
                }
            }

            while (true) {
                min_all_in = Integer.MAX_VALUE;
                for (Chair chair: chairs) {
                    if (chair != null && !chair.isPass() && chair.getPlayer().getBufBet() > 0) {
                        int ch_buf_bet = chair.getPlayer().getBufBet();
                        if (ch_buf_bet < min_all_in) {
                            min_all_in = ch_buf_bet;
                        }
                    }
                }
                if (min_all_in != Integer.MAX_VALUE) {
                    int tmp_bank = 0;
                    for (Chair chair: chairs) {
                        if (chair != null && !chair.isPass() && chair.getPlayer().getBufBet() > 0) {
                            chair.getPlayer().incIndexExtraBank();
                            tmp_bank += chair.getPlayer().getBufBet() - min_all_in;
                            chair.getPlayer().correctBuf_bet(min_all_in);
                        }
                    }
                    extra_bank.add(tmp_bank);
                } else break;
            }
        } else {
            for (Chair chair: chairs){
                if(chair != null){
                    bank += chair.getPlayer().getBufBet();
                    chair.getPlayer().clearBufBet();
                }
            }
        }



    }

    public boolean startFirstBids(){

        List<Chair> chairs_in_game = new ArrayList<>(Arrays.stream(chairs).filter(c -> (c != null && !c.isPass())).toList());
        boolean continueBids = true;
        Chair prev_chair;
        int count_action = 0;
        while (continueBids){

            prev_chair = curChair;
            do {
                curChair = nextChair();
            }
            while (curChair == null || curChair.isPass());

            int action_code = curChair.isAllIn() ? 1 : curChair.getPlayer().betAction(prev_chair.getPlayer().getBufBet());
            count_action++;
            if(action_code == -1){
                curChair.setPass(true);
                chairs_in_game.remove(curChair);
            } else if (action_code == 1) {
                cur_max_bet = curChair.getPlayer().getBufBet();
            } else if (action_code == 2) {
                cur_max_bet = curChair.getPlayer().getBufBet();
                curChair.setAllIn(true);
                isAllInn = true;
            } else if (action_code == 3) {
                curChair.setAllIn(true);
                isAllInn = true;
            }

            if(chairs_in_game.size() == 1){
                List<Player> winner = new ArrayList<>();
                winner.add(chairs_in_game.getFirst().getPlayer());
                addBank();
                System.out.println(winner.getLast().getName() + " победил и заработал " + bank + "\n");
                winRound(winner);
                return false;
            }

            if(count_action >= chairs_in_game.size()){
                //Уравнено ли?
                for(int i = 0; i <= chairs.length; i++){
                    if(i == chairs.length){
                        continueBids = false;
                        break;
                    }
                    if(chairs[i] == null || chairs[i].isPass()) continue;
                    if(chairs[i].getPlayer().getBufBet() != cur_max_bet){
                        if (chairs[i].getPlayer().getMoney() == 0) continue;
                        break;
                    }
                }
            }

        }
        addBank();
        return true;
    }

    public void dealFlop(){
        for (int i = 0; i < 3; i++) {
            cards_on_table.add(deck.getCard(index_card++));
        }
    }

    public boolean startBids(){
        for(Chair chair: chairs){
            if (chair.isDealer()){
                curChair = chair;
                break;
            }
        }
        return startFirstBids();
    }

    public void dealTurn(){
        cards_on_table.add(deck.getCard(index_card++));
    }

    public void dealRiver(){
        cards_on_table.add(deck.getCard(index_card++));
    }

    public void endRound(){
        List<Player> winners = getWinners(-1);
        if (winners.size() == 1){
            printHandsAndTable(false);
            String name = winners.getFirst().getName();
            System.out.println("\nПобедил " + name + ", заработав " + bank);
            System.out.println(name + " имел комбинацию " + winners.getFirst().getCombination() + "\n");
        } else {
            String names = "";
            for (Player winner: winners){
                names += winner.getName() + ", ";
            }
            System.out.println("\nПобедили " + names);
        }
        winRound(winners);
    }

    private List<Player> getWinners(int index_eb){
        List<Chair> chairs_in_game = new ArrayList<>(Arrays.stream(chairs).filter(c ->
                (c != null && !c.isPass() && c.getPlayer().getIndex_extra_bank() >= index_eb)).toList());

        TreeMap<Combinations, Chair> winner_candidate = new TreeMap<>();
        for (Chair chair: chairs_in_game){
            if (index_eb == -1) {
                chair.getPlayer().checkCombination();
            }
            winner_candidate.put(chair.getPlayer().getCombination(), chair);
        }

        List<Player> winners = new ArrayList<>();
        winners.add(winner_candidate.pollLastEntry().getValue().getPlayer());

        for (int i = winner_candidate.size() - 1; i >= 0 ; i--) {
            Player check = winner_candidate.pollLastEntry().getValue().getPlayer();
            if (winners.getLast().getCombination().compareTo(check.getCombination()) == 0) {
                winners.add(check);
            } else {
                break;
            }
        }
        return winners;
    }

    public boolean checkChairs(){
        for (int i = 0; i < chairs.length; i++) {
            if (chairs[i] != null) {
                if (chairs[i].getPlayer().getMoney() < BIG_BLIND) {
                    System.out.println(chairs[i].getPlayer().getName() + " всё проиграл и уходит из-за стола!");
                    chairs[i] = null;
                }
            }
        }

        int in_game = 0;
        Player winner = null;
        for (Chair chair: chairs) {
            if (chair != null) {
                in_game++;
                winner = chair.getPlayer();
            }
        }
        if (in_game == 1) {
            System.out.println(winner.getName() + " победил всех! Общий выигрыш составил "+ winner.getMoney());
            return false;
        }
        return true;
    }
}
