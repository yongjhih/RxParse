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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import rx.bolts2.RxTask;

public class ParseObservable {

    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Observable<R> find(@NonNull final ParseQuery<R> query) {
        return RxTask.observable(() -> query.findInBackground())
                .flatMap(l -> Observable.fromIterable(l))
            .doOnDispose(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Single<Integer> count(@NonNull final ParseQuery<R> query) {
        return RxTask.single(() -> query.countInBackground())
            .doOnDispose(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));

    }

    @NonNull
    public static <R extends ParseObject> Completable pin(@NonNull final R object) {
        return RxTask.completable(() -> object.pinInBackground());
    }

    @NonNull
    public static <R extends ParseObject> Completable pin(@NonNull final List<R> objects) {
        return RxTask.completable(() -> ParseObject.pinAllInBackground(objects));
    }

    @NonNull
    public static <R extends ParseObject> Completable pin(@NonNull final String name, @NonNull final R object) {
        return RxTask.completable(() -> object.pinInBackground(name));
    }

    @NonNull
    public static <R extends ParseObject> Completable pin(@NonNull final String name, @NonNull final List<R> objects) {
        return RxTask.completable(() -> ParseObject.pinAllInBackground(name, objects));
    }

    @NonNull
    public static <R extends ParseObject> Completable unpin(@NonNull final R object) {
        return RxTask.completable(() -> object.unpinInBackground());
    }

    @NonNull
    public static <R extends ParseObject> Completable unpin(@NonNull final List<R> objects) {
        return RxTask.completable(() -> ParseObject.unpinAllInBackground(objects));
    }

    @NonNull
    public static <R extends ParseObject> Completable unpin(@NonNull final String name, @NonNull final R object) {
        return RxTask.completable(() -> object.unpinInBackground(name));
    }

    @NonNull
    public static <R extends ParseObject> Completable unpin(@NonNull final String name, @NonNull final List<R> objects) {
        return RxTask.completable(() -> ParseObject.unpinAllInBackground(name, objects));
    }

    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Observable<R> all(@NonNull final ParseQuery<R> query) {
        return count(query).flatMapObservable(c -> all(query, c));
    }

    /**
     *  Limit 10000 by skip
     */
    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Observable<R> all(@NonNull final ParseQuery<R> query, int count) {
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

    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Single<R> first(@NonNull final ParseQuery<R> query) {
        return RxTask.single(() -> query.getFirstInBackground());
    }

    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Single<R> get(@NonNull final Class<R> clazz, @NonNull final String objectId) {
        return get(ParseQuery.getQuery(clazz), objectId);
    }

    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Single<R> get(@NonNull final ParseQuery<R> query, @NonNull final String objectId) {
        return RxTask.single(() -> query.getInBackground(objectId));
    }

    // Task<T> nullable?
    @NonNull
    @CheckReturnValue
    public static <R> Observable<R> callFunction(@NonNull final String name, @NonNull final Map<String, ?> params) {
        return RxTask.observable(() -> ParseCloud.callFunctionInBackground(name, params));
    }

    @NonNull
    public static <R extends ParseObject> Completable save(@NonNull final R object) {
        return RxTask.completable(() -> object.saveInBackground());
    }

    @NonNull
    public static <R extends ParseObject> Completable save(@NonNull final List<R> objects) {
        return RxTask.completable(() -> ParseObject.saveAllInBackground(objects));
    }

    @NonNull
    public static <R extends ParseObject> Completable saveEventually(@NonNull final R object) {
        return RxTask.completable(() -> object.saveEventually());
    }

    // Task<T> nullable?
    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Single<R> fetch(@NonNull final R object) {
        return RxTask.single(() -> object.fetchInBackground());
    }

    // Task<List<T>> nullable?
    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Observable<R> fetch(@NonNull final List<R> objects) {
        return RxTask.observable(() -> ParseObject.fetchAllInBackground(objects))
                .flatMap(l -> Observable.fromIterable(l));
    }

    // Task<T> nullable?
    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Single<R> fetchIfNeeded(@NonNull final R object) {
        return RxTask.single(() -> object.fetchIfNeededInBackground());
    }

    // Task<List<T>> nullable?
    @NonNull
    @CheckReturnValue
    public static <R extends ParseObject> Observable<R> fetchIfNeeded(@NonNull final List<R> objects) {
        return RxTask.observable(() -> ParseObject.fetchAllIfNeededInBackground(objects))
                .flatMap(l -> Observable.fromIterable(l));
    }

    @NonNull
    public static <R extends ParseObject> Completable delete(@NonNull final R object) {
        return RxTask.completable(() -> object.deleteInBackground());
    }

    @NonNull
    public static <R extends ParseObject> Completable delete(@NonNull final List<R> objects) {
        return RxTask.completable(() -> ParseObject.deleteAllInBackground(objects));
    }

    /* ParsePush */

    @NonNull
    public static Completable subscribe(@NonNull final String channel) {
        return RxTask.completable(() -> ParsePush.subscribeInBackground(channel));
    }

    @NonNull
    public static Completable unsubscribe(@NonNull final String channel) {
        return RxTask.completable(() -> ParsePush.unsubscribeInBackground(channel));
    }

    @NonNull
    public static Completable send(@NonNull final ParsePush push) {
        return RxTask.completable(() -> push.sendInBackground());
    }

    @NonNull
    public static Completable send(@NonNull final JSONObject data, @NonNull final ParseQuery<ParseInstallation> query) {
        return RxTask.completable(() -> ParsePush.sendDataInBackground(data, query));
    }

    @NonNull
    public static Completable send(@NonNull final String message, @NonNull final ParseQuery<ParseInstallation> query) {
        return RxTask.completable(() -> ParsePush.sendMessageInBackground(message, query));
    }

    /* ParseObject */

    // TODO refresh()
    // TODO fetchFromLocalDatastore()

    /* ParseUser */

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> become(@NonNull final String sessionToken) {
        return RxTask.single(() -> ParseUser.becomeInBackground(sessionToken));
    }

    // TODO enableRevocableSessionInBackground

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> logIn(@NonNull final String username, @NonNull final String password) {
        return RxTask.single(() -> ParseUser.logInInBackground(username, password));
    }

    @NonNull
    public static Completable logOut() {
        return RxTask.completable(() -> ParseUser.logOutInBackground());
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> anonymousLogIn() {
        return RxTask.single(() -> ParseAnonymousUtils.logInInBackground());
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

    @NonNull
    public static Completable resetPassword(@NonNull final String email) {
        return RxTask.completable(() -> ParseUser.requestPasswordResetInBackground(email));
    }

    @NonNull
    public static Completable signUp(@NonNull final ParseUser user) {
        return RxTask.completable(() -> user.signUpInBackground());
    }

    // ParseAnalytics

    @NonNull
    public static Completable trackAppOpened(@NonNull final Intent intent) {
        return RxTask.completable(() -> ParseAnalytics.trackAppOpenedInBackground(intent));
    }

    @NonNull
    public static Completable trackEvent(@NonNull final String name) {
        return RxTask.completable(() -> ParseAnalytics.trackEventInBackground(name));
    }

    @NonNull
    public static Completable trackEvent(@NonNull final String name, @NonNull final Map<String,String> dimensions) {
        return RxTask.completable(() -> ParseAnalytics.trackEventInBackground(name, dimensions));
    }

    /* ParseFile */

    @NonNull
    @CheckReturnValue
    public static Single<byte[]> getData(@NonNull final ParseFile file) {
        return RxTask.single(() -> file.getDataInBackground());
    }

    @NonNull
    @CheckReturnValue
    public static Single<byte[]> getData(@NonNull final ParseFile file, @NonNull final ProgressCallback progressCallback) {
        return RxTask.single(() -> file.getDataInBackground(progressCallback));
    }

    @NonNull
    @CheckReturnValue
    public static Single<InputStream> getDataStream(@NonNull final ParseFile file) {
        return RxTask.single(() -> file.getDataStreamInBackground());
    }

    @NonNull
    @CheckReturnValue
    public static Single<InputStream> getDataStream(@NonNull final ParseFile file, @NonNull final ProgressCallback progressCallback) {
        return RxTask.single(() -> file.getDataStreamInBackground(progressCallback));
    }

    @NonNull
    @CheckReturnValue
    public static Single<File> getFile(@NonNull final ParseFile file) {
        return RxTask.single(() -> file.getFileInBackground());
    }

    @NonNull
    @CheckReturnValue
    public static Single<File> getFile(@NonNull final ParseFile file, @NonNull final ProgressCallback progressCallback) {
        return RxTask.single(() -> file.getFileInBackground(progressCallback));
    }

    @NonNull
    public static Completable save(@NonNull final ParseFile file) {
        return RxTask.completable(() -> file.saveInBackground());
    }

    @NonNull
    public static Completable save(@NonNull final ParseFile file, @NonNull final ProgressCallback uploadProgressCallback) {
        return RxTask.completable(() -> file.saveInBackground(uploadProgressCallback));
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseConfig> getConfig() {
        return RxTask.single(() -> ParseConfig.getInBackground());
    }

    /*
    private ParseObservable() {
        throw new UnsupportedOperationException();
    }
    */
}
