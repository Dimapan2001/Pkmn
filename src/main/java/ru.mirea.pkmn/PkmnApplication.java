package ru.mirea.pkmn;

import java.io.*;

public class PkmnApplication {

    public static void main(String[] args) {
        CardImport importer = new CardImport();

        // 1. Импортируем и обрабатываем основную карту (my_card.txt)
        System.out.println("Beartic:");
        Card mainCard = loadCard(importer, "src/main/resources/my_card.txt");

        // 2. Читаем ссылку на первую карту из пятого пункта
        String firstCardFilePath = readLinkFromMainCard("src/main/resources/my_card.txt");

        // 3. Импортируем и обрабатываем первую карту, если она указана
        if (firstCardFilePath != null) {
            System.out.println("Cubchoo:");
            loadCard(importer, firstCardFilePath);
        } else {
            System.out.println("Ссылка на первый файл не найдена.");
        }

        // 4. Импортируем и обрабатываем карту из добавленного бинарного файла
        System.out.println("\nИмпортируемая карта:");
        loadCardFromBinary("src/main/resources/Morpeko.crd"); // Укажите путь к бинарному файлу
    }

    // Метод для импорта карты, вывода и сохранения в бинарный файл
    private static Card loadCard(CardImport importer, String filePath) {
        Card card = importer.importCards(filePath);
        if (card != null) {
            System.out.println(card);  // Вывод полной информации о карте
            saveCardToBinary(card);    // Сохраняем карту в бинарный файл
        }
        return card;
    }

    // Метод для сохранения карты в бинарный файл
    private static void saveCardToBinary(Card card) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(card.getName() + ".crd"))) {
            oos.writeObject(card);  // Сохраняем объект карты в файл
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении карты в бинарный файл: " + e.getMessage());
        }
    }

    // Метод для чтения карты из бинарного файла
    private static void loadCardFromBinary(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Card card = (Card) ois.readObject();  // Чтение объекта карты из бинарного файла
            System.out.println(card);  // Вывод полной информации о карте
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при чтении карты из бинарного файла: " + e.getMessage());
        }
    }

    // Метод для чтения ссылки на первый файл из пятого пункта
    private static String readLinkFromMainCard(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            for (int i = 0; i < 5; i++) {
                line = br.readLine();
                if (i == 4 && line != null) {
                    return line;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return null;
    }
}
