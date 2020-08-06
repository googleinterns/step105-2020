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
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
<<<<<<< HEAD
import com.google.songgame.data.JSONRequestReader;
import com.google.songgame.data.UserCookieReader;
=======
import com.google.songgame.data.GuessChecker;
>>>>>>> master

@WebServlet("/chat")
public final class ChatServlet extends HttpServlet {

  private static final String APP_ID = "1024158";
  private static final String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private static final String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private static final String PUSHER_APPLICATION_NAME = "song-guessing-game";
<<<<<<< HEAD
  private static final String PUSHER_CHAT_CHANNEL_NAME = "chat-update";
=======
  private static final String PUSHER_CHAT_CHANNEL_NAME_BASE = "chat-update-";
  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();
>>>>>>> master
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
    Map<String, String> dataFromChatClient = JSONRequestReader.readJSONFromRequest(request);

    String userId = UserCookieReader.getUserId(request);
    dataFromChatClient.put("userId", userId);

    Map<String, String> responseForPusherChat = createPusherChatResponse(dataFromChatClient);
    String roomId = dataFromChatClient.get("roomId");
    pusher.trigger(
        PUSHER_APPLICATION_NAME, PUSHER_CHAT_CHANNEL_NAME_BASE + roomId, responseForPusherChat);
    sendResponseToClient(response, "complete");
    return;
  }

  // TODO: @salilnadkarni modify when points stored in rooms
  private Map<String, String> createPusherChatResponse(Map<String, String> data) {
    Map<String, String> response = new HashMap<String, String>();

    String userId = data.get("userId");

    Entity currentGame = getCurrentGame();
    Entity currentUser = getUser(userId);

    String username = (String) currentUser.getProperty("username");
    String message = data.get("message");
    String messageType = "guess";

    if (GuessChecker.hasUserPreviouslyGuessedCorrect(currentUser, currentGame)) {
      messageType = "spectator";
    } else if (GuessChecker.isCorrectGuess(message, currentGame)) {
      currentGame = GuessChecker.markUserGuessedCorrectly(currentUser, currentGame);
      currentGame = GuessChecker.assignUserPoints(currentUser, currentGame);
      datastore.put(currentGame);
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

  private String getUsername(String userId) {
    Filter userIdFilter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query userQuery = new Query("User").setFilter(userIdFilter);
    PreparedQuery result = datastore.prepare(userQuery);
    Entity currentUser = result.asSingleEntity();
    return currentUser;
  }

  private void sendResponseToClient(HttpServletResponse response, String message)
      throws IOException {
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }
}
