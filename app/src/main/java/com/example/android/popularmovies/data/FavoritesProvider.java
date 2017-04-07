package com.example.android.popularmovies.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = FavoritesProvider.AUTHORITY, database = FavoritesDatabase.class)
public class FavoritesProvider {

    public FavoritesProvider() {
    }

    public static final String AUTHORITY = "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVORITES_NAME = "favorites";
    }

    private static Uri buildFavoritesUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String s : paths) {
            builder.appendPath(s);
        }
        return builder.build();
    }

    @TableEndpoint(table = FavoritesDatabase.FAVORITES)
    public static class Favorites {
        @ContentUri(
                path = Path.FAVORITES_NAME,
                type = "vnd.android.cursor.dir/favorite",
                defaultSort = FavoritesContract._ID + " ASC")
        public static final Uri CONTENT_URI = buildFavoritesUri(Path.FAVORITES_NAME);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.FAVORITES_NAME + "/*",
                type = "vnd.android.cursor.item/favorite",
                whereColumn = FavoritesContract.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(String id) {
            return buildFavoritesUri(Path.FAVORITES_NAME, id);
        }
    }
}


