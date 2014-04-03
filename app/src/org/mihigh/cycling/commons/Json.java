package org.mihigh.cycling.commons;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;

public class Json {

  static ObjectMapper mapper = new ObjectMapper();


  static {
    mapper.setVisibilityChecker(mapper.getSerializationConfig()
                                    .getDefaultVisibilityChecker()
                                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
  }

  public static <T> T parseJson(String json, Class<T> type) {
    try {
      return mapper.readValue(json, type);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String toJson(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}
