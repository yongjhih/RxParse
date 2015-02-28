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
    public static get() {
        ParseObservable.from(ParseUser.class);
    }

    // to(), be(), as()
    public static getObservable() {
        ParseObservable.from(ParseUser.class);
    }
}
