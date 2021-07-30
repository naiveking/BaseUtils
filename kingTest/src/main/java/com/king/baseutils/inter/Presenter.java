package com.king.baseutils.inter;

/**
 * Created by King on 2017/11/3.
 */

public interface Presenter<T> {

    void attachView(T view);

    void deathView();
}
