package ru.mirea.pkmn.PanshinDI;
import ru.mirea.pkmn.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardImport {

    public static Card importCard(String filename) throws IOException {
        Card card = new Card();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                processLine(line, card);
            }
        }

        return card;
    }

    private static void processLine(String line, Card card) throws IOException {
        String[] parts = line.split("\\. ");
        switch (parts[0]) {
            case "1":
                card.setPokemonStage(PokemonStage.valueOf(parts[1]));
                break;

            case "2":
                card.setName(parts[1]);
                break;

            case "3":
                card.setHp(Integer.parseInt(parts[1]));
                break;

            case "4":
                card.setPokemonType(EnergyType.valueOf(parts[1]));
                break;

            case "5":
                card.setEvolvesFrom(parts.length == 2 && !parts[1].isEmpty()
                        && !parts[1].equals("-") ? importCard(parts[1]) : null
                );
                break;

            case "6":
                if(parts[1].equals(" - ")) {
                    card.setSkills(null);
                    break;
                }

                List<AttackSkill> skills = new ArrayList<>();
                for (String i : parts[1].split(", ")) {
                    String[] arr = i.split(" / ");
                    AttackSkill attackSkill = new AttackSkill(arr[1], "", arr[0], Integer.parseInt(arr[2]));
                    skills.add(attackSkill);
                }

                card.setSkills(skills);
                break;

            case "7":
                card.setWeaknessType(parts.length == 2 && !parts[1].isEmpty() ? EnergyType.valueOf(parts[1]) : null);
                break;

            case "8":
                card.setResistanceType(parts.length == 2 && !parts[1].isEmpty() && !parts[1].equals("-") ? EnergyType.valueOf(parts[1]) : null);
                break;

            case "9":
                card.setRetreatCost(parts.length == 2 && !parts[1].isEmpty() && !parts[1].equals("-") ? parts[1] : null);
                break;

            case "10":
                card.setGameSet(parts.length == 2 &&
                        !parts[1].isEmpty() &&
                        !parts[1].equals("-")
                        ? parts[1]
                        : null);
                break;

            case "11":
                card.setRegulationMark(parts.length == 2 && !parts[1].isEmpty() ? parts[1].charAt(0) : null);
                break;

            case "12":
                if (parts.length == 2) {
                    String[] ownerInfo = parts[1].split(" / ");
                    card.setPokemonOwner(ownerInfo.length == 4 ?
                            new Student(ownerInfo[1], ownerInfo[0], ownerInfo[2], ownerInfo[3]) : null);

                } else {
                    card.setPokemonOwner(null);
                }
                break;

            case "13":
                if(parts.length == 2) {
                    card.setNumber(parts[1]);
                } else {
                    card.setNumber(null);
                }
                break;
        }
    }

    public static Card importCardByte(String filename) {
        Card card = null;

        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            card = (Card) objectIn.readObject();
        } catch (IOException e) {
            System.err.println("Error while importing card: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Card class not found: " + e.getMessage());
        }

        return card;
    }
}