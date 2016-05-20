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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bolts.Task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static mocker.Mocker.mocker;
import mocker.Mocker;
import rx.Observable;

public class ParseObservableTest {

    @Test
    public void testParseObservableAllNextAfterCompleted() {
        //List<ParseUser> users = Arrays.asList(
                //mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "1_" + user.hashCode()).mock(),
                //mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "2_" + user.hashCode()).mock(),
                //mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "3_" + user.hashCode()).mock());
        //ParseUser parseUser = mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> String.valueOf(user.hashCode())).mock();
        //List<ParseUser> users = Arrays.asList(parseUser, parseUser, parseUser);
        Mocker<ParseUser> mocker = mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> String.valueOf(user.hashCode()));
        List<ParseUser> users = Arrays.asList(mocker.mock(), mocker.mock(), mocker.mock());

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.all(mocker(ParseQuery.class)
                    .when(query -> query.countInBackground()).thenReturn(query -> Task.forResult(users.size()))
                    .when(query -> query.findInBackground()).thenReturn(query -> Task.forResult(users))
                    .when(query -> query.setSkip(any(int.class))).thenReturn(query -> null)
                    .when(query -> query.setLimit(any(int.class))).thenReturn(query -> null).mock())
                )
                //.doOnNext(user -> System.out.println("" + ((ParseUser) user).getObjectId())))
            .withoutErrors()
            .expectedValues(users)
            .completes();
    }

    @Test
    public void testParseObservableAllForMass() {
        // FIXME: how mockito to make mass mocks?
        List<ParseUser> users = Observable.range(1, 1001)
            .map(i -> mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "" + i + user.hashCode()).mock())
            .toList()
            .toBlocking()
            .single();

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.all(mocker(ParseQuery.class)
                    .when(query -> query.countInBackground()).thenReturn(query -> Task.forResult(users.size()))
                    .when(query -> query.findInBackground()).thenReturn(query -> Task.forResult(users))
                    .when(query -> query.setSkip(any(int.class))).thenReturn(query -> null)
                    .when(query -> query.setLimit(any(int.class))).thenReturn(query -> null).mock()))
            .withoutErrors()
            .expectedValues(users)
            .completes();
    }

    @Test
    public void testParseObservableFindNextAfterCompleted() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.find(mocker(ParseQuery.class)
                    .when(query -> query.findInBackground()).thenReturn(query -> Task.forResult(users))
                    .mock()))
            .withoutErrors()
            .expectedValues(users)
            .completes();
    }

    @Test
    public void testParseObservableFirst() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.first(mocker(ParseQuery.class)
                    .when(query -> query.getFirstInBackground()).thenReturn(query -> Task.forResult(users.get(0)))
                    .mock()))
            .withoutErrors()
            .expectedValues(users.get(0))
            .completes();
    }

    @Test
    public void testBlockingFind() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));
        ParseQuery<ParseUser> query = mocker(ParseQuery.class)
            .when(q -> q.findInBackground()).thenReturn(q -> Task.forResult(users))
            .when(q -> {
                List<ParseUser> list = Collections.emptyList();
                try {
                    list = q.find();
                } catch (Exception e) {
                }
                return list;
            }).thenReturn(q -> users)
            .mock();
        try {
            assertEquals(query.find(), rx.parse.ParseObservable.find(query).toList().toBlocking().single());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseObservablePin() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.pin(mocker(ParseUser.class)
                    .when(user -> user.pinInBackground())
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

}
