/*
 * Copyright (C) 2015 8tory, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rx.parse;

import rx.schedulers.*;
import rx.Observable;
import rx.functions.*;
import rx.observables.*;

import com.parse.*;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.bolts.TaskObservable;

import android.app.Activity;

public class ParseObservable {

    public static <R extends ParseObject> Observable<R> find(ParseQuery<R> query) {
        return Observable.defer(() -> TaskObservable.just(query.findInBackground()))
                .flatMap(l -> Observable.from(l))
            .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static <R extends ParseObject> Observable<Integer> count(ParseQuery<R> query) {
        return Observable.defer(() -> TaskObservable.just(query.countInBackground()))
            .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));

    }

    public static <R extends ParseObject> Observable<R> pin(R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.pinInBackground()))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> pin(List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.pinAllInBackground(objects)))
                .flatMap(v -> Observable.from(objects));
    }

    public static <R extends ParseObject> Observable<R> pin(String name, R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.pinInBackground(name)))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> pin(String name, List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.pinAllInBackground(name, objects)))
                .flatMap(v -> Observable.from(objects));
    }

    public static <R extends ParseObject> Observable<R> unpin(R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.unpinInBackground()))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> unpin(List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.unpinAllInBackground(objects)))
                .flatMap(v -> Observable.from(objects));
    }

    public static <R extends ParseObject> Observable<R> unpin(String name, R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.unpinInBackground(name)))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> unpin(String name, List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.unpinAllInBackground(name, objects)))
                .flatMap(v -> Observable.from(objects));
    }

    public static <R extends ParseObject> Observable<R> all(ParseQuery<R> query) {
        return count(query).flatMap(c -> all(query, c));
    }

    /** limit 10000 by skip */
    public static <R extends ParseObject> Observable<R> all(ParseQuery<R> query, int count) {
        final int limit = 1000; // limit limitation
        query.setSkip(0);
        query.setLimit(limit);
        Observable<R> find = find(query);
        for (int i = limit; i < count; i+= limit) {
            if (i >= 10000) break; // skip limitation
            query.setSkip(i);
            query.setLimit(limit);
            find.concatWith(find(query));
        }
        return find.distinct(o -> o.getObjectId());
    }

    public static <R extends ParseObject> Observable<R> first(ParseQuery<R> query) {
        return Observable.defer(() -> TaskObservable.just(query.getFirstInBackground()))
            .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static <R extends ParseObject> Observable<R> get(Class<R> clazz, String objectId) {
        ParseQuery<R> query = ParseQuery.getQuery(clazz);
        return Observable.defer(() -> TaskObservable.just(query.getInBackground(objectId)))
            .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static <R> Observable<R> callFunction(String name, Map<String, R> params) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseCloud.callFunctionInBackground(name, params)));
    }

    public static <R extends ParseObject> Observable<R> save(R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.saveInBackground()))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> save(List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.saveAllInBackground(objects)))
                .flatMap(v -> Observable.from(objects));
    }

    public static <R extends ParseObject> Observable<R> saveEventually(R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.saveEventually()))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> fetchIfNeeded(R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.fetchIfNeededInBackground()));
    }

    public static <R extends ParseObject> Observable<R> fetchIfNeeded(List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.fetchAllIfNeededInBackground(objects)))
                .flatMap(l -> Observable.from(l));
    }

    public static <R extends ParseObject> Observable<R> delete(R object) {
        return Observable.defer(() -> TaskObservable.justNullable(object.deleteInBackground()))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> delete(List<R> objects) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseObject.deleteAllInBackground(objects)))
                .flatMap(v -> Observable.from(objects));
    }

    public static Observable<String> subscribe(String channel) {
        android.util.Log.d("ParseObservable", "subscribe: channel: " + channel);

        return Observable.defer(() -> TaskObservable.justNullable(ParsePush.subscribeInBackground(channel)))
                .doOnNext(v -> android.util.Log.d("ParseObservable", "doOnNext: " + v))
                .map(v -> channel);
    }

    public static Observable<String> unsubscribe(String channel) {
        android.util.Log.d("ParseObservable", "unsubscribe, channel: " + channel);

        return Observable.defer(() -> TaskObservable.justNullable(ParsePush.unsubscribeInBackground(channel)))
                .map(v -> channel);
    }

    /* ParseFacebookUtils 1.8 */

    public static Observable<ParseUser> link(ParseUser user, Activity activity) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.linkInBackground(user, activity)))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Activity activity, int activityCode) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.linkInBackground(user, activity, activityCode)))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Collection<String> permissions, Activity activity) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.linkInBackground(user, permissions, activity)))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Collection<String> permissions, Activity activity, int activityCode) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.linkInBackground(user, permissions, activity, activityCode)))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, String facebookId, String accessToken, Date expirationDate) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.linkInBackground(user, facebookId, accessToken, expirationDate)))
                .map(v -> user);
    }

    public static Observable<ParseUser> logIn(Collection<String> permissions, Activity activity, int activityCode) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.logInInBackground(permissions, activity, activityCode)));
    }

    public static Observable<ParseUser> logIn(String facebookId, String accessToken, Date expirationDate) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.logInInBackground(facebookId, accessToken, expirationDate)));
    }

    public static Observable<ParseUser> saveLatestSessionData(ParseUser user) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.saveLatestSessionDataInBackground(user)))
                .map(v -> user);
    }

    public static Observable<ParseUser> unlink(ParseUser user) {
        return Observable.defer(() -> TaskObservable.justNullable(ParseFacebookUtils.unlinkInBackground(user)))
                .map(v -> user);
    }

    // TODO
    // ParsePush
    // send(JSONObject data, ParseQuery<ParseInstallation> query)
    // send()
    // sendMessage(String message)
    //
    // ParseObject
    // refresh()
    // fetchFromLocalDatastore()
    //
    // ParseUser
    // becomeInBackground()
    // enableRevocableSessionInBackground
    // logInInBackground(String username, String password)
    // logOutInBackground()
    // requestPasswordResetInBackground(String email)
    // signUpInBackground()
}
