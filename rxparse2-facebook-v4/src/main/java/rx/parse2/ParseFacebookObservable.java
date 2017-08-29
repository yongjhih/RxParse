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
import android.support.v4.app.Fragment;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Collection;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import rx.bolts2.RxTask;

public class ParseFacebookObservable {

    @NonNull
    public static Completable link(@NonNull final ParseUser user, @NonNull final com.facebook.AccessToken accessToken) {
        return RxTask.completable(() -> ParseFacebookUtils.linkInBackground(user, accessToken));
    }

    @NonNull
    public static Completable linkWithPublishPermissions(@NonNull final ParseUser user, @NonNull final Activity activity, @NonNull final Collection<String> permissions) {
        return RxTask.completable(() -> ParseFacebookUtils.linkWithPublishPermissionsInBackground(user, activity, permissions));
    }

    @NonNull
    public static Completable linkWithPublishPermissions(@NonNull final ParseUser user, @NonNull final Fragment fragment, @NonNull final Collection<String> permissions) {
        return RxTask.completable(() -> ParseFacebookUtils.linkWithPublishPermissionsInBackground(user, fragment, permissions));
    }

    @NonNull
    public static Completable linkWithReadPermissions(@NonNull final ParseUser user, @NonNull final Activity activity, @NonNull final Collection<String> permissions) {
        return RxTask.completable(() -> ParseFacebookUtils.linkWithReadPermissionsInBackground(user, activity, permissions));
    }

    @NonNull
    public static Completable linkWithReadPermissions(@NonNull final ParseUser user, @NonNull final Fragment fragment, @NonNull final Collection<String> permissions) {
        return RxTask.completable(() -> ParseFacebookUtils.linkWithReadPermissionsInBackground(user, fragment, permissions));
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> logIn(@NonNull final com.facebook.AccessToken accessToken) {
        return RxTask.single(() -> ParseFacebookUtils.logInInBackground(accessToken));
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> logInWithPublishPermissions(@NonNull final Activity activity, @NonNull final Collection<String> permissions) {
        return RxTask.single(() -> ParseFacebookUtils.logInWithPublishPermissionsInBackground(activity, permissions));
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> logInWithPublishPermissions(@NonNull final Fragment fragment, @NonNull final Collection<String> permissions) {
        return RxTask.single(() -> ParseFacebookUtils.logInWithPublishPermissionsInBackground(fragment, permissions));
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> logInWithReadPermissions(@NonNull final Activity activity, @NonNull final Collection<String> permissions) {
        return RxTask.single(() -> ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions));
    }

    @NonNull
    @CheckReturnValue
    public static Single<ParseUser> logInWithReadPermissions(@NonNull final Fragment fragment, @NonNull final Collection<String> permissions) {
        return RxTask.single(() -> ParseFacebookUtils.logInWithReadPermissionsInBackground(fragment, permissions));
    }

    @NonNull
    public static Completable unlink(@NonNull final ParseUser user) {
        return RxTask.completable(() -> ParseFacebookUtils.unlinkInBackground(user));
    }

}
