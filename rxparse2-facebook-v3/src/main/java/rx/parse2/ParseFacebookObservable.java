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

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import rx.bolts2.RxTask;

public class ParseFacebookObservable {

    @NonNull
    public static Completable link(@NonNull final ParseUser user, @NonNull final Activity activity) {
        return RxTask.completable(() -> ParseFacebookUtils.linkInBackground(user, activity));
    }

    @NonNull
    public static Completable link(@NonNull final ParseUser user, @NonNull final Activity activity, int activityCode) {
        return RxTask.completable(() -> ParseFacebookUtils.linkInBackground(user, activity, activityCode));
    }

    @NonNull
    public static Completable link(@NonNull final ParseUser user, @NonNull final Collection<String> permissions, @NonNull final Activity activity) {
        return RxTask.completable(() -> ParseFacebookUtils.linkInBackground(user, permissions, activity));
    }

    @NonNull
    public static Completable link(@NonNull final ParseUser user, @NonNull final Collection<String> permissions, @NonNull final Activity activity, int activityCode) {
        return RxTask.completable(() -> ParseFacebookUtils.linkInBackground(user, permissions, activity, activityCode));
    }

    @NonNull
    public static Completable link(@NonNull final ParseUser user, @NonNull final String facebookId, @NonNull final String accessToken, @NonNull final Date expirationDate) {
        return RxTask.completable(() -> ParseFacebookUtils.linkInBackground(user, facebookId, accessToken, expirationDate));
    }

    @CheckReturnValue
    @NonNull
    public static Single<ParseUser> logIn(@NonNull final Collection<String> permissions, Activity activity, int activityCode) {
        return RxTask.single(() -> ParseFacebookUtils.logInInBackground(permissions, activity, activityCode));
    }

    @CheckReturnValue
    @NonNull
    public static Single<ParseUser> logIn(@NonNull final Collection<String> permissions, @NonNull final Activity activity) {
        // package class com.parse.FacebookAuthenticationProvider.DEFAULT_AUTH_ACTIVITY_CODE
        // private com.facebook.android.Facebook.DEFAULT_AUTH_ACTIVITY_CODE = 32665
        return logIn(permissions, activity, 32665);
    }

    @CheckReturnValue
    @NonNull
    public static Single<ParseUser> logIn(@NonNull final String facebookId, @NonNull final String accessToken, @NonNull final Date expirationDate) {
        return RxTask.single(() -> ParseFacebookUtils.logInInBackground(facebookId, accessToken, expirationDate));
    }

    @NonNull
    public static Completable saveLatestSessionData(@NonNull final ParseUser user) {
        return RxTask.completable(() -> ParseFacebookUtils.saveLatestSessionDataInBackground(user));
    }

    @NonNull
    public static Completable unlink(@NonNull final ParseUser user) {
        return RxTask.completable(() -> ParseFacebookUtils.unlinkInBackground(user));
    }

}
