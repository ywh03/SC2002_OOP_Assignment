package util;

import java.util.Scanner;

public class ConsoleUtil {

    private final Scanner scanner = new Scanner(System.in);

    public String readLine(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine().trim();
    }

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

    public void pause() {
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();
    }

}