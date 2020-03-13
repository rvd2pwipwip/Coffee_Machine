package com.amazon.android.recipe;

import android.os.Bundle;

public class NoOpRecipeCallbacks implements IRecipeCookerCallbacks{

    @Override
    public void onPreRecipeCook(Recipe recipe, Object output, Bundle bundle) {

    }

    @Override
    public void onRecipeCooked(Recipe recipe, Object output, Bundle bundle, boolean
            done) {
//
//                        if (!subscriber.isUnsubscribed()) {
//                            subscriber.onNext(output);
//                            if (done) {
//                                subscriber.onCompleted();
//                            }
//                        }
    }

    @Override
    public void onPostRecipeCooked(Recipe recipe, Object output, Bundle bundle) {

    }

    @Override
    public void onRecipeError(Recipe recipe, Exception e, String msg) {

//                        if (e instanceof DynamicParser.ValueNotFoundException) {
//
//                        }
    }
}
