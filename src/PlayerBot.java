public class PlayerBot extends Player{

    PlayerBot(){
        super("Бот");
    }

    @Override
    public int betAction(int minBet) {
        bet(minBet - getBufBet());
        return 0;
    }
}
