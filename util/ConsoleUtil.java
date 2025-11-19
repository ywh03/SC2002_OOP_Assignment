package util;

import java.util.Scanner;

/**
 * Utility class for handling console input operations, including reading strings,
 * robustly reading integers, and pausing the console display.
 */
public class ConsoleUtil {

    private final Scanner scanner = new Scanner(System.in);


    /**
     * Reads a line of text input from the console after displaying a prompt.
     *
     * @param prompt The message to display to the user before reading input.
     * @return The trimmed line of text input from the user.
     */
    public String readLine(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine().trim();
    }


    /**
     * Reads an integer input from the console, handling {@link NumberFormatException}
     * until a valid integer is provided.
     *
     * @param prompt The message to display to the user before reading input.
     * @return The valid integer input from the user.
     */
    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Pauses the console execution until the user presses the ENTER key.
     * Typically used after displaying a block of information.
     */
    public void pause() {
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();
    }

}