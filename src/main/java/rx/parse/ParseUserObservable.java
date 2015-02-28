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
        return ParseObservable.from(ParseUser.class).contains(key, value);
    }

    /*
                            ParseObjectObservable.<ParseUser>find();
    Observable<ParseUser> = ParseObjectObservable.find();

    ParseUserObservable.find();
    ParseUserObservable.all();
    PostObservable.find();
    CommentObservable.find();

    public class ParseUserObservable {
        public static Observable<ParseUser> find() {
            return ParseObservable.from(ParseUser.class).find();
        }
        public static Observable<ParseUser> contains(String key, String value) {
            return ParseObservable.from(ParseUser.class).contains(key, value);
        }
    }

    public class PostObservable {
        public static Observable<Post> find() {
            return ParseObservable.from(Post.class).find();
        }
        public static Observable<Post> contains(String key, String value) {
            return ParseObservable.from(Post.class).contains(key, value);
        }
    }

    public class CommentObservable {
        public static Observable<Comment> find() {
            return ParseObservable.from(Comment.class).find();
        }
        public static Observable<Comment> contains(String key, String value) {
            return ParseObservable.from(Comment.class).contains(key, value);
        }
    }
    */

    // TODO
    //public static Observable<ParseUser> contains(Pair... whereClauses) { // limit 10 whereClauses

    // list(), all(), get()
    public static Observable<ParseUser> list() {
        return find();
    }

    public static Observable<ParseUser> listSkip(int skip) {
        return ParseObservable.from(ParseUser.class).findSkip(ParseUser.getQuery(), skip);
    }

    public static Observable<ParseUser> listLimit(int limit) {
        return ParseObservable.from(ParseUser.class).findLimit(ParseUser.getQuery(), limit);
    }

    public static Observable<ParseUser> list(int skip, int limit) {
        return ParseObservable.from(ParseUser.class).find(ParseUser.getQuery(), skip, limit);
    }

    public static Observable<ParseUser> find() {
        return find(ParseUser.getQuery());
    }

    public static Observable<ParseUser> find(ParseQuery<ParseUser> query) {
        return ParseObservable.from(ParseUser.class).find(query);
    }

    public static Observable<Integer> count(ParseQuery<ParseUser> query) {
        return ParseObservable.from(ParseUser.class).count(query);
    }

    public static Observable<Integer> count() {
        return ParseObservable.from(ParseUser.class).count();
    }

    public static Observable<ParseUser> all(ParseQuery<ParseUser> query) {
        return ParseObservable.from(ParseUser.class).all(query);
    }

    public static Observable<ParseUser> all() {
        return all(ParseUser.getQuery());
    }

    public static Observable<ParseUser> pin(ParseUser user) {
        return ParseObservable.from(ParseUser.class).pin(user);
    }

    public static Observable<ParseUser> pin(List<ParseUser> users) {
        return ParseObservable.from(ParseUser.class).pin(users);
    }

    public static Observable<ParseUser> listFromLocal() {
        return ParseObservable.from(ParseUser.class).listFromLocal();
    }

    /*
    public static Observable<ParseUser> getAutoUsers() {
        return Observable.merge(getLocalUsers(), getRemoteUsers().flatMap(user -> pin(user))).distinct(user -> user.getObjectId());
    }
    */
}
