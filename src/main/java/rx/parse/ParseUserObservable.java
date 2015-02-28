package rx.parse;

import rx.schedulers.*;
import rx.Observable;
import rx.functions.*;
import rx.observables.*;

import com.parse.*;

import java.util.List;

/**
 * ParseObservable
 * ParseObjectObservable
 * ParseQueryObservable
 * ParseUserObservable
 * ParseNotificationObservable
 * com.parse.simple.Callbacks
 */
public class ParseUserObservable {
    public static Observable<ParseUser> contains(String key, String value) {
        Observable<List<ParseUser>> list = Observable.create(sub -> {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContains(key, value);
            query.findInBackground(Callbacks.find((users, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(users);
                    sub.onCompleted();
                }
            }));
        });
        return list.flatMap(users -> Observable.from(users));
    }

    // TODO
    //public static Observable<ParseUser> contains(Pair... whereClauses) { // limit 10 whereClauses

    // list(), all(), get()
    public static Observable<ParseUser> list() {
        Observable<List<ParseUser>> list = Observable.create(sub -> {
            ParseUser.getQuery().findInBackground(Callbacks.find((users, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(users);
                    sub.onCompleted();
                }
            }));
        });
        return list.flatMap(users -> Observable.from(users));
    }

    public static Observable<ParseUser> listSkip(int skip) {
        return list(skip, -1);
    }

    public static Observable<ParseUser> listLimit(int limit) {
        return list(-1, limit);
    }

    public static Observable<ParseUser> list(int skip, int limit) {
        Observable<List<ParseUser>> userList = Observable.create(sub -> {
            ParseQuery<ParseUser> query = ParseUser.getQuery();

            if (skip >= 0) query.setSkip(skip);
            if (limit >= 0) query.setLimit(limit);

            query.findInBackground(Callbacks.find((users, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(users);
                    sub.onCompleted();
                }
            }));
        });
        return userList.flatMap(users -> Observable.from(users));
    }

    public static Observable<Integer> count() {
        return Observable.create(sub -> {
            ParseUser.getQuery().countInBackground(Callbacks.count((c, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(new Integer(c));
                    sub.onCompleted();
                }
            }));
        });
    }

    /*
    public static Observable<ParseUser> all() {
        return all(Observable.empty(), 0);
    }

    public static Observable<ParseUser> all(Observable<ParseUser> obs, int skip) {
        if (obs == null) obs = Observable.empty();
        Observable<ParseUser> list = list(skip, 1000);
        if (list.toList.count() < 1000) return obs.concatWith(list);
        return all(obs, skip + 1000);
    }
    */

    public static Observable<ParseUser> pin(ParseUser user) {
        return Observable.create(sub -> {
            user.pinInBackground(Callbacks.save(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(user);
                    sub.onCompleted();
                }
            }));
        });
    }

    public static Observable<ParseUser> pin(List<ParseUser> users) {
        Observable<List<ParseUser>> list = Observable.create(sub -> {
            ParseObject.pinAllInBackground(users, Callbacks.save(e -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(users);
                    sub.onCompleted();
                }
            }));
        });
        return list.flatMap(l -> Observable.from(l));
    }

    public static Observable<ParseUser> listFromLocal() {
        Observable<List<ParseUser>> list = Observable.create(sub -> {
            ParseUser.getQuery()
            .fromLocalDatastore() //
            .findInBackground(Callbacks.find((users, e) -> {
                if (e != null) {
                    sub.onError(e);
                } else {
                    sub.onNext(users);
                    sub.onCompleted();
                }
            }));
        });

        return list.flatMap(users -> Observable.from(users));
    }

    /*
    public static Observable<ParseUser> getAutoUsers() {
        return Observable.merge(getLocalUsers(), getRemoteUsers().flatMap(user -> pin(user))).distinct(user -> user.getObjectId());
    }
    */
}
