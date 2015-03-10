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
public class ParseUserObservable extends ParseObservable<ParseUser> {
    public ParseUserObservable(Class<ParseUser> subclass) {
        super(subclass);
    }

    public static ParseUserObservable from() {
        return new ParseUserObservable(ParseUser.class);
    }

    public static ParseUserObservable get() {
        return from();
    }

    public static ParseUserObservable of() {
        return from();
    }

    // to(), be(), as()
    public static ParseUserObservable getObservable() {
        return from();
    }
}
