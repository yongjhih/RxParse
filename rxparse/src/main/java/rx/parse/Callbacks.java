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

import com.parse.*;
import rx.functions.*;

import java.util.List;

/**
 * For lambda
 */
public class Callbacks {
    public static <T extends ParseObject> FindCallback<T> find(Action2<List<T>, ? super ParseException> callback) {
        return new FindCallback<T>() {
            @Override public void done(List<T> list, ParseException e) {
                callback.call(list, e);
            }
        };
    }

    public static <T extends ParseObject> GetCallback<T> get(Action2<? super T, ? super ParseException> callback) {
        return new GetCallback<T>() {
            @Override public void done(T emit, ParseException e) {
                callback.call(emit, e);
            }
        };
    }

    public static SaveCallback save(Action1<? super ParseException> callback) {
        return new SaveCallback() {
            @Override public void done(ParseException e) {
                callback.call(e);
            }
        };
    }

    public static CountCallback count(Action2<? super Integer, ? super ParseException> callback) {
        return new CountCallback() {
            @Override public void done(int count, ParseException e) {
                callback.call(count, e);
            }
        };
    }

    public static LogInCallback login(Action2<? super ParseUser, ? super ParseException> callback) {
        return new LogInCallback() {
            @Override public void done(ParseUser user, ParseException e) {
                callback.call(user, e);
            }
        };
    }

    public static <R> FunctionCallback function(Action2<? super R, ? super ParseException> callback) {
        return new FunctionCallback<R>() {
            @Override public void done(R emit, ParseException e) {
                callback.call(emit, e);
            }
        };
    }

    public static DeleteCallback delete(Action1<? super ParseException> callback) {
        return new DeleteCallback() {
            @Override public void done(ParseException e) {
                callback.call(e);
            }
        };
    }
}
