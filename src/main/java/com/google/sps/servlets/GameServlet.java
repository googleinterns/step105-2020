package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.YouTubeRequestInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {
  

    private static final String DEVELOPER_KEY = "AIzaSyBZw4Z25Lect7ux9z960RCM7YORcYo6slc";
    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "Song Guessing Game";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static int PLAYLIST_SIZE = 0;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String url = getParameter(request, "playlist-link", "");
    String playlistID = getIdFromURL(url);
    PlaylistItemListResponse playlistItem = new PlaylistItemListResponse();
    VideoListResponse videoItem = new VideoListResponse();
    try{
       playlistItem = getPlaylistInfo(playlistID);
    } catch (Exception e) {
      e.printStackTrace();
    }
    response.setContentType("application/json");
    String playlistItemJson = new Gson().toJson(playlistItem);
    ArrayList<String> playlistVideos = parsePlaylistItem(playlistItemJson);
    String videoID = getRandomVideo(playlistVideos);
    try{
      videoItem = getVideoInfo(videoID);
   } catch (Exception e) {
     e.printStackTrace();
   }
    String videoItemJson = new Gson().toJson(videoItem);
    response.sendRedirect("/game.html");
  }

  public String getIdFromURL(String url){
     if (url.contains("youtube.com/playlist?list=")) {
      int start = url.lastIndexOf('=') + 1;
      int end = url.length(); 
      return url.substring(start, end);
      // handle more complicated URL's
    }else{
      System.err.println(url + " is not a valid YouTube Playlist URL");
      return "";
      //do something more complicated to handle this error
    }
  }

  private String getRandomVideo(ArrayList<String> playlistVideos){
     Random randomGenerator = new Random();
     int index = randomGenerator.nextInt(PLAYLIST_SIZE);
     String videoID = playlistVideos.get(index);
     System.out.println("Random video: " + videoID);
     return videoID;
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
     * Call function to create API service object.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static PlaylistItemListResponse getPlaylistInfo(String playlistID)
        throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
            .list(Arrays.asList("snippet")).setMaxResults(25L)
            .setPlaylistId(playlistID);
        // System.out.println(request);
        PlaylistItemListResponse response = request
            .execute();
        // System.out.println(response);    
        return response;
    }


    public static VideoListResponse getVideoInfo(String videoID)
        throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Videos.List request = youtubeService.videos()
            .list(Arrays.asList("snippet","contentDetails","statistics"));
        VideoListResponse response = request.setId(Arrays.asList("Ks-_Mh1QhMc")).execute();
        // System.out.println(response);
        return response;
    }

    private ArrayList<String> parsePlaylistItem(String playlistItemJson){
      String[] playlistItemData = playlistItemJson.split("\",\"");
      ArrayList<String> playlistVideos= new ArrayList<String>();
      for (String data : playlistItemData)
        if(data.startsWith("videoId\":\"")){
          int idStart = data.indexOf("\":\"") + 3;
          int idEnd = data.indexOf("\"", idStart);
          String videoID = data.substring(idStart, idEnd);
          playlistVideos.add(videoID);
          PLAYLIST_SIZE++;
        }
      return playlistVideos;
    }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }


}


