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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static mocker.Mocker.mocker;
import mocker.Mocker;
import java.io.File;
import java.io.InputStream;

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
    public void testParseObservableGet() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.get(mocker(ParseQuery.class)
                    .when(query -> query.getInBackground(any(String.class))).thenReturn(query -> Task.forResult(mock(ParseUser.class)))
                    .mock(), "hello"))
            .withoutErrors()
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

    @Test
    public void testParseObservablePinName() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.pin("hello", mocker(ParseUser.class)
                    .when(user -> user.pinInBackground(any(String.class)))
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableUnpin() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.unpin(mocker(ParseUser.class)
                    .when(user -> user.unpinInBackground())
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableUnpinName() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.unpin("hello", mocker(ParseUser.class)
                    .when(user -> user.unpinInBackground(any(String.class)))
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableSaveEventually() {
        ParseUser user = mock(ParseUser.class);
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.saveEventually(user))
            .fails();
        try {
            verify(user).saveEventually();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /* NPE
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.saveEventually(mocker(ParseUser.class)
                    .when(user -> user.saveEventually())
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
            */
        /* isDirty()
        ParseUser user = mock(ParseUser.class);
        doReturn(bolts.Task.<Void>forResult(null)).when(user).saveEventually();
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.saveEventually(user))
            .withoutErrors()
            .completes();
        */
    }

    @Test
    public void testParseObservableFetch() {
        /*
        ParseUser user = mock(ParseUser.class);
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.fetch(user))
            .fails();
        try {
            verify(user).fetchInBackground();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        */
        /* NPE
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.fetch(mocker(ParseUser.class)
                    .when(user -> user.fetchInBackground())
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .then(user -> doThrow(new RuntimeException()).when(user))
                    .mock()))
            .withoutErrors()
            .completes();
            */
    }

    @Test
    public void testParseObservableFetchIfNeeded() {
        ParseUser user = mock(ParseUser.class);
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.fetchIfNeeded(user))
            .fails();
        try {
            verify(user).fetchIfNeededInBackground();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /* NPE
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.fetchIfNeeded(mocker(ParseUser.class)
                    .when(user -> user.fetchIfNeededInBackground())
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
        */
    }

    @Test
    public void testParseObservableSend() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.send(mocker(ParsePush.class)
                    .when(it -> it.sendInBackground())
                    .thenReturn(it -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableSignUp() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.signUp(mocker(ParseUser.class)
                    .when(user -> user.signUpInBackground())
                    .thenReturn(user -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableGetData() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.getData(mocker(ParseFile.class)
                    .when(it -> it.getDataInBackground())
                    .thenReturn(it -> bolts.Task.<byte[]>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableGetDataProgress() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.getData(mocker(ParseFile.class)
                    .when(it -> it.getDataInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> bolts.Task.<byte[]>forResult(null))
                    .mock(), mock(ProgressCallback.class)))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableGetDataStream() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.getDataStream(mocker(ParseFile.class)
                    .when(it -> it.getDataStreamInBackground())
                    .thenReturn(it -> bolts.Task.<InputStream>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableGetDataStreamProgress() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.getDataStream(mocker(ParseFile.class)
                    .when(it -> it.getDataStreamInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> bolts.Task.<InputStream>forResult(null))
                    .mock(), mock(ProgressCallback.class)))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableGetFile() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.getFile(mocker(ParseFile.class)
                    .when(it -> it.getFileInBackground())
                    .thenReturn(it -> bolts.Task.<File>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableGetFileProgress() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.getFile(mocker(ParseFile.class)
                    .when(it -> it.getFileInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> bolts.Task.<File>forResult(null))
                    .mock(), mock(ProgressCallback.class)))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableSaveFile() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.save(mocker(ParseFile.class)
                    .when(it -> it.saveInBackground())
                    .thenReturn(it -> bolts.Task.<Void>forResult(null))
                    .mock()))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableSaveFileProgress() {
        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.save(mocker(ParseFile.class)
                    .when(it -> it.saveInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> bolts.Task.<Void>forResult(null))
                    .mock(), mock(ProgressCallback.class)))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testConstructor() {
        assertNotNull(new ParseObservable());
    }

    /*
    @Test(expected=IllegalAccessException.class)
    public void testConstructorPrivate() throws Exception {
        ParseObservable.class.newInstance();
        fail("Constructor should be private");
    }
    */
}
