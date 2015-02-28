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
        return ParseObservable.getObservable(ParseUser.class).contains(key, value);
    }

    /*
                            ParseObjectObservable.<ParseUser>find();
    Observable<ParseUser> = ParseObjectObservable.find();

    ParseUserObservable.find();
    PostObservable.find();
    CommentObservable.find();

    public class ParseUserObservable {
        static Observable<ParseUser> find() {
            public static Observable<ParseUser> contains(String key, String value) {
                return ParseObservable.getObservable(ParseUser.class).contains(key, value);
            }
        }
    }

    public class PostObservable {
        Observable<Post> find() {
            public static Observable<ParseUser> contains(String key, String value) {
                return ParseObservable.getObservable(ParseUser.class).contains(key, value);
            }
        }
    }

    public class CommentObservable {
        Observable<Comment> find() {
            public static Observable<ParseUser> contains(String key, String value) {
                return ParseObservable.getObservable(ParseUser.class).contains(key, value);
            }
        }
    }
    */

    // TODO
    //public static Observable<ParseUser> contains(Pair... whereClauses) { // limit 10 whereClauses

    // list(), all(), get()
    public static Observable<ParseUser> list() {
        return ParseObservable.getObservable(ParseUser.class).find();
    }

    public static Observable<ParseUser> listSkip(int skip) {
        return listRange(skip, -1);
    }

    public static Observable<ParseUser> listLimit(int limit) {
        return listRange(-1, limit);
    }

    public static Observable<ParseUser> listRange(int skip, int limit) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        if (skip >= 0) query.setSkip(skip);
        if (limit >= 0) query.setLimit(limit);

        return find(query);
    }

    public static Observable<ParseUser> find() {
        return find(ParseUser.getQuery());
    }

    public static Observable<ParseUser> find(ParseQuery<ParseUser> query) {
        return ParseObservable.getObservable(ParseUser.class).find(query);
    }

    public static Observable<Integer> count(ParseQuery<ParseUser> query) {
        return ParseObservable.getObservable(ParseUser.class).count(query);
    }

    public static Observable<Integer> count() {
        return count(ParseUser.getQuery());
    }

    /*
    public static Observable<ParseUser> all() {
    }
    */

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
        return ParseObservable.getObservable(ParseUser.class).pin(user);
    }

    public static Observable<ParseUser> pin(List<ParseUser> users) {
        return ParseObservable.getObservable(ParseUser.class).pin(users);
    }

    public static Observable<ParseUser> listFromLocal() {
        return ParseObservable.getObservable(ParseUser.class).listFromLocal();
    }

    /*
    public static Observable<ParseUser> getAutoUsers() {
        return Observable.merge(getLocalUsers(), getRemoteUsers().flatMap(user -> pin(user))).distinct(user -> user.getObjectId());
    }
    */
}
