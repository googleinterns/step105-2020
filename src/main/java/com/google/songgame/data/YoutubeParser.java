package com.google.songgame.data;

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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
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
import java.lang.IllegalArgumentException;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.PlaylistItemSnippet;

public final class YoutubeParser {

  private static final String DEVELOPER_KEY = "AIzaSyBZw4Z25Lect7ux9z960RCM7YORcYo6slc";
  private static final String APPLICATION_NAME = "Song Guessing Game";
  private static final JsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final long MAX_RESULTS = 25L;

  /**
   * Returns a list of YouTube Video IDs of the music videos in a given playlist url
   *
   * <p>Utilizes YouTube Data v3 API to list the first MAX_RESULTS video's IDs in a given playlist
   */
  public ArrayList<String> getPlaylistVideoIds(String playlistUrl) {
    String playlistId = getPlaylistIdFromUrl(playlistUrl);
    PlaylistItemListResponse playlistItem = new PlaylistItemListResponse();
    // Retrieve Playlist item from Youtube API
    try {
      playlistItem = getPlaylistInfo(playlistId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ArrayList<String> playlistVideoIds = getVideoIdsFromPlaylistItem(playlistItem);
    return playlistVideoIds;
  }

  /** Returns playlist ID */
  public String getPlaylistIdFromUrl(String playlistUrl) {
    if (playlistUrl.contains("youtube.com/playlist?list=")) {
      int start = playlistUrl.indexOf("list=") + 5;
      int end = playlistUrl.length();
      if (playlistUrl.contains("&")) {
        end = playlistUrl.indexOf("&", start);
      }
      return playlistUrl.substring(start, end);
    } else {
      throw new IllegalArgumentException(playlistUrl + " is not a valid YouTube Playlist URL.");
    }
  }

  /**
   * Call function to create API service object.
   *
   * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
   */
  private static PlaylistItemListResponse getPlaylistInfo(String playlistId)
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

  /** Returns an ArrayList of video IDs */
  private ArrayList<String> getVideoIdsFromPlaylistItem(PlaylistItemListResponse playlistItem) {
    List<PlaylistItem> videoItems = playlistItem.getItems();
    ArrayList<String> playlistVideos = new ArrayList<String>();

    for (PlaylistItem item : videoItems) {
      PlaylistItemSnippet snippet = item.getSnippet();
      ResourceId resourceId = snippet.getResourceId();
      String videoId = resourceId.getVideoId();
      playlistVideos.add(videoId);
    }
    return playlistVideos;
  }

  /** Returns a Video object from the Youtube Data API */
  public Video getVideoFromVideoId(String videoId) {
    Video currentVideo = null;
    try {
      currentVideo = getVideo(videoId);
    } catch (Exception e) {
      System.err.println("ERROR: Could not read video");
    }
    return currentVideo;
  }

  /** Retrieves video information given a particular video ID */
  private Video getVideo(String videoId)
      throws GeneralSecurityException, IOException, GoogleJsonResponseException {
    YouTube youtubeService = getService();
    YouTube.Videos.List request = youtubeService.videos().list("snippet");
    VideoListResponse response = request.setId(videoId).execute();
    Video video = response.getItems().get(0);
    return video;
  }

  /**
   * Build and return an authorized API client service.
   *
   * <p>Returns an authorized API client service
   *
   * @throws GeneralSecurityException, IOException
   */
  private static YouTube getService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new YouTube.Builder(httpTransport, GSON_FACTORY, null)
        .setApplicationName(APPLICATION_NAME)
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(DEVELOPER_KEY))
        .build();
  }
}
