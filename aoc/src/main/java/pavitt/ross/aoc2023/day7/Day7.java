package pavitt.ross.aoc2023.day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    public String part1(String fileName) throws IOException {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            AtomicInteger sum = new AtomicInteger();
            AtomicInteger multiple = new AtomicInteger(1);
            stream
                    .map(Day7::toHand)
                    .sorted()
                    .forEach(x -> sum.addAndGet(multiple.getAndAdd(1) * x.bid));
            return "" + sum.get();
        }
    }

    public String part2(String fileName) throws IOException {
        WildcardJokerSort sortAlgo = new WildcardJokerSort();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            AtomicInteger sum = new AtomicInteger();
            AtomicInteger multiple = new AtomicInteger(1);
            stream
                    .map(Day7::toHand)
                    .sorted(sortAlgo)
                    .forEach(x -> sum.addAndGet(multiple.getAndAdd(1) * x.bid));
            return "" + sum.get();
        }
    }

    static Hand toHand(String line) {
        String[] parts = line.split(" +");
        return new Hand(parts[0], Integer.parseInt(parts[1]));
    }

    class WildcardJokerSort implements Comparator<Hand> {

        @Override
        public int compare(Hand o1, Hand o2) {

            Type thisType = Type.getTypeWithJokers(o1);
            Type thatType = Type.getTypeWithJokers(o2);
            if (thisType.equals(thatType)) {
                String[] thisHand = o1.cards.split("");
                String[] thatHand = o2.cards.split("");
                for (int i = 0; i < 5; i++) {
                    Card thisCard = Card.parse(thisHand[i]);
                    Card thatCard = Card.parse(thatHand[i]);
                    if (thisCard != thatCard) {
                        return thisCard.compareTo(thatCard);
                    }
                }
                throw new IllegalStateException();
            }
            return -1 * thisType.compareTo(thatType);
        }

    }

    record Hand(String cards, int bid) implements Comparable {

        @Override
        public int compareTo(Object o) {
            Hand that = (Hand) o;
            Type thisType = Type.getType(this);
            Type thatType = Type.getType(that);
            if (thisType.equals(thatType)) {
                String[] thisHand = this.cards.split("");
                String[] thatHand = that.cards.split("");
                for (int i = 0; i < 5; i++) {
                    Card thisCard = Card.parse(thisHand[i]);
                    Card thatCard = Card.parse(thatHand[i]);
                    if (thisCard != thatCard) {
                        return thisCard.compareTo(thatCard);
                    }
                }
                return 0;
            }
            return -1 * thisType.compareTo(thatType);
        }
    }

    enum Card {
        JACK("J"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("T"),

        QUEEN("Q"),
        KING("K"),
        ACE("A");

        String icon;

        Card(String icon) {
            this.icon = icon;
        }

        static Card parse(String value) {
            return Arrays.stream(Card.values()).filter(x -> x.icon.equals(value)).findFirst().get();
        }
    }

    enum Type {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD;

        static Type getType(Hand hand) {
            Map<String, Integer> cardMap = Arrays.stream(hand.cards.split(""))
                    .collect(Collectors.groupingBy(x -> x, Collectors.summingInt(i -> 1)));
            if (cardMap.size() == 1) {
                return FIVE_OF_A_KIND;
            }

            if (cardMap.size() == 2) {
                // either four fo a kind or full house
                if (cardMap.containsValue(4)) {
                    return FOUR_OF_A_KIND;
                }
                return FULL_HOUSE;
            }

            if (cardMap.size() == 3) {
                // either three of a kind or two pair
                if (cardMap.containsValue(3)) {
                    return THREE_OF_A_KIND;
                }
                return TWO_PAIR;
            }

            if (cardMap.size() == 4) {
                return ONE_PAIR;
            }

            return HIGH_CARD;
        }

        static Type getTypeWithJokers(Hand hand) {
            Map<String, Integer> cardMap = Arrays.stream(hand.cards.split(""))
                    .collect(Collectors.groupingBy(x -> x, Collectors.summingInt(i -> 1)));
            if (cardMap.size() == 1) {
                return FIVE_OF_A_KIND;
            }

            if (cardMap.size() == 2) {
                // either four fo a kind or full house
                // Or, becuase jokers are now a thing, need to check if there is a joker that
                // isn't the dominant type

                if (cardMap.containsValue(4)) {
                    // This could upgrade to five of a kind
                    if (cardMap.containsKey("J")) {
                        return FIVE_OF_A_KIND;
                    }
                    return FOUR_OF_A_KIND;
                }

                // Thsi full house could become a four of a kind
                if (cardMap.containsKey("J")) {
                    return FIVE_OF_A_KIND;
                }
                return FULL_HOUSE;
            }

            if (cardMap.size() == 3) {
                // either three of a kind or two pair
                // Unless there is a joker in there
                if (cardMap.containsValue(3)) {
                    // Upgrade a three of a kind to a four of a kind (a full house would already
                    // exist if both were jokers)
                    if (cardMap.containsKey("J")) {
                        return FOUR_OF_A_KIND;
                    }
                    return THREE_OF_A_KIND;
                }

                // If there are two jokers then this is a four of a kind
                // If there is only one joker this is three of a kind
                if (cardMap.containsKey("J")) {
                    if (cardMap.get("J").intValue() == 1) {
                        if (cardMap.entrySet().stream().filter(e -> !e.getKey().equals("J")).allMatch(e -> e.getValue() == 2)) {
                            return FULL_HOUSE;
                        }
                        return THREE_OF_A_KIND;
                    }
                    if (cardMap.get("J").intValue() == 2) {
                        return FOUR_OF_A_KIND;
                    }
                }

                return TWO_PAIR;
            }

            if (cardMap.size() == 4) {
                if (cardMap.containsKey("J")) {
                    return THREE_OF_A_KIND;
                }

                return ONE_PAIR;
            }

            if (cardMap.containsKey("J")) {
                return ONE_PAIR;
            }

            return HIGH_CARD;
        }
    }
}
