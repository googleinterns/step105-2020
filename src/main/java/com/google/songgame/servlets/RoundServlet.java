package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.api.services.youtube.model.Video;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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
  private Pusher pusher;
  private Gson gson;
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
    pusher.trigger(
        PUSHER_APPLICATION_NAME,
        PUSHER_GAME_CHANNEL_NAME,
        Collections.singletonMap("message", "Start Game"));
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity game = getCurrentGame();
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    if (isNewGame(game) || roundOver(currentRound)) {
      currentRound = getNewRound(game);

      game.setProperty("currentRound", currentRound);
      datastore.put(game);
    }
    Map<String, Object> roundMap = createRoundMap(game, currentRound);

    String json = new Gson().toJson(roundMap);
    response.getWriter().println(json);
  }

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    pusher.trigger(
        PUSHER_APPLICATION_NAME,
        PUSHER_ROUND_CHANNEL_NAME,
        Collections.singletonMap("message", "Start Round"));
  }

  private Entity getCurrentGame() {
    Query gameQuery = new Query("Game").addSort("creationTime", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(gameQuery);
    Entity currentGame = result.asList(FetchOptions.Builder.withLimit(1)).get(0);
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
    EmbeddedEntity video = getVideoEntity(playlist);
    EmbeddedEntity userGuessStatuses = createUserGuessStatuses();

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

  private EmbeddedEntity createUserGuessStatuses() {
    EmbeddedEntity userGuessStatuses = new EmbeddedEntity();
    // TODO: @salilnadkarni, add more specific query to only get users with correct roomId
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);

    for (Entity user : results.asIterable()) {
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
}
  