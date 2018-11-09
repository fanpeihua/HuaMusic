package fanpeihua.huamusic.base;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class BasePresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;
    protected List<Disposable> disposables;

    @Override
    public void attachToView(T view) {
        this.mView = view;
        disposables = new ArrayList<>();
    }

    @Override
    public void detachToView() {
        if (mView != null) {
            mView = null;
        }
        for (Disposable dis : disposables) {
            dis.dispose();
        }
        disposables.clear();
    }
}
