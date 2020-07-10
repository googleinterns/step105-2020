package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;
import com.google.gson.Gson; 
import java.util.ArrayList;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {
  private static final int TIME_OFFSET = 3000;
  private static final int ROUND_LENGTH = 30000;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String authToken = getAuthToken();
    ArrayList<String> jsonArray = new ArrayList<String>();
    jsonArray.add(authToken);
    Gson gson = new Gson();
    String jsonData = gson.toJson(jsonArray);
    response.setContentType("application/json;");
    response.getWriter().println(jsonData);

    Entity roundEntity = new Entity("Round");

    roundEntity.setProperty("startTime", System.currentTimeMillis() + TIME_OFFSET);
    roundEntity.setProperty("endTime", System.currentTimeMillis() + ROUND_LENGTH);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(roundEntity);
  }
  

  private String getAuthToken() {
    String clientId = "1c29ff191b444611a6d9dbb4a354642f";
    String clientSecret = "6b92b6eb8fe24d5e98178407d3099bd8";
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
    ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

    try {
      ClientCredentials clientCredentials = clientCredentialsRequest.execute();
      return clientCredentials.getAccessToken();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return "";
  }

}
