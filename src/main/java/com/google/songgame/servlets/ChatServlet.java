package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import javax.servlet.http.Cookie;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@WebServlet("/chat")
public final class ChatServlet extends HttpServlet {

  private final static String APP_ID = "1024158";
  private final static String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private final static String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private final static String PUSHER_APPLICATION_NAME = "song-guessing-game";
  private final static String PUSHER_CHAT_CHANNEL_NAME = "chat-update";
  private final static Type MESSAGE_TYPE = new TypeToken<Map<String, String>>(){}.getType();
  private Pusher pusher;
  private Gson gson;
  private DatastoreService datastore;

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
    Map<String, String> dataFromChatClient = readJSONFromRequest(request);
    Map<String, String> responseForPusherChat = createPusherChatResponse(dataFromChatClient);
    pusher.trigger(PUSHER_APPLICATION_NAME, PUSHER_CHAT_CHANNEL_NAME, responseForPusherChat);
    sendResponseToClient(response, "complete");
    return;
  }

  private Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    String userId = getUserId(request);
    jsonData.put("userId", userId);
    return jsonData;
  }

  //TODO: @salilnadkarni modify when points stored in rooms
  private Map<String, String> createPusherChatResponse(Map<String, String> data) {
    Map<String, String> response = new HashMap<String, String>();

    Entity currentRound = getCurrentRound();

    String userId = data.get("userId");
    String username = getUsername(userId);
    String message = data.get("message");
    String messageType = "guess";

    if (checkStatus(userId, currentRound)) {
      messageType = "spectator";
    } else if (checkGuess(message)) {
      updateStatusAndPoints(userId, currentRound);
      messageType = "correct";
      message = "guessed correctly!";
    }
    response.put("username", username);
    response.put("message", message);
    response.put("messageType", messageType);
    return response;
  }

  private Entity getCurrentRound() {
    // TODO: @salilnadkarni add more robust way of finding current round
    Query roundQuery = new Query("Round").addSort("startTime", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(roundQuery);
    
    Entity currentRound = result.asList(FetchOptions.Builder.withLimit(1)).get(0);
    return currentRound;
  }

  private String getUserId(HttpServletRequest request) throws IOException {
    Cookie[] cookies = request.getCookies();
    String userId = "";
    for (Cookie cookie : cookies) {
      String name = cookie.getName();
      if (name.equals("userId")) {
        userId = cookie.getValue();
      }
    }

    if (userId.equals("")) {
      System.err.println("ERROR: UserID cookie could not be found.");
    }

    return userId;
  }

  private String getUsername(String userId) {
    Filter userIdFilter =
        new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query userQuery = new Query("User").setFilter(userIdFilter);
    PreparedQuery result = datastore.prepare(userQuery);
    Entity currentUser = result.asSingleEntity();
    return (String) currentUser.getProperty("username");
  }

  private boolean checkStatus(String userId, Entity currentRound) {
    EmbeddedEntity userStatuses = (EmbeddedEntity) currentRound.getProperty("userStatuses");
    boolean userStatus = (Boolean) userStatuses.getProperty(userId);
    return userStatus == true;
  }

  private void updateStatusAndPoints(String userId, Entity currentRound) {
    EmbeddedEntity userStatuses = (EmbeddedEntity) currentRound.getProperty("userStatuses");
    userStatuses.setProperty(userId, true);

    EmbeddedEntity userPoints = (EmbeddedEntity) currentRound.getProperty("userPoints");
    long currentUserPoints = (Long) userPoints.getProperty(userId);
    userPoints.setProperty(userId, currentUserPoints + 100);

    datastore.put(currentRound);
  }

  private boolean checkGuess(String message) {
    Query videoQuery = new Query("Video").addSort("fetchTime", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(videoQuery);
    
    Entity currentVideo = result.asList(FetchOptions.Builder.withLimit(1)).get(0);
    String videoTitle = (String) currentVideo.getProperty("title");
    return message.equals(videoTitle);
  }

  private void sendResponseToClient(HttpServletResponse response, String message) throws IOException {
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }

}
