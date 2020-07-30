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

  private static final String APP_ID = "1024158";
  private static final String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private static final String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private static final String PUSHER_APPLICATION_NAME = "song-guessing-game";
  private static final String PUSHER_CHAT_CHANNEL_NAME = "chat-update";
  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();
  private static final long POINTS_PER_ROUND = 100;
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

  // TODO: @salilnadkarni modify when points stored in rooms
  private Map<String, String> createPusherChatResponse(Map<String, String> data) {
    Map<String, String> response = new HashMap<String, String>();

    Entity currentGame = getCurrentGame();
    EmbeddedEntity currentRound = (EmbeddedEntity) currentGame.getProperty("currentRound");

    String userId = data.get("userId");
    String username = getUsername(userId);
    String message = data.get("message");
    String messageType = "guess";

    if (checkIfUserPreviouslyGuessedCorrect(userId, currentRound)) {
      messageType = "spectator";
    } else if (isCorrectGuess(message, currentRound)) {
      markUserGuessedCorrectlyAndAddPoints(userId, currentRound, currentGame);
      messageType = "correct";
      message = "guessed correctly!";
    }
    response.put("username", username);
    response.put("message", message);
    response.put("messageType", messageType);
    return response;
  }

  private Entity getCurrentGame() {
    // TODO: @salilnadkarni add more robust way of finding current round
    Query gameQuery = new Query("Game").addSort("creationTime", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(gameQuery);

    Entity currentGame = result.asList(FetchOptions.Builder.withLimit(1)).get(0);
    return currentGame;
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
    Filter userIdFilter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query userQuery = new Query("User").setFilter(userIdFilter);
    PreparedQuery result = datastore.prepare(userQuery);
    Entity currentUser = result.asSingleEntity();
    return (String) currentUser.getProperty("username");
  }

  private boolean checkIfUserPreviouslyGuessedCorrect(String userId, EmbeddedEntity currentRound) {
    EmbeddedEntity userGuessStatuses =
        (EmbeddedEntity) currentRound.getProperty("userGuessStatuses");
    boolean userGuessStatus = (Boolean) userGuessStatuses.getProperty(userId);
    return userGuessStatus;
  }

  private void markUserGuessedCorrectlyAndAddPoints(
      String userId, EmbeddedEntity currentRound, Entity currentGame) {
    // Changes the users "guess status" to show they've guessed correctly
    EmbeddedEntity userGuessStatuses =
        (EmbeddedEntity) currentRound.getProperty("userGuessStatuses");
    userGuessStatuses.setProperty(userId, true);

    // Increases the users points by a fixed amount
    EmbeddedEntity userPoints = (EmbeddedEntity) currentGame.getProperty("userPoints");
    long currentUserPoints = (Long) userPoints.getProperty(userId);
    long updatedUserPoints = currentUserPoints + POINTS_PER_ROUND;
    userPoints.setProperty(userId, updatedUserPoints);

    // Update both changes in Datastore
    currentGame.setProperty("currentRound", currentRound);
    currentGame.setProperty("userPoints", userPoints);
    datastore.put(currentGame);
  }

  private boolean isCorrectGuess(String message, EmbeddedEntity currentRound) {
    EmbeddedEntity currentVideo = (EmbeddedEntity) currentRound.getProperty("video");
    String videoTitle = (String) currentVideo.getProperty("title");
    String guess = message.toLowerCase();
    return guess.equals(videoTitle);
  }

  private void sendResponseToClient(HttpServletResponse response, String message)
      throws IOException {
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }
}
