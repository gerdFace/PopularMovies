package com.example.android.popularmovies.data;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = FavoritesDatabase.VERSION)
public final class FavoritesDatabase {

    private FavoritesDatabase() {
    }

    public static final int VERSION = 3;

    @Table(FavoritesContract.class)
    public static final String FAVORITES = "favorites";

}
