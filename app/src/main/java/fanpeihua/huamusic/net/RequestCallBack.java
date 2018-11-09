package fanpeihua.huamusic.net;

/**
 *
 * 请求成功回调类
 *
 * @param <T>
 */
public interface RequestCallBack<T> {
    void success(T result);

    void error(String msg);
}
