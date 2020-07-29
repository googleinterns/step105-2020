package com.google.songgame.data;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public final class JSONRequestReader {

  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();
  private static final Gson gson = new Gson();

  public Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    return jsonData;
  }
}
