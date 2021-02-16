package birintsev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Hill implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hill.class);

    private static final char BIGRAM_BALANCING_CHAR = 'Х';

    private static final Map<Character, Integer> CHAR_CODES;

    private static final Map<Integer, Character> CODES_CHAR;

    static {
        CHAR_CODES = new HashMap<>();
        CODES_CHAR = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            CHAR_CODES.put(c, c - 'A');
            CODES_CHAR.put(c - 'A', c);
        }
    }

    @Override
    public void run(String... args) {
        int[][] key = {
            {5, 3},
            {5, 4},
        };
        String toEncrypt = "MISFORTUNE";
        LOGGER.info("Text to encrypt: " + toEncrypt);
        LOGGER.info("Encrypted text: " + encrypt(toEncrypt, key));
    }

    public static void main(String[] args) {
        SpringApplication.run(Hill.class, args);
    }

    private String encrypt(String toEncrypt, int[][] key) {
        List<String> bigrams = splitToBigrams(toEncrypt);
        List<String> encryptedBigrams = new ArrayList<>(bigrams.size());

        for (String bigram : bigrams) {
            encryptedBigrams.add(encryptBigram(bigram, key));
        }

        return String.join("", encryptedBigrams);
    }

    private List<String> splitToBigrams(String string) {
        List<String> bigrams = new ArrayList<>();
        if (string.length() % 2 != 0) {
            string += BIGRAM_BALANCING_CHAR;
        }
        for (int i = 0; i <= string.length() - 1; i+= 2) {
            String bigram = string.substring(i, i + 2);
            if (bigram.charAt(0) == bigram.charAt(1)) {
                String nextBigram =
                    "" + BIGRAM_BALANCING_CHAR + bigram.charAt(1);
                bigram =
                    "" + bigram.charAt(0) + BIGRAM_BALANCING_CHAR;
                bigrams.addAll(Arrays.asList(bigram, nextBigram));
            } else {
                bigrams.add(bigram);
            }
        }
        LOGGER.info("Bigrams are: " + String.join(" ", bigrams));
        return bigrams;
    }

    private String encryptBigram(String bigram, int[][] key) {
        int[] bigramCharCodes;
        int[] encryptedCharCodes;
        String encryptedBigram;

        if (bigram == null || bigram.length() != 2) {
            throw new IllegalArgumentException(
                "Passed parameter bigram must be an instance of String"
                    + " with length == 2 (found: "
                    + bigram
                    + ")"
            );
        }

        bigramCharCodes = new int[] {
            CHAR_CODES.get(bigram.charAt(0)),
            CHAR_CODES.get(bigram.charAt(1))
        };

        encryptedCharCodes = multByMod(key, bigramCharCodes, alphabetMaxCode());

        if (
            encryptedCharCodes.length != 2
                || !CODES_CHAR.keySet().containsAll(
                    Arrays.asList(encryptedCharCodes[0], encryptedCharCodes[1])
                )
        ) {
            throw new RuntimeException(
                "encryptedCharCodes.length must be equal to 2"
                    + " and CODES_CHAR must contain its values"
            );
        }

        encryptedBigram = ""
            + CODES_CHAR.get(encryptedCharCodes[0])
            + CODES_CHAR.get(encryptedCharCodes[1]);

        LOGGER.info(
            "Bigram: " + bigram
                + "; Codes: " + Arrays.toString(bigramCharCodes)
                + "; Encrypted codes: " + Arrays.toString(encryptedCharCodes)
                + "; Encrypted bigram: " + encryptedBigram);

        return encryptedBigram;
    }

    private int[] multByMod(int[][] key, int[] bigramCharCodes, int mod) {
        int[] res = new int[key.length];
        for (int i = 0; i < key.length; i++) {
            res[i] = scalarProduct(column(key, i), bigramCharCodes) % mod;
        }
        return res;
    }

    private int[] column(int[][] matrix, int columnIndex) {
        int[] column = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][columnIndex];
        }
        return column;
    }

    private int scalarProduct(int[] v1, int[] v2) {
        int res = 0;
        if (v1.length != v2.length) {
            throw new IllegalArgumentException(
                "v1 and v2 must be of the same length"
            );
        }
        for (int i = 0; i < v1.length; i++) {
            res += v1[i] * v2[i];
        }
        return res;
    }

    private double[][] toVector(int[] column) {
        double[][] res = new double[column.length][1];
        for (int i = 0; i < column.length; i++) {
            res[i][0] = column[i];
        }
        return res;
    }

    private double[][] toDoubleMatrix(int[][] intMatrix) {
        double[][] doubleMatrix =
            new double[intMatrix.length][intMatrix[0].length];
        for (int i = 0; i < intMatrix.length; i++) {
            for (int j = 0; j < intMatrix.length; j++) {
                doubleMatrix[i][j] = intMatrix[i][j];
            }
        }
        return doubleMatrix;
    }

    private int alphabetMaxCode() {
        return CODES_CHAR
            .keySet()
            .size();
    }
}
