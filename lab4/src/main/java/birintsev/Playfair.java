package birintsev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

//@SpringBootApplication
public class Playfair implements CommandLineRunner {

    static final char BIGRAM_BALANCING_CHAR = 'Х';

    private static final Logger LOGGER = LoggerFactory.getLogger(
        Playfair.class
    );

    public static void main(String[] args) {
        SpringApplication.run(Playfair.class, args);
    }

    //@Bean
    CommandLineRunner playfairCommandLineRunner() {
        return args -> new Playfair().run(args);
    }

    @Override
    public void run(String[] args) {
        final char[][] table = {
            {'Б', 'Р', 'М', 'Х', 'В', 'О'},
            {'З', 'А', '_', ',', 'Є', 'Г'},
            {'И', 'К', 'Ґ', 'Е', 'Ф', 'Я'},
            {'П', 'Ю', '.', 'Ь', 'У', 'Ч'},
            {'Ш', 'Л', 'С', 'Й', 'І', 'Т'},
            {'Ї', 'Щ', 'Д', 'Н', 'Ц', 'Ж'}
        };
        //String toEncrypt = "НЕХАЙ_В_ТВОЇМ_СЕРЦІ_ЛЮБОВІ_НЕ_ЗГАСНЕ_СВЯЩЕНИЙ_ВОГОНЬ,_ЯК_ПЕРШЕ_ПРОМОВЛЕНЕ_СЛОВО_НА_МОВІ_НАРОДУ_СВОГО.";
        //String toEncrypt = "ВОНА_ЯК_ЗОРЯ_ПУРПОРОВА_ЩО_СЯЄ_З_НЕБЕСНИХ_ВИСОТ,_І_ТАМ_ДЕ_ЗВУЧИТЬ_РІДНА_МОВА_ЖИВЕ_УКРАЇНСЬКИЙ_НАРОД.";
        String toEncrypt = "ЯК_СОНЦЯ_БЕЗСМЕРТНОГО_КОЛО_ЩО_КРЕСЛИТЬ_У_НЕБІ_ПУТІ_ЛЮБІТЬ_СВОЮ_МОВУ_Й_НІКОЛИ_ЇЇ_НЕ_ЗАБУДЬТЕ_В_ЖИТТІ.";
        LOGGER.info("Text to encrypt: " + toEncrypt);
        LOGGER.info(
            "Encrypted text: " + encrypt(toEncrypt, table)
        );
    }

    private String[] splitToBigrams(String string) {
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
        return bigrams.toArray(new String[0]);
    }

    private String encrypt(String toEncrypt, char[][] table) {
        String[] bigrams = splitToBigrams(toEncrypt);
        List<String> encodedBigrams = new ArrayList<>(bigrams.length);
        for (String bigram : bigrams) {
            encodedBigrams.add(encryptBigram(bigram, table));
        }
        return String.join("", encodedBigrams);
    }

    private String encryptBigram(String bigram, char[][] table) {
        char[] splitBigram;
        int row0;
        int col0;
        int row1;
        int col1;
        String encodedBigram;

        if (bigram == null || bigram.length() != 2) {
            throw new IllegalArgumentException(
                "Passed parameter bigram must be an instance of String"
                    + " with length == 2 (found: "
                    + bigram
                    + ")"
            );
        }

        splitBigram = bigram.toUpperCase().toCharArray();
        row0 = findRowOfChar(splitBigram[0], table);
        col0 = findColOfChar(splitBigram[0], table);
        row1 = findRowOfChar(splitBigram[1], table);
        col1 = findColOfChar(splitBigram[1], table);

        if (row0 != row1 && col0 != col1) {
            encodedBigram = "" + table[row0][col1] + table[row1][col0];
        } else if (row0 == row1) {
            int shiftedCol0 = (col0 + 1 > table[row0].length - 1)
                  ? 0
                  : col0 + 1;
            int shiftedCol1 = (col1 + 1 > table[row1].length - 1)
                  ? 0
                  : col1 + 1;
            encodedBigram =
                "" + table[row0][shiftedCol0] + table[row1][shiftedCol1];
        } else /*if (col0 == col1)*/ {
            int shiftedRow0 = (row0 + 1 > table.length - 1)
                              ? 0
                              : row0 + 1;
            int shiftedRow1 = (row1 + 1 > table.length - 1)
                              ? 0
                              : row1 + 1;
            encodedBigram =
                "" + table[shiftedRow0][col0] + table[shiftedRow1][col1];
        }

        return encodedBigram;
    }

    private int findRowOfChar(char c, char[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] == c) {
                    return i;
                }
            }
        }
        throw new NoSuchElementException(
            "Charachter '" + c + "' has not been found in passed table"
        );
    }

    private int findColOfChar(char c, char[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] == c) {
                    return j;
                }
            }
        }
        throw new NoSuchElementException(
            "Charachter '" + c + "' has not been found in passed table"
        );
    }
}
