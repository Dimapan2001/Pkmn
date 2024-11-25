package ru.mirea.pkmn.PanshinDI.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mirea.pkmn.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseServiceImpl implements DatabaseService {

    private final Connection connection;

    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {

        // Загружаем файл database.properties

        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src/main/resources/database.properties"));

        // Подключаемся к базе данных

        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));
    }

    private Card mapResultSetToCard(ResultSet rs) throws SQLException, JsonProcessingException {
        Card card = new Card();
        card.setName(rs.getString("name"));
        card.setNumber(rs.getString("card_number"));
        card.setPokemonStage(PokemonStage.valueOf(rs.getString("stage")));
        card.setHp(rs.getInt("hp"));
        card.setPokemonType(EnergyType.valueOf(rs.getString("pokemon_type")));

        String evolvesFrom = rs.getString("evolves_from");
        card.setEvolvesFrom(evolvesFrom != null ? getCardFromDatabaseById(UUID.fromString(evolvesFrom)) : null);

        JsonNode attacks_json = new ObjectMapper().readTree(rs.getString("attack_skills"));
        List<AttackSkill> attack_skills = new ArrayList<>();
        for(JsonNode attack : attacks_json) {
            attack_skills.add(new AttackSkill(attack.findValue("name").asText(),
                    (attack.findValue("text") != null ?
                            attack.findValue("text").asText() : ""),
                    attack.findValue("cost").asText(),
                    attack.findValue("damage").asInt()));
        }
        card.setSkills(attack_skills);

        card.setWeaknessType(rs.getString("weakness_type") != null
                ? EnergyType.valueOf(rs.getString("weakness_type")) : null);
        card.setResistanceType(rs.getString("resistance_type") != null
                ? EnergyType.valueOf(rs.getString("resistance_type")) : null);

        card.setRetreatCost(rs.getString("retreat_cost"));
        card.setGameSet(rs.getString("game_set"));
        card.setRegulationMark(rs.getString("regulation_mark").charAt(0));
        // if(!rs.next()) return card;
        String pokemonOwner = rs.getString("pokemon_owner");
        card.setPokemonOwner(pokemonOwner != null ? getStudentFromDatabaseById(UUID.fromString(pokemonOwner)) : null);

//        int columnCount = rs.getMetaData().getColumnCount();
//
//        // Выводим наименования столбцов
//        for (int i = 1; i <= columnCount; i++) {
//            String columnName = rs.getMetaData().getColumnName(i);
//            System.out.print(columnName); // Печатаем имя столбца
//            if (i < columnCount) {
//                System.out.print(", "); // Добавляем запятую между названиями
//            }
//        }
//        System.out.println(); // Переход на новую строку после заголовков
//
//        // Перебираем все строки в ResultSet
//        while (rs.next()) {
//            StringBuilder row = new StringBuilder();
//            for (int i = 1; i <= columnCount; i++) {
//                row.append(rs.getString(i)); // Получаем значение колонки как строку
//                if (i < columnCount) {
//                    row.append(", "); // Добавляем запятую между значениями
//                }
//            }
//            System.out.println(row.toString()); // Печатаем строку
//        }

        return card;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("firstName"),
                rs.getString("familyName"),
                rs.getString("patronicName"),
                rs.getString("group")
        );
    }

    public Card getCardFromDatabaseById(UUID uuid)  throws SQLException, JsonProcessingException {
        String query = "SELECT * FROM card WHERE \"id\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToCard(rs) : null;
            }
        }
    }

    public Student getStudentFromDatabaseById(UUID uuid) throws SQLException {
        String query = "SELECT * FROM student WHERE \"id\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToStudent(rs) : null;
            }
        }
    }

    public UUID getCardIdFromDatabase(String cardName) throws SQLException, JsonProcessingException {
        String query = "SELECT * FROM card WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, cardName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? UUID.fromString(rs.getString("id")) : null;
            }
        }
    }

    public UUID getStudentIdFromDatabase(String studentFullName) throws SQLException {
        String[] split = studentFullName.split(" ");
        String query = "SELECT * FROM student WHERE \"familyName\" = ? AND \"firstName\" = ? AND \"patronicName\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, split[0]);
            ps.setString(2, split[1]);
            ps.setString(3, split[2]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? UUID.fromString(rs.getString("id")) : null;
            }
        }
    }

    @Override
    public Card getCardFromDatabase(String cardName) throws SQLException, JsonProcessingException {
        String query = "SELECT * FROM card WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, cardName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToCard(rs) : null;
            }
        }
    }

    @Override
    public Student getStudentFromDatabase(String studentFullName) throws SQLException {
        String[] split = studentFullName.split(" ");
        String query = "SELECT * FROM student WHERE \"familyName\" = ? AND \"firstName\" = ? AND \"patronicName\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, split[0]);
            ps.setString(2, split[1]);
            ps.setString(3, split[2]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToStudent(rs) : null;
            }
        }
    }

    @Override
    public void saveCardToDatabase(Card card) throws SQLException, JsonProcessingException {
        UUID evolvesFromId = null;
        if (card.getEvolvesFrom() != null) {
            evolvesFromId = getCardIdFromDatabase(card.getEvolvesFrom().getName());
            while (evolvesFromId == null) {
                saveCardToDatabase(card.getEvolvesFrom());
                evolvesFromId = getCardIdFromDatabase(card.getEvolvesFrom().getName());
                if(evolvesFromId != null)
                    System.out.println("Create new card: id -> " + evolvesFromId
                            + "name -> " + card.getEvolvesFrom().getName());
            }
        }

        UUID ownerId = null;
        if (card.getPokemonOwner() != null) {
            String studentFullName = card.getPokemonOwner().getSurName() + " " +
                    card.getPokemonOwner().getFirstName() + " " + card.getPokemonOwner().getFamilyName();
            ownerId = getStudentIdFromDatabase(studentFullName);

            while (ownerId == null) {
                createPokemonOwner(card.getPokemonOwner());
                ownerId = getStudentIdFromDatabase(studentFullName);
                if(ownerId != null)
                    System.out.println("Create new owner: id -> " + ownerId + "name -> " + studentFullName);
            }
        }

        String query = "INSERT INTO card(id, name, hp, evolves_from, game_set, pokemon_owner, stage, retreat_cost, " +
                "weakness_type, resistance_type, attack_skills, pokemon_type, regulation_mark, card_number)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, UUID.randomUUID());
            ps.setString(2, card.getName());
            ps.setInt(3, card.getHp());
            ps.setObject(4, evolvesFromId);
            ps.setObject(5, card.getGameSet());
            ps.setObject(6, ownerId);
            ps.setString(7, card.getPokemonStage().name());
            ps.setString(8, card.getRetreatCost());
            ps.setString(9, card.getWeaknessType() != null ? card.getWeaknessType().name() : null);
            ps.setString(10, card.getResistanceType() != null ? card.getResistanceType().name() : null);
            ps.setString(11, new ObjectMapper().writeValueAsString(card.getSkills()));
            ps.setString(12, card.getPokemonType().name());
            ps.setString(13, String.valueOf(card.getRegulationMark()));
            ps.setString(14, card.getNumber());
            ps.executeUpdate();
        }
    }

    @Override
    public void createPokemonOwner(Student owner) throws SQLException {
        String query = "INSERT INTO student(id, \"familyName\", \"firstName\", \"patronicName\", \"group\") " +
                "VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, UUID.randomUUID());
            ps.setString(2, owner.getFirstName());
            ps.setString(3, owner.getSurName());
            ps.setString(4, owner.getFamilyName());
            ps.setString(5, owner.getGroup());
            ps.executeUpdate();
        }
    }
}