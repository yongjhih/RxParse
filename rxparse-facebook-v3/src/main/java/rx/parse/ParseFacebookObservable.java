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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.bolts.TaskObservable;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class ParseFacebookObservable {

    public static Observable<ParseUser> link(ParseUser user, Activity activity) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.linkInBackground(user, activity))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Activity activity, int activityCode) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.linkInBackground(user, activity, activityCode))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Collection<String> permissions, Activity activity) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.linkInBackground(user, permissions, activity))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Collection<String> permissions, Activity activity, int activityCode) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.linkInBackground(user, permissions, activity, activityCode))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, String facebookId, String accessToken, Date expirationDate) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.linkInBackground(user, facebookId, accessToken, expirationDate))
                .map(v -> user);
    }

    public static Observable<ParseUser> logIn(Collection<String> permissions, Activity activity, int activityCode) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.logInInBackground(permissions, activity, activityCode));
    }

    public static Observable<ParseUser> logIn(Collection<String> permissions, Activity activity) {
        // package class com.parse.FacebookAuthenticationProvider.DEFAULT_AUTH_ACTIVITY_CODE
        // private com.facebook.android.Facebook.DEFAULT_AUTH_ACTIVITY_CODE = 32665
        return logIn(permissions, activity, 32665);
    }

    public static Observable<ParseUser> logIn(String facebookId, String accessToken, Date expirationDate) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.logInInBackground(facebookId, accessToken, expirationDate));
    }

    public static Observable<ParseUser> saveLatestSessionData(ParseUser user) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.saveLatestSessionDataInBackground(user))
                .map(v -> user);
    }

    public static Observable<ParseUser> unlink(ParseUser user) {
        return TaskObservable.deferNullable(() -> ParseFacebookUtils.unlinkInBackground(user))
                .map(v -> user);
    }

}
