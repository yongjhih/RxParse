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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;

public class ParseObservable<T extends ParseObject> {
    private Class<T> mSubClass;

    public ParseObservable(Class<T> subclass) {
        mSubClass = subclass;
    }

    /** {@Deprecated} */
    public static <T extends ParseObject> ParseObservable<T> from(Class<T> subclass) {
        return of(subclass);
    }

    /** {@Deprecated} */
    public static <T extends ParseObject> ParseObservable<T> of(Class<T> subclass) {
        return new ParseObservable<T>(subclass);
    }

    /** {@Deprecated} */
    public ParseQuery<T> getQuery() {
        /* error: incompatible types: ParseQuery<ParseUser> cannot be converted to ParseQuery<T>
        if (mSubClass.equals(ParseUser.class)) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            return query;
        }
        */
        return ParseQuery.getQuery(mSubClass);
    }

    /** {@Deprecated} */
    public Observable<T> find() {
        return find(getQuery());
    }

    public static <R extends ParseObject> Observable<R> find(ParseQuery<R> query) {
        Observable<List<R>> list = Observable.create(sub -> {
            query.findInBackground(Callbacks.find((l, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(l);
                    sub.onCompleted();
                }
            }));
        });
        return list.flatMap(l -> Observable.from(l))
            .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static <R extends ParseObject> Observable<Integer> count(ParseQuery<R> query) {
        return Observable.<Integer>create(sub -> {
            query.countInBackground(Callbacks.count((c, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(new Integer(c));
                    sub.onCompleted();
                }
            }));
        })
        .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public Observable<Integer> count() {
        return count(getQuery());
    }

    public static <R extends ParseObject> Observable<R> pin(R object) {
        return Observable.create(sub -> {
            object.pinInBackground(Callbacks.save(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(object);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static <R extends ParseObject> Observable<R> pin(List<R> objects) {
        Observable<List<R>> list = Observable.create(sub -> {
            ParseObject.pinAllInBackground(objects, Callbacks.save(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(objects);
                    sub.onCompleted();
                }
            }));
        });
        return list.flatMap(l -> Observable.from(l));
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
        return Observable.<R>create(sub -> {
            query.getFirstInBackground(Callbacks.get((o, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(o);
                    sub.onCompleted();
                }
            }));
        })
        .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public Observable<T> get(String objectId) {
        ParseQuery<T> query = getQuery();
        return Observable.<T>create(sub -> {
            query.getInBackground(objectId, Callbacks.get((o, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(o);
                    sub.onCompleted();
                }
            }));
        })
        .doOnUnsubscribe(() -> Observable.just(query)
                .doOnNext(q -> q.cancel())
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {}, e -> {}));
    }

    public static Observable<ParseUser> loginWithFacebook(Activity activity, Collection<String> permissions) {
        return Observable.create(sub -> {
            ParseFacebookUtils.logIn(permissions, activity, Callbacks.login((user, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(user);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static Observable<ParseUser> loginWithFacebook(Activity activity) {
        return loginWithFacebook(activity, Arrays.asList("public_profile", "email"));
    }

    public static <R> Observable<R> callFunction(String name, Map<String, R> params) {
        return Observable.create(sub -> {
            ParseCloud.callFunctionInBackground(name, params, Callbacks.<R>function((o, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(o);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static <R extends ParseObject> Observable<R> save(R object) {
        return Observable.create(sub -> {
            object.saveInBackground(Callbacks.save(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(object);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static <R extends ParseObject> Observable<R> fetchIfNeeded(R object) {
        return Observable.<R>create(sub -> {
            object.<R>fetchIfNeededInBackground(Callbacks.get((o, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(o);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static <R extends ParseObject> Observable<R> delete(R object) {
        return Observable.create(sub -> {
            object.deleteInBackground(Callbacks.delete(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(object);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static <R extends ParseObject> Observable<R> delete(List<R> objects) {
        return Observable.<List<R>>create(sub -> {
            ParseObject.deleteAllInBackground(objects, Callbacks.delete(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(objects);
                    sub.onCompleted();
                }
            }));
        }).flatMap(l -> Observable.from(l));
    }
}
