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
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

@WebServlet("/chat")
public final class ChatServlet extends HttpServlet {

<<<<<<< HEAD
  private static final String APP_ID = "1024158";
  private static final String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private static final String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private static final String PUSHER_APPLICATION_NAME = "song-guessing-game";
  private static final String PUSHER_CHAT_CHANNEL_NAME = "chat-update";
=======
  private final static String APP_ID = "1024158";
  private final static String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private final static String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private final static String PUSHER_APPLICATION_NAME = "song-guessing-game";
  private final static String PUSHER_CHAT_CHANNEL_NAME = "chat-update";
  private final static Type MESSAGE_TYPE = new TypeToken<Map<String, String>>(){}.getType();
>>>>>>> master
  private Pusher pusher;
  private Gson gson;

  // TODO: @salilnadkarni remove temp variables and integrate datastore
  Map<String, Boolean> status  = new HashMap<String,Boolean>();
  String ANSWER = "Google";

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

<<<<<<< HEAD
  private void sendResponseToClient(HttpServletResponse response, String message)
      throws IOException {
=======
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
    return message.equals(ANSWER);
  }

  private void sendResponseToClient(HttpServletResponse response, String message) throws IOException {
>>>>>>> master
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }
}
