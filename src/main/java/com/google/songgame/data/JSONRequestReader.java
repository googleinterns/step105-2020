import javax.servlet.http.HttpServletRequest;
import java.util.Collectors;
import com.google.gson.Gson;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public final class JSONRequestReader {

  private final static Type MESSAGE_TYPE = new TypeToken<Map<String, String>>(){}.getType();
  private final static gson = new Gson();

  public Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    return jsonData;
  }
}