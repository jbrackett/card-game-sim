package com.brackett;

import java.util.*;

public class Main {

    public record Card(Integer value) implements Comparable<Card> {

        @Override
        public int compareTo(Card o) {
            return value.compareTo(o.value);
        }
    }
    public record Player(String name, List<Card> hand){

        public int handScore() {
            return hand.stream().map(c -> c.value).reduce(Integer::sum).orElse(0);
        }

        public void removeLowest(int count) {
            Collections.sort(hand);
            for (var i = 0; i < count; i++) {
                hand.removeFirst();
            }
        }

        @Override
        public String toString() {
            return name + " with hand: " + hand + "| score : " + handScore();
        }

    }

    public static class Deck {
        private static final List<Integer> cardList =
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10);
        private final Stack<Card> deck;

        public Deck() {
            deck = new Stack<>();
            for (var i = 1; i <= 4; i++) {
                deck.addAll(cardList.stream().map(Card::new).toList());
            }
        }

        public void shuffle() {
            Collections.shuffle(deck);
        }

        public Card nextCard() {
            return deck.pop();
        }
    }

    public static class Game {
        private Deck deck;
        private final List<Player> players;

        public Game(List<Player> players) {
            this.deck = new Deck();
            this.players = players;
        }

        public void start() {
            this.deck.shuffle();

            for (var i = 0; i < 5; i++) {
                for (var player : players) {
                    player.hand.add(deck.nextCard());
                }
            }
        }

        public void exchange(Player player) {
            System.out.println(player.name + ", your hand is: " + player.hand());
            System.out.println("How many cards would you like to exchange: ");
            Scanner in = new Scanner(System.in);

            var number = in.nextInt();

            if (number < 0) {
                number = 0;
            }
            if (number > 5) {
                number = 5;
            }

            if (number > 0) {
                player.removeLowest(number);
                for (int i = 0; i < number; i++) {
                    player.hand.add(deck.nextCard());
                }
            }
        }

        public void reset() {
            this.deck = new Deck();
            for (var player : players) {
                player.hand.clear();
            }
        }

        public Player declareWinner() {
            Player winner = null;
            for(var player: players) {
                if (winner == null) {
                    winner = player;
                }
                else {
                    if (player.handScore() > winner.handScore()) {
                        winner = player;
                    }
                }
            }
            return new Player(winner.name(), new ArrayList<>(winner.hand()));
        }

    }

    public static void main(String[] args) {
        try(var in = new Scanner(System.in)) {

            System.out.println("Enter player 1 name: ");
            var p1 = new Player(in.next(), new ArrayList<>());

            System.out.println("Enter player 2 name: ");
            var p2 = new Player(in.next(), new ArrayList<>());

            var winners = new ArrayList<Player>();
            var game = new Game(List.of(p1, p2));

            System.out.println("How many rounds do you want to play: ");
            var rounds = in.nextInt();
            for (int i = 0; i < rounds; i++) {
                game.start();
                game.exchange(p1);
                game.exchange(p2);
                var winner = game.declareWinner();
                System.out.println(winner.name + " won!");
                winners.add(winner);
                game.reset();
            }

            winners.forEach(System.out::println);
        }
    }
}