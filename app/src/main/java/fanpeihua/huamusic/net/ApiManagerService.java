package fanpeihua.huamusic.net;


import java.util.List;
import java.util.Map;

import fanpeihua.huamusic.api.ApiModel;
import fanpeihua.huamusic.ui.my.user.User;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiManagerService {
    @POST
    Observable<ApiModel<User>> getUserInfo(@Url String baseUrl, @QueryMap Map<String,String> params);

    @GET
    Observable<ApiModel<List<Location>>>
}
