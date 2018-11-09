package fanpeihua.huamusic.net;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fanpeihua.huamusic.MusicApp;
import fanpeihua.huamusic.R;
import fanpeihua.huamusic.api.gson.MyGsonConverterFactory;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ApiManager {
    public ApiManagerService apiService;
    private static ApiManager sApiManager;

    private static long CONNECT_TIMEOUT = 60L;
    private static long READ_TIMEOUT = 10L;
    private static long WRITE_TIMEOUT = 10L;

    // 设缓存
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    // 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-sale=" + CACHE_STALE_SEC;
    // 查询网络的Cache-Control设置
    // 假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回
    public static final String CACHE_CONTROL_NETWORK = "Cache-Control: public, max-age=10";

    // 避免出现 HTTP 403 Forbidden，参考：http://stackoverflow.com/questions/13670692/403-forbidden-with-java-but-not-web-browser
    private static final String AVOID_HTTP403_FORBIDDEN = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static volatile OkHttpClient mOkHttpClient;

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private static final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                // 统一进行配置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" +
                                CACHE_CONTROL_CACHE)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    private static final Interceptor mLogginInterceptor = chain -> {
        Request request = chain.request();
        Response response = chain.proceed(request);
        return response;
    };

    private static OkHttpClient getmOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (ApiManager.class) {
                Cache cache = new Cache(new File(MusicApp.getAppContext().getCacheDir(), "HttpCache"),
                        1024 * 1024 * 1000);
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    public static ApiManager getInstance() {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                if (sApiManager == null) {
                    sApiManager = new ApiManager();
                }
            }
        }
        return sApiManager;
    }

    public ApiManager() {
    }

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public <T> T create(Class<T> clazz, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(getmOkHttpClient())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    public static <T> void request(Observable<T> service, RequestCallBack<T> result) {
        service.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T t) {
                        if (result != null) {
                            result.success(t);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            try {
                                String string = ((HttpException) e).response().errorBody().string();
                                if (string != null) {
                                    JSONObject jsonObject = new JSONObject(string);
                                    String error = jsonObject.getString("msg");
                                    result.error(error);
                                } else {
                                    result.error("未知错误");
                                }
                            } catch (IOException | JSONException exception) {
                                exception.printStackTrace();
                            }
                        } else {
                            if (result != null) {
                                if (e.getMessage() == null) {
                                    result.error(MusicApp.getAppContext().getString(R.string.error_connection));
                                } else {
                                    result.error(e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
