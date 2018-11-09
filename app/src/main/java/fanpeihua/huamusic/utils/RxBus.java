package fanpeihua.huamusic.utils;

import android.annotation.NonNull;

import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

public class RxBus {
    private static volatile RxBus sRxBus;
    private final FlowableProcessor<Object> mBus;

    public RxBus() {
        mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus();
                }
            }
        }
        return sRxBus;
    }

    public void post(Object obj) {
        mBus.onNext(obj);
    }


}
