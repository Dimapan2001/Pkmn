package ru.mirea.pkmn.PanshinDI;

import ru.mirea.pkmn.Card;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CardExport {
    public static void exportCard(Card card) {
        String filename = card.getName() + ".crd";

        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(card);
            System.out.println("Card exported as " + filename);
        } catch (IOException e) {
            System.err.println("Error while exporting card: " + e.getMessage());
        }
    }
}