package com.google.songgame.servlets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
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
  private String videoId;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = new Gson().toJson(videoId);
    response.getWriter().println(json);
    // Create a round
    Entity roundEntity = new Entity("Round");

    roundEntity.setProperty("startTime", System.currentTimeMillis() + TIME_OFFSET);
    roundEntity.setProperty("endTime", System.currentTimeMillis() + ROUND_LENGTH);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(roundEntity);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String playlistUrl = getParameter(request, "playlist-link", "");
    setVideoId(playlistUrl);
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

  /**
   * Use playlist url to connect to YouTube API and set video ID
   *
   */
  private void setVideoId(String playlistUrl) {
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
    videoId = getRandomVideo(playlistVideos);
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
   * Returns video ID
   */
  private String getRandomVideo(ArrayList<String> playlistVideos) {
    Random randomGenerator = new Random();
    int playlistSize = playlistVideos.size();
    int index = randomGenerator.nextInt(playlistSize);
    String videoId = playlistVideos.get(index);
    return videoId;
  }
}
