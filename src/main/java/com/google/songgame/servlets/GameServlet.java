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
import com.google.songgame.data.YoutubeParser;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Random;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.json.gson.GsonFactory;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {

  private static final String DEVELOPER_KEY = "AIzaSyBZw4Z25Lect7ux9z960RCM7YORcYo6slc";

  private static final String APPLICATION_NAME = "Song Guessing Game";
  private static final JsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final int TIME_OFFSET = 3000;
  private static final int ROUND_LENGTH = 30000;
  private static final long MAX_RESULTS = 25L;
  private DatastoreService datastore;

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity game = getCurrentGame();
    EmbededEntity currentRound = getNextRound(game);
    game.setProperty("currentRound", currentRound);

    datastore.put(game);

    String videoId = currentRound.getProperty("videoId");
    String json = new Gson().toJson(mostRecentVideoId);
    response.getWriter().println(json);


    Entity roundEntity = new Entity("Round");
    EmbeddedEntity userStatuses = createUserStatuses();
    EmbeddedEntity userPoints = createUserPoints();
    roundEntity.setProperty("startTime", System.currentTimeMillis() + TIME_OFFSET);
    roundEntity.setProperty("endTime", System.currentTimeMillis() + ROUND_LENGTH);
    roundEntity.setProperty("userStatuses", userStatuses);
    roundEntity.setProperty("userPoints", userPoints);
    datastore.put(roundEntity);
  }

  private Entity getCurrentGame() {
    Query gameQuery = new Query("Game").addSort("creationTime", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(gameQuery);
    
    Entity currentGame = result.asList(FetchOptions.Builder.withLimit(1)).get(0);
    return currentGame;
  }

  private EmbededEntity getNextRound(Entity game) {
    ArrayList<String> playlist = (ArrayList<String>) game.getProperty("playlist");
    EmbededEntity video = getVideoEntity(playlist);  

    EmbededEntity currentRound = new EmbededEntity();
    currentRound.setProperty("video", video);
  }

  private EmbededEntity getVideoEntity(ArrayList<String> playlist) {
    YoutubeParser parser = new YoutubeParser();
    Video video = parser.getRandomVideoFromPlaylist(playlist);
    String videoId = video.getId();
    String videoTitle = video.getSnippet().getTitle();

    EmbededEntity video = new EmbededEntity();
    videoEntity.setProperty("videoId", videoTitle);
    videoEntity.setProperty("title", videoTitle);
    
    return videoEntity;
  }

  private EmbeddedEntity createUserStatuses() {
    EmbeddedEntity userStatuses = new EmbeddedEntity();
    //TODO: @salilnadkarni, add more specific query to only get users with correct roomId
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);

    for (Entity user : results.asIterable()) {
      String userId = (String) user.getProperty("userId");
      userStatuses.setProperty(userId, false);
    }

    return userStatuses;
  }

  private EmbeddedEntity createUserPoints() {
    EmbeddedEntity userPoints = new EmbeddedEntity();
    //TODO: @salilnadkarni, add more specific query to only get users with correct roomId
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);

    for (Entity user : results.asIterable()) {
      String userId = (String) user.getProperty("userId");
      userPoints.setProperty(userId, 0);
    }

    return userPoints;
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

    Entity gameEntity = new Entity("Game");
    gameEntity.setProperty("playlist", playlistVideoIds);
    gameEntity.setProperty("creationTime", creationTime);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(gameEntity);
  }











  /**
   * Use playlist url to connect to YouTube API and set video ID
   *
   */
  private void setVideo(String playlistUrl) {
    String playlistId = getPlaylistIdFromUrl(playlistUrl);
    PlaylistItemListResponse playlistItem = new PlaylistItemListResponse();
    // Retrieve Playlist item from Youtube API
    try {
      playlistItem = getPlaylistInfo(playlistId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Parse Playlist item Json string to retrieve video IDs
    String playlistItemJson = new Gson().toJson(playlistItem);
    createGameAndStorePlaylist(playlistItemJson);
    ArrayList<String> playlistVideos = parseVideoIdsFromPlaylistItem(playlistItemJson);
    String videoId = getRandomVideo(playlistVideos);

    // Store information about Video in datastore
    try {
      Video currentVideo = getVideoInfo(videoId);
      setVideoInfo(currentVideo);
    } catch (Exception e) {
      System.err.println("ERROR: Could not read video");
    }

  }

  /**
   * Returns playlist ID
   */
  private String getPlaylistIdFromUrl(String playlistUrl) {
    if (playlistUrl.contains("youtube.com/playlist?list=")) {
      int start = playlistUrl.indexOf("list=") + 5;
      int end = playlistUrl.length();
      if (playlistUrl.contains("&")) {
        end = playlistUrl.indexOf("&", start);
      }
      return playlistUrl.substring(start, end);
    } else {
      System.err.println(playlistUrl + " is not a valid YouTube Playlist URL.");
      return "";
      // TODO @hdee: do something more complicated to handle this error.
      // TODO @hdee: test this method.
    }
  }

  /**
   * Call function to create API service object.
   *
   * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
   */
  public static PlaylistItemListResponse getPlaylistInfo(String playlistId)
      throws GeneralSecurityException, IOException, GoogleJsonResponseException {
    YouTube youtubeService = getService();
    // Define and execute the API request
    YouTube.PlaylistItems.List request =
        youtubeService
            .playlistItems()
            .list("snippet")
            .setMaxResults(MAX_RESULTS)
            .setPlaylistId(playlistId);
    PlaylistItemListResponse response = request.execute();
    return response;
  }

  /**
   * Build and return an authorized API client service.
   *
   * Returns an authorized API client service
   * @throws GeneralSecurityException, IOException
   */
  public static YouTube getService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new YouTube.Builder(httpTransport, GSON_FACTORY, null)
        .setApplicationName(APPLICATION_NAME)
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(DEVELOPER_KEY))
        .build();
  }

  /**
   * Create a Game Entity in datastore and add playlist information to it.
   */
  private void createGameAndStorePlaylist(String playlistItemJson){
    // Create a game
    Entity gameEntity = new Entity("Game");
    Text playlistItem = new Text(playlistItemJson);

    // Store playlist in game entity
    gameEntity.setProperty("playlist", playlistItem);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(gameEntity);
  }

  /**
   * Returns an ArrayList of video IDs
   * TODO @hdee: add tests for this method
   */
  private ArrayList<String> parseVideoIdsFromPlaylistItem(String playlistItemJson) {
    String[] playlistItemData = playlistItemJson.split("\",\"");
    ArrayList<String> playlistVideos = new ArrayList<String>();
    // extract video ID from sections
    for (String data : playlistItemData)
      if (data.startsWith("videoId\":\"")) {
        int idStart = data.indexOf("\":\"") + 3;
        int idEnd = data.indexOf("\"", idStart);
        String videoId = data.substring(idStart, idEnd);
        playlistVideos.add(videoId);
      }
    return playlistVideos;
  }

  /**
   * Retrieve random video from array of videos and stores video in datastore
   */
  private String getRandomVideo(ArrayList<String> playlistVideos) {
    Random randomGenerator = new Random();
    int playlistSize = playlistVideos.size();
    int index = randomGenerator.nextInt(playlistSize);
    String videoId = playlistVideos.get(index);

    return videoId;
  }

  /**
   * Retrieves video information given a particular video ID
   */
  private Video getVideoInfo(String videoId) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
    YouTube youtubeService = getService();
    YouTube.Videos.List request = youtubeService.videos().list("snippet");
    VideoListResponse response = request.setId(videoId).execute();
    Video video = response.getItems().get(0);
    return video;    
  }

  /**
   * Set video information in Datastore
   */
  private void setVideoInfo(Video video) {
    String videoTitle = video.getSnippet().getTitle();
    long fetchTime = System.currentTimeMillis();
    String videoId = video.getId();
    
    Entity videoEntity = new Entity("Video");
    videoEntity.setProperty("title", videoTitle);
    videoEntity.setProperty("fetchTime", fetchTime);
    videoEntity.setProperty("videoId", videoId);
    
    datastore.put(videoEntity);
  }

}
