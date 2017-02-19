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

package rx.parse2;

import android.app.Activity;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Collection;
import java.util.Date;

import io.reactivex.Observable;
import rx.bolts2.TaskObservable;

public class ParseFacebookObservable {

    public static Observable<ParseUser> link(ParseUser user, Activity activity) {
        return TaskObservable.defer(() -> ParseFacebookUtils.linkInBackground(user, activity))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Activity activity, int activityCode) {
        return TaskObservable.defer(() -> ParseFacebookUtils.linkInBackground(user, activity, activityCode))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Collection<String> permissions, Activity activity) {
        return TaskObservable.defer(() -> ParseFacebookUtils.linkInBackground(user, permissions, activity))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, Collection<String> permissions, Activity activity, int activityCode) {
        return TaskObservable.defer(() -> ParseFacebookUtils.linkInBackground(user, permissions, activity, activityCode))
                .map(v -> user);
    }

    public static Observable<ParseUser> link(ParseUser user, String facebookId, String accessToken, Date expirationDate) {
        return TaskObservable.defer(() -> ParseFacebookUtils.linkInBackground(user, facebookId, accessToken, expirationDate))
                .map(v -> user);
    }

    public static Observable<ParseUser> logIn(Collection<String> permissions, Activity activity, int activityCode) {
        return TaskObservable.defer(() -> ParseFacebookUtils.logInInBackground(permissions, activity, activityCode));
    }

    public static Observable<ParseUser> logIn(Collection<String> permissions, Activity activity) {
        // package class com.parse.FacebookAuthenticationProvider.DEFAULT_AUTH_ACTIVITY_CODE
        // private com.facebook.android.Facebook.DEFAULT_AUTH_ACTIVITY_CODE = 32665
        return logIn(permissions, activity, 32665);
    }

    public static Observable<ParseUser> logIn(String facebookId, String accessToken, Date expirationDate) {
        return TaskObservable.defer(() -> ParseFacebookUtils.logInInBackground(facebookId, accessToken, expirationDate));
    }

    public static Observable<ParseUser> saveLatestSessionData(ParseUser user) {
        return TaskObservable.defer(() -> ParseFacebookUtils.saveLatestSessionDataInBackground(user))
                .map(v -> user);
    }

    public static Observable<ParseUser> unlink(ParseUser user) {
        return TaskObservable.defer(() -> ParseFacebookUtils.unlinkInBackground(user))
                .map(v -> user);
    }

}
