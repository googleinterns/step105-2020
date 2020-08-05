package com.google.songgame.servlets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.songgame.data.TitleFormatter;
import com.google.songgame.data.RoomLoader;
import com.google.songgame.data.YoutubeParser;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Random;
// TODO: @salilnadkarni, remove once helper class merged in
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.cloud.datastore.Datastore;
import java.util.Collections;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {

  private DatastoreService datastore;
  private Gson gson;
  // TODO: @salilnadkarni, remove once helper class merged in
  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String roomId = request.getParameter("roomId");
    Entity currentGame = RoomLoader.getCurrentGameFromRoom(roomId);
    Entity currentRoom = RoomLoader.getRoom(roomId);
    List<Entity> users = RoomLoader.getUsersInRoom(currentRoom);
    EmbeddedEntity userPoints = (EmbeddedEntity) currentGame.getProperty("userPoints");
    Map<String, Long> userPointsWithUsernames = new HashMap<String, Long>();
    for (Entity user : users) {
      String userId = (String) user.getProperty("userId");
      String username = (String) user.getProperty("username");
      long points = (Long) userPoints.getProperty(userId);
      userPointsWithUsernames.put(username, points);
    }
    String json = new Gson().toJson(userPointsWithUsernames);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> gamePostParameters = readJSONFromRequest(request);
    String roomId = gamePostParameters.get("roomId");
    Entity currentGame = RoomLoader.getCurrentGameFromRoom(roomId);
    if (currentGame == null) {
      createGame(roomId);
    }
  }

  private void createGame(String roomId) {
    YoutubeParser parser = new YoutubeParser();

    Entity currentRoom = RoomLoader.getRoom(roomId);
    String playlistUrl = (String) currentRoom.getProperty("playlistUrl");

    ArrayList<String> playlistVideoIds = parser.getPlaylistVideoIds(playlistUrl);
    Collections.shuffle(playlistVideoIds);
    long creationTime = System.currentTimeMillis();
    EmbeddedEntity userPoints = createUserPoints(currentRoom);
    long roundNumber = 0;

    Entity gameEntity = new Entity("Game");
    gameEntity.setProperty("roomId", roomId);
    gameEntity.setProperty("playlist", playlistVideoIds);
    gameEntity.setProperty("creationTime", creationTime);
    gameEntity.setProperty("userPoints", userPoints);
    gameEntity.setProperty("roundNumber", roundNumber);

    datastore.put(gameEntity);
  }

  private EmbeddedEntity createUserPoints(Entity currentRoom) {
    EmbeddedEntity userPoints = new EmbeddedEntity();
    // TODO: @salilnadkarni, add more specific query to only get users with correct roomId
    List<Entity> users = RoomLoader.getUsersInRoom(currentRoom);
    for (Entity user : users) {
      String userId = (String) user.getProperty("userId");
      userPoints.setProperty(userId, 0L);
    }

    return userPoints;
  }

  // TODO: @salilnadkarni, remove for helper class once that's merged in
  private Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    return jsonData;
  }
}
