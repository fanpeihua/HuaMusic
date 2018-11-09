package fanpeihua.huamusic.base;

import android.content.ServiceConnection;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import fanpeihua.huamusic.R;
import fanpeihua.huamusic.di.component.ActivityComponent;

public abstract class BaseActivity<T extends BaseContract.BasePresenter> extends RxAppCompatActivity
        implements ServiceConnection, BaseContract.BaseView {

    @Nullable
    @Inject
    protected T mPresenter;
    protected ActivityComponent mActivityComponent;

    @Nullable
    @BindView(R.id.error_button_retry)
    public View emptyButtonRetry;

    @Nullable
    @BindView(R.id.error_message_view)
    public View errorTextView;

    @Nullable
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    // empty state view
    // error panel
    // loading progress bar
    // Toolbar

    protected Handler mHandler;
    private Unbinder unbinder;
//    private PlayerManager
}
