package charbitanos.demo.services;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class ApiConf {

    public final static ObjectMapper MAPPER = new ObjectMapper();

    public final static String URL = "https://www.omdbapi.com/?t=";
    public final static String API_KEY = "&apikey=" + System.getenv("APIKEY_OMDB");
    private ApiConf() {}
}
