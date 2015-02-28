package rx.parse;

import rx.schedulers.*;
import rx.Observable;
import rx.functions.*;
import rx.observables.*;

import com.parse.*;

import java.util.List;

public class ParseObservable<T extends ParseObject> {
    /*
    public class ParseUserObservable {
        Observable<ParseUser> find() {
        }
    }

    public class PostObservable {
        Observable<Post> find() {
        }
    }

    public class CommentObservable {
        Observable<Comment> find() {
        }
    }
    */

    private Class<T> mSubClass;

    private ParseObservable(Class<T> subclass) {
        mSubClass = subclass;
    }

    public static <T extends ParseObject> ParseObservable<T> getObservable(Class<T> subclass) {
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
}
