package fanpeihua.huamusic.base;

import android.content.Context;

import com.trello.rxlifecycle2.LifecycleTransformer;

public class BaseContract {

    public interface BasePresenter<T extends BaseContract.BaseView> {
        void attachToView(T view);

        void detachToView();
    }

    public interface BaseView {
        Context getContext();

        // 显示进度中
        void showLoading();

        // 隐藏进度
        void hideLoading();

        // 隐藏进度
        void showError(String message, boolean showReryButton);

        // 显示空状态
        void showEmptyState();

        /**
         * 绑定生命周期
         *
         * @param <T>
         * @return
         */
        <T> LifecycleTransformer<T> bindToLife();
    }
}
