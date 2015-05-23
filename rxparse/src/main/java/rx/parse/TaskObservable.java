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

package rx.bolts;

import bolts.Task;

import rx.schedulers.*;
import rx.Observable;
import rx.functions.*;
import rx.observables.*;

/**
 * Bolts2Rx
 * Bolts.Task2Observable
 * RxBolts
 * RxTask
 * BoltsObservable
 * TaskObservable
 */
public class TaskObservable {

    public static <R> Observable<R> just(Task<R> task, boolean nullable) {
        return Observable.create(sub -> {
            task.continueWith(t -> {
                if (t.isCancelled()) {
                    // NOTICE: doOnUnsubscribe(() -> Observable.just(query) in outside
                    sub.unsubscribe(); //sub.onCompleted();?
                } else if (t.isFaulted()) {
                    Throwable error = t.getError();
                    sub.onError(error);
                } else {
                    R r = t.getResult();
                    if (nullable || r != null) sub.onNext(r);
                    sub.onCompleted();
                }
                return null;
            });
        });
    }

    public static <R> Observable<R> justNullable(Task<R> task) {
        return just(task, true);
    }

    public static <R> Observable<R> just(Task<R> task) {
        return just(task, false);
    }

    @Deprecated
    public static <R> Observable<R> just(Func0<Task<R>> task) {
        return defer(task);
    }

    public static <R> Observable<R> defer(Func0<Task<R>> task) {
        return Observable.defer(() -> just(task.call()));
    }

}
