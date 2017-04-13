package com.example.android.popularmovies;

import com.example.android.popularmovies.data.JsonMovieDataExtractor;
import com.example.android.popularmovies.model.Trailer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JsonMovieDataExtractorTest {

    @Test
    public void testGetExtractedTrailerStringsWithOneTrailer() throws Exception {
        // Arrange
        //language=JSON
        String trailerJsonResponse = "{\"adult\":false,\"backdrop_path\":\"/6aUWe0GSl69wMTSWWexsorMIvwU.jpg\",\"belongs_to_collection\":null,\"budget\":160000000,\"genres\":[{\"id\":14,\"name\":\"Fantasy\"},{\"id\":10402,\"name\":\"Music\"},{\"id\":10749,\"name\":\"Romance\"}],\"homepage\":\"http://movies.disney.com/beauty-and-the-beast-2017\",\"id\":321612,\"imdb_id\":\"tt2771200\",\"original_language\":\"en\",\"original_title\":\"Beauty and the Beast\",\"overview\":\"A live-action adaptation of Disney's version of the classic 'Beauty and the Beast' tale of a cursed prince and a beautiful young woman who helps him break the spell.\",\"popularity\":187.137858,\"poster_path\":\"/tnmL0g604PDRJwGJ5fsUSYKFo9.jpg\",\"production_companies\":[{\"name\":\"Walt Disney Pictures\",\"id\":2},{\"name\":\"Mandeville Films\",\"id\":10227}],\"production_countries\":[{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"2017-03-15\",\"revenue\":693519966,\"runtime\":123,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"}],\"status\":\"Released\",\"tagline\":\"Be our guest.\",\"title\":\"Beauty and the Beast\",\"video\":false,\"vote_average\":7.2,\"vote_count\":985}";

        ArrayList<String> expectedTrailerList = new ArrayList<>();
        Trailer expectedTrailer = new Trailer("550", "SUXWAEX2jlg", "Trailer 1");

        // Act
        ArrayList<Trailer> actualTrailerList = new JsonMovieDataExtractor().getExtractedTrailerStringsFromJson(trailerJsonResponse);

        // Assert
        assertEquals(expectedTrailerList.size(), actualTrailerList.size());
    }
}
