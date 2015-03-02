package rx.parse;

import rx.schedulers.*;
import rx.Observable;
import rx.functions.*;
import rx.observables.*;

import com.parse.*;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;

import android.app.Activity;

public class ParseObservable<T extends ParseObject> {
    private Class<T> mSubClass;

    public ParseObservable(Class<T> subclass) {
        mSubClass = subclass;
    }

    public static <T extends ParseObject> ParseObservable<T> from(Class<T> subclass) {
        return new ParseObservable<T>(subclass);
    }

    public ParseQuery<T> getQuery() {
        /* error: incompatible types: ParseQuery<ParseUser> cannot be converted to ParseQuery<T>
        if (mSubClass.equals(ParseUser.class)) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            return query;
        }
        */
        return ParseQuery.getQuery(mSubClass);
    }

    public Observable<T> contains(String key, String value) {
        return find(getQuery().whereContains(key, value));
    }

    public Observable<T> find() {
        return find(getQuery());
    }

    public Observable<T> find(ParseQuery<T> query) {
        Observable<List<T>> list = Observable.create(sub -> {
            query.findInBackground(Callbacks.find((l, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(l);
                    sub.onCompleted();
                }
            }));
        });
        return list.flatMap(l -> Observable.from(l));
    }

    public Observable<Integer> count(ParseQuery<T> query) {
        return Observable.create(sub -> {
            query.countInBackground(Callbacks.count((c, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(new Integer(c));
                    sub.onCompleted();
                }
            }));
        });
    }

    public Observable<Integer> count() {
        return count(getQuery());
    }

    public Observable<T> pin(T object) {
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

    public Observable<T> pin(List<T> objects) {
        Observable<List<T>> list = Observable.create(sub -> {
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

    public Observable<T> listFromLocal() {
        return find(getQuery().fromLocalDatastore());
    }

    public Observable<T> all(ParseQuery<T> query) {
        return count(query).flatMap(c -> all(query, c));
    }

    public Observable<T> all(ParseQuery<T> query, int count) {
        final int limit = 1000; // limit limitation
        Observable<T> find = find(query, 0, limit);
        for (int i = limit; i < count; i+= limit) {
            if (i >= 10000) break; // skip limitation
            find.concatWith(find(query, i, limit));
        }
        return find.distinct(o -> o.getObjectId());
    }

    public Observable<T> findSkip(ParseQuery<T> query, int skip) {
        return find(query, skip, -1);
    }

    public Observable<T> findLimit(ParseQuery<T> query, int limit) {
        return find(query, -1, limit);
    }

    public Observable<T> find(ParseQuery<T> query, int skip, int limit) {
        if (skip >= 0) query.setSkip(skip);
        if (limit >= 0) query.setLimit(limit);

        return find(query);
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
}
