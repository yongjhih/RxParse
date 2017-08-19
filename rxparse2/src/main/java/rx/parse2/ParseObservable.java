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

package rx.parse2;

import android.content.Intent;

import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseConfig;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import rx.bolts2.TaskObservable;

public class ParseObservable {

    public static <R extends ParseObject> Observable<R> find(ParseQuery<R> query) {
        return TaskObservable.defer(() -> query.findInBackground())
                .flatMap(l -> Observable.fromIterable(l))
            .doOnDispose(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static <R extends ParseObject> Observable<Integer> count(ParseQuery<R> query) {
        return TaskObservable.defer(() -> query.countInBackground())
            .doOnDispose(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));

    }

    public static <R extends ParseObject> Observable<R> pin(R object) {
        return TaskObservable.defer(() -> object.pinInBackground().continueWith(v -> object))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> pin(List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.pinAllInBackground(objects).continueWith(v -> objects))
                .flatMap(v -> Observable.fromIterable(objects));
    }

    public static <R extends ParseObject> Observable<R> pin(String name, R object) {
        return TaskObservable.defer(() -> object.pinInBackground(name))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> pin(String name, List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.pinAllInBackground(name, objects))
                .flatMap(v -> Observable.fromIterable(objects));
    }

    public static <R extends ParseObject> Observable<R> unpin(R object) {
        return TaskObservable.defer(() -> object.unpinInBackground())
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> unpin(List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.unpinAllInBackground(objects))
                .flatMap(v -> Observable.fromIterable(objects));
    }

    public static <R extends ParseObject> Observable<R> unpin(String name, R object) {
        return TaskObservable.defer(() -> object.unpinInBackground(name))
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> unpin(String name, List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.unpinAllInBackground(name, objects))
                .flatMap(v -> Observable.fromIterable(objects));
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
        return TaskObservable.defer(() -> query.getFirstInBackground())
            .doOnDispose(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static <R extends ParseObject> Observable<R> get(Class<R> clazz, String objectId) {
        return get(ParseQuery.getQuery(clazz), objectId);
    }

    public static <R extends ParseObject> Observable<R> get(ParseQuery<R> query, String objectId) {
        return TaskObservable.defer(() -> query.getInBackground(objectId))
            .doOnDispose(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    // Task<T> nullable?
    public static <R> Observable<R> callFunction(String name, Map<String, ?> params) {
        return TaskObservable.defer(() -> ParseCloud.callFunctionInBackground(name, params));
    }

    public static <R extends ParseObject> Observable<R> save(R object) {
        return TaskObservable.defer(() -> object.saveInBackground())
                .map(v -> object);
    }

    public static <R extends ParseObject> Observable<R> save(List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.saveAllInBackground(objects))
                .flatMap(v -> Observable.fromIterable(objects));
    }

    public static <R extends ParseObject> Observable<R> saveEventually(R object) {
        return TaskObservable.defer(() -> object.saveEventually())
                .map(v -> object);
    }

    // Task<T> nullable?
    public static <R extends ParseObject> Observable<R> fetch(R object) {
        return TaskObservable.defer(() -> object.fetchInBackground())
                .map(v -> object);
    }

    // Task<List<T>> nullable?
    public static <R extends ParseObject> Observable<R> fetch(List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.fetchAllInBackground(objects))
                .flatMap(l -> Observable.fromIterable(l)); // v -> Observable.fromIterable(objects)
    }

    // Task<T> nullable?
    public static <R extends ParseObject> Observable<R> fetchIfNeeded(R object) {
        return TaskObservable.defer(() -> object.fetchIfNeededInBackground())
                .map(v -> object);
    }

    // Task<List<T>> nullable?
    public static <R extends ParseObject> Observable<R> fetchIfNeeded(List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.fetchAllIfNeededInBackground(objects))
                .flatMap(l -> Observable.fromIterable(l)); // v -> Observable.fromIterable(objects)
    }

    // Task<T> nullable?
    public static <R extends ParseObject> Observable<R> delete(R object) {
        return TaskObservable.defer(() -> object.deleteInBackground())
                .map(v -> object);
    }

    // Task<List<T>> nullable?
    public static <R extends ParseObject> Observable<R> delete(List<R> objects) {
        return TaskObservable.defer(() -> ParseObject.deleteAllInBackground(objects))
                .flatMap(v -> Observable.fromIterable(objects));
    }

    /* ParsePush */

    public static Observable<String> subscribe(String channel) {
        android.util.Log.d("ParseObservable", "subscribe: channel: " + channel);

        return TaskObservable.defer(() -> ParsePush.subscribeInBackground(channel))
                .doOnNext(v -> android.util.Log.d("ParseObservable", "doOnNext: " + v))
                .map(v -> channel);
    }

    public static Observable<String> unsubscribe(String channel) {
        android.util.Log.d("ParseObservable", "unsubscribe, channel: " + channel);

        return TaskObservable.defer(() -> ParsePush.unsubscribeInBackground(channel))
                .map(v -> channel);
    }

    public static Observable<ParsePush> send(ParsePush push) {
        return TaskObservable.defer(() -> push.sendInBackground()).map(v -> push);
    }

    public static Observable<JSONObject> send(JSONObject data, ParseQuery<ParseInstallation> query) {
        return TaskObservable.defer(() -> ParsePush.sendDataInBackground(data, query))
                .map(v -> data);
    }

    public static Observable<String> send(String message, ParseQuery<ParseInstallation> query) {
        return TaskObservable.defer(() -> ParsePush.sendMessageInBackground(message, query))
                .map(v -> message);
    }

    /* ParseObject */

    // TODO refresh()
    // TODO fetchFromLocalDatastore()

    /* ParseUser */

    public static Observable<ParseUser> become(String sessionToken) {
        return TaskObservable.defer(() -> ParseUser.becomeInBackground(sessionToken));
    }

    // TODO enableRevocableSessionInBackground

    public static Observable<ParseUser> logIn(String username, String password) {
        return TaskObservable.defer(() -> ParseUser.logInInBackground(username, password));
    }

    public static Observable<Void> logOut() {
        return TaskObservable.defer(() -> ParseUser.logOutInBackground());
    }

    public static Observable<ParseUser> anonymousLogIn() {
        return TaskObservable.defer(() -> ParseAnonymousUtils.logInInBackground());
    }

    /*
    ParseObservable.Anonymous.logIn()
    public static class Anonymous {
        public static Observable<ParseUser> logIn() {
            return TaskObservable.defer(() -> ParseAnonymousUtils.logInInBackground());
        }
    }
    */

    // TODO linkWithInBackground(String authType, Map<String,String> authData)
    // TODO unlinkFromInBackground(String authType)

    public static Observable<String> resetPassword(String email) {
        return TaskObservable.defer(() -> ParseUser.requestPasswordResetInBackground(email)).map(v -> email);
    }

    public static Observable<ParseUser> signUp(ParseUser user) {
        return TaskObservable.defer(() -> user.signUpInBackground()).map(v -> user);
    }

    // ParseAnalytics

    public static Observable<Intent> trackAppOpened(Intent intent) {
        return TaskObservable.defer(() -> ParseAnalytics.trackAppOpenedInBackground(intent)).map(v -> intent);
    }

    public static Observable<String> trackEvent(String name) {
        return TaskObservable.defer(() -> ParseAnalytics.trackEventInBackground(name)).map(v -> name);
    }

    public static Observable<String> trackEvent(String name, Map<String,String> dimensions) {
        return TaskObservable.defer(() -> ParseAnalytics.trackEventInBackground(name, dimensions)).map(v -> name);
    }

    /* ParseFile */

    public static Observable<byte[]> getData(ParseFile file) {
        return TaskObservable.defer(() -> file.getDataInBackground());
    }

    public static Observable<byte[]> getData(ParseFile file, ProgressCallback progressCallback) {
        return TaskObservable.defer(() -> file.getDataInBackground(progressCallback));
    }

    public static Observable<InputStream> getDataStream(ParseFile file) {
        return TaskObservable.defer(() -> file.getDataStreamInBackground());
    }

    public static Observable<InputStream> getDataStream(ParseFile file, ProgressCallback progressCallback) {
        return TaskObservable.defer(() -> file.getDataStreamInBackground(progressCallback));
    }

    public static Observable<File> getFile(ParseFile file) {
        return TaskObservable.defer(() -> file.getFileInBackground());
    }

    public static Observable<File> getFile(ParseFile file, ProgressCallback progressCallback) {
        return TaskObservable.defer(() -> file.getFileInBackground(progressCallback));
    }

    public static Observable<ParseFile> save(ParseFile file) {
        return TaskObservable.defer(() -> file.saveInBackground()).map(v -> file);
    }

    public static Observable<ParseFile> save(ParseFile file, ProgressCallback uploadProgressCallback) {
        return TaskObservable.defer(() -> file.saveInBackground(uploadProgressCallback)).map(v -> file);
    }

    public static Observable<ParseConfig> getConfig() {
        return TaskObservable.defer(() -> ParseConfig.getInBackground())
                .map(c -> ParseConfig.getCurrentConfig());
    }

    /*
    private ParseObservable() {
        throw new UnsupportedOperationException();
    }
    */

}
