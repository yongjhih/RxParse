package rx.parse;

import com.parse.*;
/*
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.SaveCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
*/

import java.util.List;

/**
 * getInBackground(GetCallback callback)
 * findInBackground(FindCallback callback) // abstract class
 * findInBackground((list, e) -> {;})
 * findInBackground(CallbackUtils.create((list, e) -> {} ))
 */
public class Callbacks {
    public interface IFindCallback<E> {
        void done(List<E> list, ParseException e);
    }

    public interface IGetCallback<E> {
        void done(E emit, ParseException e);
    }

    public interface ISaveCallback {
        void done(ParseException e);
    }

    public interface ICountCallback {
        void done(int count, ParseException e);
    }

    public interface ILogInCallback {
        void done(ParseUser user, ParseException e);
    }

    public interface IFunctionCallback<E> {
        void done(E emit, ParseException e);
    }

    public static <T extends ParseObject> FindCallback<T> find(IFindCallback<T> callback) {
        return new FindCallback<T>() {
            @Override public void done(List<T> list, ParseException e) {
                callback.done(list, e);
            }
        };
    }

    public static <T extends ParseObject> GetCallback<T> get(IGetCallback<T> callback) {
        return new GetCallback<T>() {
            @Override public void done(T emit, ParseException e) {
                callback.done(emit, e);
            }
        };
    }

    public static SaveCallback save(ISaveCallback callback) {
        return new SaveCallback() {
            @Override public void done(ParseException e) {
                callback.done(e);
            }
        };
    }

    public static CountCallback count(ICountCallback callback) {
        return new CountCallback() {
            @Override public void done(int count, ParseException e) {
                callback.done(count, e);
            }
        };
    }

    public static LogInCallback login(ILogInCallback callback) {
        return new LogInCallback() {
            @Override public void done(ParseUser user, ParseException e) {
                callback.done(user, e);
            }
        };
    }

    public static <T> FunctionCallback function(IFunctionCallback<T> callback) {
        return new FunctionCallback<T>() {
            @Override public void done(T emit, ParseException e) {
                callback.done(emit, e);
            }
        };
    }
}
