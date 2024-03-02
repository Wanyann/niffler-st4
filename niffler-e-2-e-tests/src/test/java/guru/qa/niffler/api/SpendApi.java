package guru.qa.niffler.api;

import guru.qa.niffler.model.SpendJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface SpendApi {

  @POST("/addSpend")
  Call<SpendJson> addSpend(@Body SpendJson spend);

  @DELETE("/deleteSpends")
  Call<ResponseBody> deleteSpends(@Query("username") String username,
                                  @Query("ids") List<String> ids);
}
