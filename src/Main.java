public class Main {
    static final boolean INCLUDE_BOT = true;
    static final int NUMBER_OF_PLAYERS = 1;



    public static void main(String[] args) {

//        JFrame frame = new JFrame("Poker");
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1600, 900);
//        frame.getContentPane().setBackground(Color.darkGray);


        System.out.println("Поиграем в покер! (Бот всегда уравнивает ставку)\n");
        Table main_table = new Table();


        if(INCLUDE_BOT) main_table.addPlayerToChair(new PlayerBot());
        for(int i = 1; i <= NUMBER_OF_PLAYERS; i++){
            main_table.addPlayerToChair(new Player("Игрок " + i));
        }



        main_table.getChairs()[0].setDealer(true);


        while(main_table.checkChairs()){


            main_table.shuffleDeck();
            main_table.getBlinds();
            main_table.dealHand();

            main_table.printHandsAndTable(INCLUDE_BOT);

            if(!main_table.startFirstBids()) continue;

            main_table.dealFlop();

            main_table.printHandsAndTable(INCLUDE_BOT);

            if(!main_table.startBids()) continue;

            main_table.dealTurn();

            main_table.printHandsAndTable(INCLUDE_BOT);

            if(!main_table.startBids()) continue;

            main_table.dealRiver();

            main_table.printHandsAndTable(INCLUDE_BOT);

            if(!main_table.startBids()) continue;

            main_table.endRound();
        }


    }
}