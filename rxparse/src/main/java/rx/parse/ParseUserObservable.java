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
