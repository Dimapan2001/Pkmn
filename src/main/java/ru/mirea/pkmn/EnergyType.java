package ru.mirea.pkmn;

public enum EnergyType {
    FIRE, GRASS, WATER, LIGHTNING, PSYCHIC, FIGHTING, DARKNESS, METAL, FAIRY, DRAGON, COLORLESS;

    // Переопределение метода toString.
    @Override
    public String toString() {
        return name(); // Имя константы перечисления возвращается в виде строки
    }
}
