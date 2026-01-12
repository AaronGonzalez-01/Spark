package Obligatorio2;
import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }
}

