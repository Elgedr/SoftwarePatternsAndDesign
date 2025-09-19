package excercise1;

import java.util.Random;

import java.io.*;
import java.util.*;

public class WordSearchGenerator {


    private static final char EMPTY = '-';
    private static final int MAX_SIZE = 40;
    private static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        String inputFile = null;
        String outputFile = null;
        int size = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) inputFile = args[++i];
            else if (args[i].equals("-s")) size = Integer.parseInt(args[++i]);
            else if (args[i].equals("-o")) outputFile = args[++i];
        }

        if (inputFile == null || size == 0) {
            System.out.println("Use command-i <wordlist> -s <size> [-o <outputfile>]");
            return;
        }

        if (size > MAX_SIZE) {
            System.out.println("Error: maximum puzzle size is " + MAX_SIZE + "x" + MAX_SIZE);
            return;
        }

        List<String> words = readWordsFromFile(inputFile);
        char[][] puzzle = generatePuzzle(size, words);

        printPuzzle(puzzle);


        if (outputFile != null) {
            saveToFile(outputFile, puzzle, words);
        }
    }

    private static List<String> readWordsFromFile(String filename) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("[,; ]+");
                for (String w : parts) {
                    if (!w.isEmpty()) {
                        w = w.toUpperCase();
                        if (!w.matches("[A-Z]+")) {
                            throw new IllegalArgumentException("Word '" + w + "' contains non-alphabetic characters.");
                        }
                        words.add(w);
                    }
                }
            }
        }

        return words;
    }

    private static char[][] generatePuzzle(int size, List<String> words) {
        if (size <= 0 || size > 40) {
            throw new IllegalArgumentException("Puzzle size must be between 1 and 40.");
        }

        char[][] grid = new char[size][size];
        for (char[] row : grid) Arrays.fill(row, EMPTY);

        for (String word : words) {
            placeWord(grid, word);
        }


        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == EMPTY) {
                    grid[r][c] = (char) ('A' + rand.nextInt(26));
                }
            }
        }

        return grid;
    }

    private static void placeWord(char[][] grid, String word) {
        int size = grid.length;
        if (word.length() > size) {
            throw new IllegalArgumentException("Word '" + word + "' is too long for grid size " + size);
        }

        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        List<int[]> candidates = new ArrayList<>();

        // Collect all valid placements
        for (int dir = 0; dir < 8; dir++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int r = row, c = col;
                    boolean fits = true;

                    for (char ch : word.toCharArray()) {
                        if (r < 0 || r >= size || c < 0 || c >= size ||
                                (grid[r][c] != EMPTY && grid[r][c] != ch)) {
                            fits = false;
                            break;
                        }
                        r += dr[dir];
                        c += dc[dir];
                    }

                    if (fits) {
                        candidates.add(new int[]{row, col, dir});
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("Could not place word '" + word + "' in the grid");
        }

        // Randomly pick one of the valid placements
        int[] choice = candidates.get(rand.nextInt(candidates.size()));
        int r = choice[0], c = choice[1], dir = choice[2];
        for (char ch : word.toCharArray()) {
            grid[r][c] = ch;
            r += dr[dir];
            c += dc[dir];
        }
    }

    private static void printPuzzle(char[][] grid) {
        for (char[] row : grid) {
            for (char ch : row) {
                System.out.print(ch + " ");
            }
            System.out.println();
        }
    }

    private static void saveToFile(String filename, char[][] grid, List<String> words) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (char[] row : grid) {
                for (char c : row) pw.print(c);
                pw.println();
            }
            String wordLine = String.join(";", words).toLowerCase();
            pw.println(wordLine);
        }
    }

}


