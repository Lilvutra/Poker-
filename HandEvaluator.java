import java.util.*;

public class HandEvaluator {

    // Evaluate the rank of a 5-card hand
    public int evaluateHand(String[] hand) {
        List<Integer> values = new ArrayList<>();
        Map<Character, Integer> rankCount = new HashMap<>();
        Map<Character, Integer> suitCount = new HashMap<>();

        for (String card : hand) {
            char rank = card.charAt(0);
            char suit = card.charAt(1);

            values.add(rankToValue(rank));

            // Count occurrences of each rank and suit
            // if rank/suit has not been seen before, initialize to 0
            //  if rank/suit has been seen before, increment the count
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
            suitCount.put(suit, suitCount.getOrDefault(suit, 0) + 1);
        }

        Collections.sort(values);
        boolean flush = suitCount.containsValue(5);
        boolean straight = isStraight(values);

        // Royal Flush
        if (flush && straight && values.contains(14) && values.contains(10)) return 10;

        // Straight Flush
        if (flush && straight) return 9;

        // Four of a Kind
        if (rankCount.containsValue(4)) return 8;

        // Full House
        if (rankCount.containsValue(3) && rankCount.containsValue(2)) return 7;

        // Flush
        if (flush) return 6;

        // Straight
        if (straight) return 5;

        // Three of a Kind
        if (rankCount.containsValue(3)) return 4;

        // Two Pair
        int pairCount = 0;
        for (int count : rankCount.values()) {
            if (count == 2) pairCount++;
        }
        if (pairCount == 2) return 3;

        // One Pair
        if (pairCount == 1) return 2;

        // High Card
        return 1;
    }

    // Compare two 5-card hands
    public int compareHands(String[] hand1, String[] hand2) {
        int rank1 = evaluateHand(hand1);
        int rank2 = evaluateHand(hand2);
        if (rank1 != rank2) return rank1 - rank2;

        // Tiebreaker: Compare sorted card values
        List<Integer> v1 = getSortedRankValues(hand1);
        List<Integer> v2 = getSortedRankValues(hand2);
        System.out.println("SortedRankValues v1:" + v1);
        System.out.println("SortedRankValues v2:" + v2);

        // Compare highest cards of both hands 
        for (int i = 0; i < v1.size(); i++) {
            int cmp = v1.get(i) - v2.get(i);
            System.out.println("Compare v1:" + v1.get(i) + " v2:" + v2.get(i) + " cmp:" + cmp);
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    // Find best 5-card hand from any 7 cards
    public String[] findBestHand(String[] playerCards, String[] communityCards) {
        // Combine player and community cards
        // to create a pool of 7 cards
        // and generate all possible 5-card combinations
        String[] all = new String[playerCards.length + communityCards.length];
        System.arraycopy(playerCards, 0, all, 0, playerCards.length);
        System.arraycopy(communityCards, 0, all, playerCards.length, communityCards.length);

        List<String[]> combinations = generateCombinations(all, 5);
        System.out.println("Combinations size: " + combinations.size());
        for (String[] combo : combinations) {
            System.out.println("Combo: " + Arrays.toString(combo));
        }
        String[] best = combinations.get(0);
        for (String[] combo : combinations) {
            if (compareHands(combo, best) > 0) {
                best = combo;
            }
        }
        return best;
    }

    // Helper: Generate all k-card combinations
    private List<String[]> generateCombinations(String[] cards, int k) {
        List<String[]> result = new ArrayList<>();
        combine(cards, 0, k, new ArrayList<>(), result);
        return result;
    }

    private void combine(String[] cards, int start, int k, List<String> current, List<String[]> result) {
        if (current.size() == k) {
            result.add(current.toArray(new String[0]));
            return;
        }
        for (int i = start; i <= cards.length - (k - current.size()); i++) {
            current.add(cards[i]);
            combine(cards, i + 1, k, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Helper: Convert ranks to sorted values
    private List<Integer> getSortedRankValues(String[] hand) {
        List<Integer> values = new ArrayList<>();
        for (String card : hand) {
            values.add(rankToValue(card.charAt(0)));
        }
        values.sort(Collections.reverseOrder());
        return values;
    }

    // Check for straight, considering A-2-3-4-5
    private boolean isStraight(List<Integer> values) {
        Set<Integer> set = new HashSet<>(values);
        List<Integer> unique = new ArrayList<>(set);
        Collections.sort(unique);

        // Check for A-2-3-4-5
        if (set.containsAll(Arrays.asList(14, 2, 3, 4, 5))) return true;

        for (int i = 0; i <= unique.size() - 5; i++) {
            boolean straight = true;
            for (int j = 0; j < 4; j++) {
                if (unique.get(i + j) + 1 != unique.get(i + j + 1)) {
                    straight = false;
                    break;
                }
            }
            if (straight) return true;
        }
        return false;
    }

    // Convert char rank to int
    private int rankToValue(char rank) {
        switch (rank) {
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case 'T': return 10;
            case 'J': return 11;
            case 'Q': return 12;
            case 'K': return 13;
            case 'A': return 14;
            default: return 0;
        }
    }

    public static void main(String[] args) {
        HandEvaluator evaluator = new HandEvaluator();
        String[] hand1 = {"2H", "3D", "5S", "9C", "KD"};
        String[] hand2 = {"2C", "3H", "4S", "8C", "AH"};
        System.out.println("Hand 1 Rank: " + evaluator.evaluateHand(hand1));
        System.out.println("Hand 2 Rank: " + evaluator.evaluateHand(hand2));
        System.out.println("Compare hands: " + evaluator.compareHands(hand1, hand2));

        String[] playerCards = {"2H", "3D"};
        String[] communityCards = {"5S", "9C", "KD", "2C", "3H"};
        String[] bestHand = evaluator.findBestHand(playerCards, communityCards);
        System.out.println("Best Hand: " + Arrays.toString(bestHand));
        System.out.println("Best Hand Rank: " + evaluator.evaluateHand(bestHand));
        System.out.println("Best Hand Compare: " + evaluator.compareHands(bestHand, hand1));
        System.out.println("Best Hand Compare: " + evaluator.compareHands(bestHand, hand2));
        System.out.println("Best Hand Compare: " + evaluator.compareHands(hand1, hand2));
      
    }
}
