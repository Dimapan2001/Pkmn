package ru.mirea.pkmn.PanshinDI;

import ru.mirea.pkmn.Card;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CardExport {

    // Метод для экспорта карты в бинарный файл
    public void exportCard(Card card, String filename) {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(card);  // Сериализация объекта карты
            System.out.println("Карта успешно экспортирована в " + filename);
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте карты: " + e.getMessage());
        }
    }
}