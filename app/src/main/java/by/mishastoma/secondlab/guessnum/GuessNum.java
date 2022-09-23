package by.mishastoma.secondlab.guessnum;

import android.util.Log;

import java.util.Random;

import by.mishastoma.secondlab.GuessNumberException;

public class GuessNum {

    private static final int MIN_ATTEMPTS = 1;

    private static final String INFO = "INFO";

    private static final int[][] VALID_NUMBER_SIZES = {{2, 5}, {3, 7}, {4, 10}};

    private int lowestNumber = 0;

    private int biggestNumber = 99;

    private int attempts = 5;

    private int generatedNumber = 0;

    private int numberSize = 0;

    public GuessNum() throws GuessNumberException {
        updateBounds(VALID_NUMBER_SIZES[0][0]);
        restart();
    }

    public GuessNum(int numberSize) throws GuessNumberException {
        updateBounds(numberSize);
        restart();
    }

    public int getAttempts() {
        return attempts;
    }

    public void restart() {
        this.biggestNumber = (int) (Math.pow(10, numberSize)) - 1;
        this.lowestNumber = biggestNumber / 10 + 1;
        this.generatedNumber = generateNum();
        resetAttempts();
        Log.i(INFO, "Lowest possible number: " + lowestNumber);
        Log.i(INFO, "Biggest possible number: " + biggestNumber);
        Log.i(INFO, "Generated number: " + generatedNumber);
    }

    public void updateBounds(int numberSize) throws GuessNumberException {
        int indexOfBound = findNumberSize(numberSize);
        if (indexOfBound == -1) {
            throw new GuessNumberException("Invalid number size");
        }
        this.numberSize = numberSize;
    }

    public GuessConditions takeAGuess(int number) {
        if (number == generatedNumber) {
            return GuessConditions.WIN;
        } else {
            if (attempts == MIN_ATTEMPTS) {
                return GuessConditions.LOSE;
            } else {
                attempts--;
                return generatedNumber < number ?
                        GuessConditions.GENERATED_NUMBER_LOWER : GuessConditions.GENERATED_NUMBER_BIGGER;
            }
        }
    }

    private int findNumberSize(int numberSize) {
        for (int i = 0; i < VALID_NUMBER_SIZES.length; i++) {
            if (numberSize == VALID_NUMBER_SIZES[i][0]) {
                return i;
            }
        }
        return -1;
    }

    private int generateNum() {
        Random random = new Random();
        return random.nextInt(biggestNumber - lowestNumber + 1) + lowestNumber;
    }

    private void resetAttempts(){
        int index = findNumberSize(numberSize);
        attempts = VALID_NUMBER_SIZES[index][1];
    }
}
