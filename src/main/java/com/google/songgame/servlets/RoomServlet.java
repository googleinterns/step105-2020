package com.google.songgame.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hc.core5.http.ParseException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

@WebServlet("/room")
public final class RoomServlet extends HttpServlet {

  private final static Type MESSAGE_TYPE = new TypeToken<Map<String, String>>(){}.getType();
  private Gson gson;

  @Override
  public void init() {
    gson = new Gson();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> userProperties = readJSONFromRequest(request);

    // Save player username and userId to datastore.
    Entity userEntity = new Entity("User");
    userEntity.setProperty("username", userProperties.get("username"));
    userEntity.setProperty("userId", userProperties.get("userId"));
    
    datastore.put(userEntity);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);

    List<String> usernames = new ArrayList<String>();
    for (Entity entity : results.asIterable()) {
      String username = (String) entity.getProperty("username");
      usernames.add(username);
    }
    
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(usernames));
  }

  private Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    return jsonData;
  }

}
