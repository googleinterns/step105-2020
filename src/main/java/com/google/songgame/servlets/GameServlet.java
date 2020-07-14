package com.google.songgame.servlets;

import com.google.api.client.auth.oauth2.Credential;
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

@WebServlet("/game")
public final class GameServlet extends HttpServlet {

  private static final String DEVELOPER_KEY = "AIzaSyBZw4Z25Lect7ux9z960RCM7YORcYo6slc";
  private static final String CLIENT_SECRETS = "/client_secret.json";
  private static final Collection<String> SCOPES =
      Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

  private static final String APPLICATION_NAME = "Song Guessing Game";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private int PLAYLIST_SIZE;
  private String videoID;
  private static final int TIME_OFFSET = 3000;
  private static final int ROUND_LENGTH = 30000;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = new Gson().toJson(videoID);
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
    String url = getParameter(request, "playlist-link", "");
    setVideoID(url);
    response.sendRedirect("/game.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Use url to connect to YouTube API and set video ID.
   *
   * @param url
   */
  public void setVideoID(String url) {
    String playlistID = getIDFromURL(url);
    PlaylistItemListResponse playlistItem = new PlaylistItemListResponse();
    // Retrieve Playlist item from Youtube API
    try {
      playlistItem = getPlaylistInfo(playlistID);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Parse Playlist item Json string to retrieve video IDs
    String playlistItemJson = new Gson().toJson(playlistItem);
    ArrayList<String> playlistVideos = parsePlaylistItem(playlistItemJson);
    PLAYLIST_SIZE = playlistVideos.size();
    videoID = getRandomVideo(playlistVideos);
  }

  /**
   * Parse Youtube URL for playlist ID.
   *
   * @param url
   * @return playlist ID
   */
  public String getIDFromURL(String url) {
    if (url.contains("youtube.com/playlist?list=")) {
      int start = url.indexOf("list=") + 5;
      int end = url.length();
      if (url.contains("&")) {
        end = url.indexOf("&", start);
      }
      return url.substring(start, end);
    } else {
      System.err.println(url + " is not a valid YouTube Playlist URL");
      return "";
      // TODO @hdee: do something more complicated to handle this error
    }
  }

  /**
   * Call function to create API service object.
   *
   * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
   */
  public static PlaylistItemListResponse getPlaylistInfo(String playlistID)
      throws GeneralSecurityException, IOException, GoogleJsonResponseException {
    YouTube youtubeService = getService();
    // Define and execute the API request
    YouTube.PlaylistItems.List request =
        youtubeService
            .playlistItems()
            .list("snippet")
            .setMaxResults(25L)
            .setPlaylistId(playlistID);
    PlaylistItemListResponse response = request.execute();
    return response;
  }

  /**
   * Build and return an authorized API client service.
   *
   * @return an authorized API client service
   * @throws GeneralSecurityException, IOException
   */
  public static YouTube getService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    Credential credential = null;
    return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(DEVELOPER_KEY))
        .build();
  }

  /**
   * @param playlistItemJson
   * @return an ArrayList of video IDs
   */
  private ArrayList<String> parsePlaylistItem(String playlistItemJson) {
    String[] playlistItemData = playlistItemJson.split("\",\"");
    ArrayList<String> playlistVideos = new ArrayList<String>();
    // extract video ID from sections
    for (String data : playlistItemData)
      if (data.startsWith("videoId\":\"")) {
        int idStart = data.indexOf("\":\"") + 3;
        int idEnd = data.indexOf("\"", idStart);
        String videoID = data.substring(idStart, idEnd);
        playlistVideos.add(videoID);
      }
    return playlistVideos;
  }

  /**
   * @param playlistVideos
   * @return video ID
   */
  private String getRandomVideo(ArrayList<String> playlistVideos) {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(PLAYLIST_SIZE);
    String videoID = playlistVideos.get(index);
    return videoID;
  }
}
