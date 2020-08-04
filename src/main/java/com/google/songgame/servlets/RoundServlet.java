package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.api.services.youtube.model.Video;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.songgame.data.YoutubeParser;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.HashMap;
import java.lang.Boolean;

@WebServlet("/round")
public final class RoundServlet extends HttpServlet {

  private static final String APP_ID = "1024158";
  private static final String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private static final String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private static final String PUSHER_APPLICATION_NAME = "song-guessing-game";
  private static final String PUSHER_ROUND_CHANNEL_NAME = "start-round";
  private static final String PUSHER_GAME_CHANNEL_NAME = "start-game";
  private static final int TIME_OFFSET = 3000;
  private static final int ROUND_LENGTH = 30000;
  private static final int MAX_USERS = 20;
  private Pusher pusher;
  private Gson gson;
  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();

  DatastoreService datastore;

  @Override
  public void init() {
    pusher = new Pusher(APP_ID, CLIENT_KEY, CLIENT_SECRET);
    pusher.setCluster("us2");
    pusher.setEncrypted(true);

    gson = new Gson();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> roundPostParameters = readJSONFromRequest(request);
    String roomId = roundPostParameters.get("roomId");
    Entity game = getCurrentGame(roomId);

    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    if (isNewGame(game) || roundOver(currentRound)) {
      currentRound = getNewRound(game);

      game.setProperty("currentRound", currentRound);
      datastore.put(game);
    }

    pusher.trigger(
        PUSHER_APPLICATION_NAME,
        PUSHER_GAME_CHANNEL_NAME,
        Collections.singletonMap("message", "Start Round"));
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String roomId = request.getParameter("roomId");
    Entity game = getCurrentGame(roomId);

    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    Map<String, Object> roundMap = createRoundMap(game, currentRound);

    String json = new Gson().toJson(roundMap);
    response.getWriter().println(json);
  }

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // pusher.trigger(
    //     PUSHER_APPLICATION_NAME,
    //     PUSHER_ROUND_CHANNEL_NAME,
    //     Collections.singletonMap("message", "Start Round"));
  }

  private Entity getCurrentGame(String roomId) {
    Filter roomIdFilter = new FilterPredicate("roomId", FilterOperator.EQUAL, roomId);
    Query gameQuery = new Query("Game").setFilter(roomIdFilter);
    PreparedQuery result = datastore.prepare(gameQuery);
    Entity currentGame = result.asSingleEntity();
    return currentGame;
  }

  private boolean isNewGame(Entity game) {
    return game.getProperty("currentRound") == null;
  }

  private boolean roundOver(EmbeddedEntity round) {
    return (long) round.getProperty("endTime") <= System.currentTimeMillis();
  }

  private EmbeddedEntity getNewRound(Entity game) {
    ArrayList<String> playlist = (ArrayList<String>) game.getProperty("playlist");
    String roomId = (String) game.getProperty("roomId");
    EmbeddedEntity video = getVideoEntity(playlist);
    EmbeddedEntity userGuessStatuses = createUserGuessStatuses(roomId);
    EmbeddedEntity currentRound = new EmbeddedEntity();

    currentRound.setProperty("video", video);
    currentRound.setProperty("startTime", System.currentTimeMillis() + TIME_OFFSET);
    currentRound.setProperty("endTime", System.currentTimeMillis() + TIME_OFFSET + ROUND_LENGTH);

    return currentRound;
  }

  private EmbeddedEntity getVideoEntity(ArrayList<String> playlist) {
    YoutubeParser parser = new YoutubeParser();
    Video video = parser.getRandomVideoFromPlaylist(playlist);
    String videoId = video.getId();
    String videoTitle = video.getSnippet().getTitle();

    EmbeddedEntity videoEntity = new EmbeddedEntity();
    videoEntity.setProperty("videoId", videoId);
    videoEntity.setProperty("title", videoTitle);

    return videoEntity;
  }

  private EmbeddedEntity createUserGuessStatuses(String roomId) {
    EmbeddedEntity userGuessStatuses = new EmbeddedEntity();

    Entity room = getRoom(roomId);
    List<Entity> users = getUsersInRoom(room);

    for (Entity user : users) {
      String userId = (String) user.getProperty("userId");
      userGuessStatuses.setProperty(userId, false);
    }

    return userGuessStatuses;
  }

  private Map<String, Object> createRoundMap(Entity game, EmbeddedEntity round) {
    boolean isNewGame = isNewGame(game);
    EmbeddedEntity currentVideo = (EmbeddedEntity) round.getProperty("video");
    String currentVideoId = (String) currentVideo.getProperty("videoId");
    long roundStartTime = (long) round.getProperty("startTime");
    long roundEndTime = (long) round.getProperty("endTime");

    Map<String, Object> roundMap = new HashMap<String, Object>();
    roundMap.put("isNewGame", isNewGame);
    roundMap.put("videoId", currentVideoId);
    roundMap.put("startTime", roundStartTime);
    roundMap.put("endTime", roundEndTime);

    return roundMap;
  }

  private Entity getRoom(String roomId) {
    Filter roomIdFilter = new FilterPredicate("roomId", FilterOperator.EQUAL, roomId);
    Query roomQuery = new Query("Room").setFilter(roomIdFilter);
    PreparedQuery result = datastore.prepare(roomQuery);
    Entity currentRoom = result.asSingleEntity();
    return currentRoom;
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
