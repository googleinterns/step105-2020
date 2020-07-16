package com.google.songgame.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.Collections;
import org.apache.hc.core5.http.ParseException;
import java.util.*;

@WebServlet("/room")
public final class RoomServlet extends HttpServlet {

  private Gson gson;

  @Override
  public void init() {
    gson = new Gson();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> userJson = readJSONFromRequest(request);

    // Save player username and userId to datastore.
    Entity userEntity = new Entity("User");
    userEntity.setProperty("username", getValuesList(userJson).get(0));
    userEntity.setProperty("userId", getValuesList(userJson).get(1));
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userEntity);

    response.sendRedirect("/lobby.html");
  }

  private Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map jsonData = gson.fromJson(requestJSONString, Map.class);
    return jsonData;
  }

  private List<String> getValuesList(Map <String, String> userJson) throws IOException {
    List<String> values =  new ArrayList<String>();

    for (String key : userJson.keySet()) {
      values.add(userJson.get(key));
    }
    return values;
  }
}
