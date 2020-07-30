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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.json.gson.GsonFactory;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {

  private DatastoreService datastore;
  private Gson gson;

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity game = getCurrentGame();
    EmbeddedEntity currentRound = getNewRound(game);
    game.setProperty("currentRound", currentRound);
    datastore.put(game);
    EmbeddedEntity currentVideo = (EmbeddedEntity) currentRound.getProperty("video");
    String currentVideoId = (String) currentVideo.getProperty("videoId");
    String json = new Gson().toJson(currentVideoId);
    response.getWriter().println(json);
  }

  private Entity getCurrentGame() {
    Query gameQuery = new Query("Game").addSort("creationTime", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(gameQuery);
    Entity currentGame = result.asList(FetchOptions.Builder.withLimit(1)).get(0);
    return currentGame;
  }

  private EmbeddedEntity getNewRound(Entity game) {
    ArrayList<String> playlist = (ArrayList<String>) game.getProperty("playlist");
    EmbeddedEntity video = getVideoEntity(playlist);
    EmbeddedEntity userGuessStatuses = createUserGuessStatuses();

    EmbeddedEntity currentRound = new EmbeddedEntity();
    currentRound.setProperty("video", video);
    currentRound.setProperty("userGuessStatuses", userGuessStatuses);

    return currentRound;
  }

  private EmbeddedEntity getVideoEntity(ArrayList<String> playlist) {
    YoutubeParser parser = new YoutubeParser();
    Video video = parser.getRandomVideoFromPlaylist(playlist);
    String videoId = video.getId();
    String unformattedVideoTitle = video.getSnippet().getTitle();
    String videoTitle = TitleFormatter.formatVideoTitle(unformattedVideoTitle);
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

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String playlistUrl = getParameter(request, "playlist-link", "");
    createGame(playlistUrl);
    response.sendRedirect("/game.html");
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

  private void createGame(String playlistUrl) {
    YoutubeParser parser = new YoutubeParser();
    ArrayList<String> playlistVideoIds = parser.getPlaylistVideoIds(playlistUrl);
    long creationTime = System.currentTimeMillis();
    EmbeddedEntity userPoints = createUserPoints();

    Entity gameEntity = new Entity("Game");
    gameEntity.setProperty("playlist", playlistVideoIds);
    gameEntity.setProperty("creationTime", creationTime);
    gameEntity.setProperty("userPoints", userPoints);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(gameEntity);
  }

  private EmbeddedEntity createUserPoints() {
    EmbeddedEntity userPoints = new EmbeddedEntity();
    // TODO: @salilnadkarni, add more specific query to only get users with correct roomId
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);

    for (Entity user : results.asIterable()) {
      String userId = (String) user.getProperty("userId");
      userPoints.setProperty(userId, 0);
    }

    return userPoints;
  }
}
