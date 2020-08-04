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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.songgame.data.TitleFormatter;
import com.google.songgame.data.YoutubeParser;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Random;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.json.gson.GsonFactory;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {

  private DatastoreService datastore;
  private Gson gson;
  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();
  private static final int MAX_USERS = 20;

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    gson = new Gson();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> gamePostParameters = readJSONFromRequest(request);
    String roomId = gamePostParameters.get("roomId");
    createGame(roomId);
  }

  /**
   * Returns the request parameter, or the default value if the parameter was not specified by the
   * client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  private void createGame(String roomId) {
    YoutubeParser parser = new YoutubeParser();

    Entity currentRoom = getRoom(roomId);
    String playlistUrl = (String) currentRoom.getProperty("playlistUrl");

    ArrayList<String> playlistVideoIds = parser.getPlaylistVideoIds(playlistUrl);
    long creationTime = System.currentTimeMillis();
    EmbeddedEntity userPoints = createUserPoints(currentRoom);

    Entity gameEntity = new Entity("Game");
    gameEntity.setProperty("roomId", roomId);
    gameEntity.setProperty("playlist", playlistVideoIds);
    gameEntity.setProperty("creationTime", creationTime);
    gameEntity.setProperty("userPoints", userPoints);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(gameEntity);
  }

  private Entity getRoom(String roomId) {
    Filter roomIdFilter = new FilterPredicate("roomId", FilterOperator.EQUAL, roomId);
    Query roomQuery = new Query("Room").setFilter(roomIdFilter);
    PreparedQuery result = datastore.prepare(roomQuery);
    Entity currentRoom = result.asSingleEntity();
    return currentRoom;
  }

  private EmbeddedEntity createUserPoints(Entity currentRoom) {
    EmbeddedEntity userPoints = new EmbeddedEntity();
    // TODO: @salilnadkarni, add more specific query to only get users with correct roomId
    List<Entity> users = getUsersInRoom(currentRoom);
    for (Entity user : users) {
      String userId = (String) user.getProperty("userId");
      userPoints.setProperty(userId, 0L);
    }

    return userPoints;
  }

  private List<Entity> getUsersInRoom(Entity room) {
    List<String> userIds = (List<String>) room.getProperty("userIdList");
    Filter usersInRoomFilter = new FilterPredicate("userId", FilterOperator.IN, userIds);
    Query usersInRoomQuery = new Query("User").setFilter(usersInRoomFilter);
    PreparedQuery result = datastore.prepare(usersInRoomQuery);
    return result.asList(FetchOptions.Builder.withLimit(MAX_USERS));
  }

  private Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    return jsonData;
  }
}
