package com.king.baseutils.base;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.king.baseutils.http.ApiClient;
import com.king.baseutils.inter.Presenter;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Retrofit + Presenter 封装Base
 *
 * @author Gwall
 * @date 2020/1/6
 */
public abstract class BasePresenter<T> implements Presenter<T> {

    public T mView;

    public BasePresenter(T mView) {
        this.mView = mView;
    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }


    @Override
    public void deathView() {
        this.mView = null;
        onUnsubscribe();
    }

    protected Retrofit rf = ApiClient.getHttp();
    private CompositeSubscription mCompositeSubscription;

    protected void addSubscription(Observable<Object> observable, Subscriber<Object> subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showLong("网络不可用,请检查网络~");
            return;
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(mainThread())
                .subscribe(subscriber));
    }


    private void onUnsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
