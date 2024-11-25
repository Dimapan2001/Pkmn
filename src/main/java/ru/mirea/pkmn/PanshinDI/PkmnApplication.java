package ru.mirea.pkmn.PanshinDI;

import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.AttackSkill;
import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.PanshinDI.web.http.PkmnHttpClient;
import ru.mirea.pkmn.PanshinDI.web.jdbc.DatabaseServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PkmnApplication {
    public static void main(String[] args) throws IOException, SQLException {

        Card card = new Card();
        Scanner scanner = new Scanner(System.in);
        DatabaseServiceImpl db = new DatabaseServiceImpl();
        boolean flag = true;

        System.out.println("Выберите действие:");
        System.out.println("0 - Выход");
        System.out.println("1 - Импорт из текстового файла");
        System.out.println("2 - Импорт из бинарного файла");
        System.out.println("3 - Экспорт в бинарный файл");
        System.out.println("4 - Парсинг описаний умений (Задание 4.1)");
        System.out.println("5 - Сохранение карточки в бд (Задание 4.2)");
        System.out.println("6 - Проверяем карточки в бд без добавления (Задание 4.2)");

        db.getCardFromDatabase("Beartic");
        System.out.println(db.getStudentIdFromDatabase("Паньшин Дмитрий Игоревич"));

        while (flag) {
            switch (scanner.nextInt()) {
                case 0:
                    flag = false;
                    break;

                case 1:
                    card = CardImport.importCard("src/main/resources/my_card.txt");
                    System.out.println(card);
                    break;

                case 2:
                    card = CardImport.importCardByte("Beartic.crd");
                    System.out.println(card);
                    break;

                case 3:
                    CardExport.exportCard(card);
                    break;

                case 4:
                    card = CardImport.importCard("src/main/resources/my_card.txt");
                    System.out.println(card);
                    updateSkills(card);
                    System.out.println(card);
                    break;

                case 5:
                    card = CardImport.importCard("src/main/resources/my_card.txt");
                    updateSkills(card, false);
                    db.saveCardToDatabase(card);
                    System.out.println(db.getCardFromDatabase(card.getName()));
                    break;

                case 6:
                    card = CardImport.importCard("src/main/resources/my_card.txt");
                    System.out.println(db.getCardFromDatabase(card.getName()));
                    break;

            }
        }
    }
    public static void updateSkills(Card card) throws IOException {
        updateSkills(card, true);
    }

    public static void updateSkills(Card card, boolean flag) throws IOException {
        if(card.getEvolvesFrom() != null)
            updateSkills(card.getEvolvesFrom(), flag);
        PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();
        JsonNode card_jn = pkmnHttpClient.getPokemonCard(card.getName(), card.getNumber());
        if (flag)
            System.out.println(card_jn.toPrettyString());


        Stream<JsonNode> stream = card_jn.findValues("attacks").stream();
        JsonNode attacks = stream.toList().getFirst();

        for(JsonNode attack : attacks) {
            for(AttackSkill skill : card.getSkills()) {
                if(skill.getName().equals(attack.findValue("name").asText())) {
                    skill.setDescription(attack.findValue("text").asText());
                }
            }
        }
        stream.close();
    }
}