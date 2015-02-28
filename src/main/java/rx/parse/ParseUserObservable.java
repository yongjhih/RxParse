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

    public static ParseUserObservable get() {
        return new ParseUserObservable(ParseUser.class);
    }

    // to(), be(), as()
    public static ParseUserObservable getObservable() {
        return new ParseUserObservable(ParseUser.class);
    }
}
