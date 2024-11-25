package ru.mirea.pkmn.PanshinDI.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.Student;

import java.sql.SQLException;

public interface DatabaseService {

    Card getCardFromDatabase(String cardName) throws SQLException, JsonProcessingException;

    Student getStudentFromDatabase(String studentFullName) throws SQLException;

    void saveCardToDatabase(Card card) throws SQLException, JsonProcessingException;

    void createPokemonOwner(Student owner) throws SQLException;
}