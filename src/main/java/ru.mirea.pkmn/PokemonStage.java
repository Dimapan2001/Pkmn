package ru.mirea.pkmn;

public enum PokemonStage {
    BASIC, STAGE1, STAGE2, VSTAR, VMAX;

    // Переопределение метода toString для вывода стадии в виде строки
    @Override
    public String toString() {
        return name(); // Возвращаем имя константы
    }
}
