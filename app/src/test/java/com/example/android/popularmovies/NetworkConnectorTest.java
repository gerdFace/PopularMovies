package com.example.android.popularmovies;

import com.example.android.popularmovies.network.NetworkConnector;

import java.net.URL;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetworkConnectorTest {

    @Test
    public void testGetTrailersUri() throws Exception {
        String movieId = "123";
        String apiKey = "123456789";
        String expectedResult = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + apiKey;

        URL expectedUrl = new URL(expectedResult);

        NetworkConnector networkConnector = new NetworkConnector();
//        URL actualUrl = networkConnector.buildUrl(movieId);

//        assertEquals(expectedUrl, actualUrl);
    }
}
