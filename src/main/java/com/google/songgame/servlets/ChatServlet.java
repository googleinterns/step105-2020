package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
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
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

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

  // TODO: @salilnadkarni remove temp variables and integrate datastore
  Map<String,Boolean> status  = new HashMap<String,Boolean>();

  @Override
  public void init() {
    pusher = new Pusher(APP_ID, CLIENT_KEY, CLIENT_SECRET);
    pusher.setCluster("us2");
    pusher.setEncrypted(true);
    gson = new Gson();
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
    return jsonData;
  }

  private Map<String, String> createPusherChatResponse(Map<String, String> data) {
    Map<String, String> response = new HashMap<String, String>();
    String userId = data.get("userId");
    String message = data.get("message");
    String messageType = "guess";

    if (checkStatus(userId)) {
      messageType = "spectator";
    } else if (checkGuess(message)) {
      updateStatus(userId);
      messageType = "correct";
      message = "guessed correctly!";
    }
    response.put("username", userId);
    response.put("message", message);
    response.put("messageType", messageType);
    return response;
  }

  // TODO: @salilnadkarni update checkStatus / updateStatus to use Datastore
  private boolean checkStatus(String userId) {
    if (!status.containsKey(userId)) {
      status.put(userId, false);
    }
    return status.get(userId) == true;
  }

  private void updateStatus(String userId) {
    status.replace(userId, true);
  }

  // TODO: @salilnadkarni update checkGuess to use current video title
  private boolean checkGuess(String message) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query videoQuery = new Query("Video").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery result = datastore.prepare(videoQuery);
    
    Entity currentVideo = result.asList(FetchOptions.Builder.withLimit(10)).get(0);
    String videoTitle = (String) currentVideo.getProperty("title");
    System.out.println(videoTitle);
    return message.equals(videoTitle);
  }

  private void sendResponseToClient(HttpServletResponse response, String message) throws IOException {
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }
}
