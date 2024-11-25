package ru.mirea.pkmn;

public enum PokemonStage {
    BASIC("BASIC"),
    STAGE1("STAGE1"),
    STAGE2("STAGE2"),
    VSTAR("VSTAR"),
    VMAX("VMAX");

    private String stage;

    PokemonStage(String stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return stage;
    }
}
