package com.google.songgame.servlets;

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> b95bf11dda91c0fa5796a4a4d7690af9d517660e
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
<<<<<<< HEAD
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
=======
<<<<<<< HEAD
=======
>>>>>>> master
=======
>>>>>>> b95bf11dda91c0fa5796a4a4d7690af9d517660e
>>>>>>> store-user
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.Collections;
<<<<<<< HEAD

<<<<<<< HEAD
=======
>>>>>>> b95bf11dda91c0fa5796a4a4d7690af9d517660e
import org.apache.hc.core5.http.ParseException;
import java.util.*;

<<<<<<< HEAD
=======
>>>>>>> master
=======
>>>>>>> b95bf11dda91c0fa5796a4a4d7690af9d517660e
@WebServlet("/room")
public final class RoomServlet extends HttpServlet {

  private Gson gson;
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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
    
    datastore.put(userEntity);

    response.sendRedirect("/lobby.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);

    List<String> users = new ArrayList<String>();
    for (Entity entity : results.asIterable()) {
      String username = (String) entity.getProperty("username");
      users.add(username);
    }
    
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(users));
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
