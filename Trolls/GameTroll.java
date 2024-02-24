package Trolls;

import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class GameTroll.
 * Course code tailored by the CSC207 instructional
 * team at UTM, with special thanks to:
 *
 * @author anshag01
 * @author mustafassami
 * @author guninkakr03
 *  */
public class GameTroll implements Troll {

    //Write your own code here!
    public GameTroll() {
        // You can use System.out.print here
        System.out.println("The Game Troll");
    }

    /**
     * Print GameTroll instructions for the user
     */
    public void giveInstructions()
    {
        System.out.println("I can read ur mind, hehe!");
    }

    /**
     * Play the GameTroll game
     *
     * @return true if player wins the game, else false
     */
    public boolean playGame() {
        Scanner s = new Scanner(System.in);
        System.out.println("Write something");
        String k = s.nextLine();
        System.out.println(k + ", I read ur mind, HAHAHAHAHA");
        return true;
    }

    /**
     * Main method, use for debugging
     *
     * @param args: Input arguments
     */
    public static void main(String [] args) throws InterruptedException {
        GameTroll s = new GameTroll();
        boolean a = s.playGame();
    }
}
