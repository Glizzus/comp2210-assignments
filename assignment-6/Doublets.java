import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Provides an implementation of the WordLadderGame interface. 
 *
 * @author Cal Crosby (csc0088@auburn.edu)
 */
public class Doublets implements WordLadderGame {

    // The word list used to validate words.
    // Must be instantiated and populated in the constructor.
    private TreeSet<String> lexicon;


    /**
     * Instantiates a new instance of Doublets with the lexicon populated with
     * the strings in the provided InputStream. The InputStream can be formatted
     * in different ways as long as the first string on each line is a word to be
     * stored in the lexicon.
     */
    public Doublets(InputStream in) {
        try {
            lexicon = new TreeSet<>();
            Scanner s =
                    new Scanner(new BufferedReader(new InputStreamReader(in)));
            while (s.hasNext()) {
                String str = s.next();
                lexicon.add(str.toLowerCase());
                s.nextLine();
            }
            in.close();
        } catch (java.io.IOException e) {
            System.err.println("Error reading from InputStream.");
            System.exit(1);
        }
    }


    //////////////////////////////////////////////////////////////
    // ADD IMPLEMENTATIONS FOR ALL WordLadderGame METHODS HERE  //
    //////////////////////////////////////////////////////////////

    @Override
    public int getWordCount() {
        return lexicon.size();
    }

    @Override
    public boolean isWord(String str) {
        str = str.toLowerCase();
        return lexicon.contains(str);
    }

    @Override
    public int getHammingDistance(String str1, String str2) {

        if (str1 == null || str2 == null) throw new NullPointerException();
        if (str1.length() != str2.length()) return -1;

        int differences = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                differences++;
            }
        }
        return differences;
    }

    private boolean areNeighbors(String str1, String str2) {
        return getHammingDistance(str1, str2) == 1;
    }


    @Override
    public List<String> getNeighbors(String word) {

        word = word.toLowerCase();
        ArrayList<String> neighbors = new ArrayList<>();
        for (String w : lexicon) {
            if (areNeighbors(w, word)) {
                neighbors.add(w);
            }
        }
        return neighbors;
    }

    @Override
    public boolean isWordLadder(List<String> sequence) {

        if (sequence.isEmpty()) return false;
        else if (sequence.size() == 1) return true;

        Iterator<String> it1 = sequence.iterator();
        Iterator<String> it2 = sequence.iterator();
        it2.next();

        while (it2.hasNext()) {
            String word1 = it1.next();
            String word2 = it2.next();
            if (!areNeighbors(word1, word2) ||
                    !isWord(word1) || !isWord(word2)) {

                return false;
            }
        }
        return true;
    }


    @Override
    public List<String> getMinLadder(String start, String end) {

        List<String> ladder = new ArrayList<>();

        if (start == null || end == null) return ladder;
        if (!isWord(end) || !isWord(start)) return ladder;
        if (start.length() != end.length()) return ladder;

        if (start.equalsIgnoreCase(end)) {
            ladder.add(start);
            return ladder;
        }

        Deque<Node> q = new ArrayDeque<>();
        q.add(new Node(start, null));
        HashSet<String> visited = new HashSet<>();

        while (!q.isEmpty()) {
            Node root = q.removeFirst();
            for (String word : getNeighbors(root.word)) {
                if (!visited.contains(word)) {
                    visited.add(word);
                    q.addLast(new Node(word, root));

                    if (word.equals(end)) {
                        ladder = backtrack(root);
                        ladder.add(end);
                        return ladder;
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private List<String> backtrack(Node end) {

        List<String> path = new ArrayList<>();
        path.add(end.word);
        Node previous = end.back;
        while (previous != null) {
            path.add(0, previous.word);
            previous = previous.back;
        }
        return path;
    }

    private static class Node {
        String word;
        Node back;

        public Node(String str, Node back) {
            this.word = str;
            this.back = back;
        }
    }
}
