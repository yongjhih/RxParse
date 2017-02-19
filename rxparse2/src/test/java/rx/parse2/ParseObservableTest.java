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

import com.parse.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bolts.Task;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static mocker.Mocker.mocker;
import java.io.File;
import java.io.InputStream;


public class ParseObservableTest {

    @Test
    public void testParseObservableAllNextAfterCompleted() {
        List<ParseUser> users = mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> String.valueOf(user.hashCode())).asList(3);

        rx.parse2.ParseObservable.all(mocker(ParseQuery.class)
                    .when(query -> query.countInBackground()).thenReturn(query -> Task.forResult(users.size()))
                    .when(query -> query.findInBackground()).thenReturn(query -> Task.forResult(users))
                    .when(query -> query.setSkip(any(int.class))).thenReturn(query -> null)
                    .when(query -> query.setLimit(any(int.class))).thenReturn(query -> null).mock())
                    //.doOnNext(user -> System.out.println("" + ((ParseUser) user).getObjectId())))
            .test()
            .assertNoErrors()
            //.assertValue(check(l -> assertThat(l).isEqualTo(users)))
            .assertComplete();
    }

    @Test
    public void testParseObservableAllForMass() {
        // FIXME: how mockito to make mass mocks?
        List<ParseUser> users = Observable.range(1, 1001)
            .map(i -> mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "" + i + user.hashCode()).mock())
            .toList()
            .blockingGet();

        rx.parse2.ParseObservable.all(mocker(ParseQuery.class)
                    .when(query -> query.countInBackground()).thenReturn(query -> Task.forResult(users.size()))
                    .when(query -> query.findInBackground()).thenReturn(query -> Task.forResult(users))
                    .when(query -> query.setSkip(any(int.class))).thenReturn(query -> null)
                    .when(query -> query.setLimit(any(int.class))).thenReturn(query -> null).mock())
            .test()
            .assertNoErrors()
            //.assertValue(check(l -> assertThat(l).isEqualTo(users)))
            .assertComplete();
    }

    @Test
    public void testParseObservableFindNextAfterCompleted() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));

        rx.parse2.ParseObservable.find(mocker(ParseQuery.class)
                    .when(query -> query.findInBackground()).thenReturn(query -> Task.forResult(users))
                    .mock())
            .test()
            .assertNoErrors()
            //.assertValue(check(l -> assertThat(l).isEqualTo(users)))
            .assertComplete();
    }

    @Test
    public void testParseObservableFirst() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));

        rx.parse2.ParseObservable.first(mocker(ParseQuery.class)
                    .when(query -> query.getFirstInBackground()).thenReturn(query -> Task.forResult(users.get(0)))
                    .mock())
            .test()
            .assertNoErrors()
            .assertValue(users.get(0))
            .assertComplete();
    }

    @Test
    public void testParseObservableGet() {
        rx.parse2.ParseObservable.get(mocker(ParseQuery.class)
                    .when(query -> query.getInBackground(any(String.class))).thenReturn(query -> Task.forResult(mock(ParseUser.class)))
                    .mock(), "hello")
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testBlockingFind() throws ParseException {
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
        assertThat(query.find()).isEqualTo(rx.parse2.ParseObservable.find(query).toList().blockingGet());
    }

    @Test
    public void testParseObservablePin() {
        rx.parse2.ParseObservable.pin(mocker(ParseUser.class)
                    .when(user -> user.pinInBackground())
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservablePinName() {
        rx.parse2.ParseObservable.pin("hello", mocker(ParseUser.class)
                    .when(user -> user.pinInBackground(any(String.class)))
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableUnpin() {
        rx.parse2.ParseObservable.unpin(mocker(ParseUser.class)
                    .when(user -> user.unpinInBackground())
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableUnpinName() {
        rx.parse2.ParseObservable.unpin("hello", mocker(ParseUser.class)
                    .when(user -> user.unpinInBackground(any(String.class)))
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableSaveEventually() {
        ParseUser user = mock(ParseUser.class);
        rx.parse2.ParseObservable.saveEventually(user)
            .test().assertFailure(NullPointerException.class);
        try {
            verify(user).saveEventually();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /* NPE
        rx.parse.ParseObservable.saveEventually(mocker(ParseUser.class)
                    .when(user -> user.saveEventually())
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock()))
            .test()
.assertNoErrors()
            .assertComplete();
            */
        /* isDirty()
        ParseUser user = mock(ParseUser.class);
        doReturn(Task.<Void>forResult(null)).when(user).saveEventually();
        rx.parse.ParseObservable.saveEventually(user))
            .test()
.assertNoErrors()
            .assertComplete();
        */
    }

    @Test
    public void testParseObservableFetch() {
        /*
        ParseUser user = mock(ParseUser.class);
        rx.parse.ParseObservable.fetch(user))
            .fails();
        try {
            verify(user).fetchInBackground();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        */
        /* NPE
        rx.parse.ParseObservable.fetch(mocker(ParseUser.class)
                    .when(user -> user.fetchInBackground())
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .then(user -> doThrow(new RuntimeException()).when(user))
                    .mock()))
            .test()
.assertNoErrors()
            .assertComplete();
            */
    }

    @Test
    public void testParseObservableFetchIfNeeded() {
        ParseUser user = mock(ParseUser.class);
        rx.parse2.ParseObservable.fetchIfNeeded(user)
            .test().assertFailure(NullPointerException.class);
        try {
            verify(user).fetchIfNeededInBackground();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /* NPE
        rx.parse.ParseObservable.fetchIfNeeded(mocker(ParseUser.class)
                    .when(user -> user.fetchIfNeededInBackground())
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock()))
            .test()
.assertNoErrors()
            .assertComplete();
        */
    }

    @Test
    public void testParseObservableSend() {
        rx.parse2.ParseObservable.send(mocker(ParsePush.class)
                    .when(it -> it.sendInBackground())
                    .thenReturn(it -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableSignUp() {
        rx.parse2.ParseObservable.signUp(mocker(ParseUser.class)
                    .when(user -> user.signUpInBackground())
                    .thenReturn(user -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableGetData() {
        rx.parse2.ParseObservable.getData(mocker(ParseFile.class)
                    .when(it -> it.getDataInBackground())
                    .thenReturn(it -> Task.<byte[]>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableGetDataProgress() {
        rx.parse2.ParseObservable.getData(mocker(ParseFile.class)
                    .when(it -> it.getDataInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> Task.<byte[]>forResult(null))
                    .mock(), mock(ProgressCallback.class))
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableGetDataStream() {
        rx.parse2.ParseObservable.getDataStream(mocker(ParseFile.class)
                    .when(it -> it.getDataStreamInBackground())
                    .thenReturn(it -> Task.<InputStream>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableGetDataStreamProgress() {
        rx.parse2.ParseObservable.getDataStream(mocker(ParseFile.class)
                    .when(it -> it.getDataStreamInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> Task.<InputStream>forResult(null))
                    .mock(), mock(ProgressCallback.class))
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableGetFile() {
        rx.parse2.ParseObservable.getFile(mocker(ParseFile.class)
                    .when(it -> it.getFileInBackground())
                    .thenReturn(it -> Task.<File>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableGetFileProgress() {
        rx.parse2.ParseObservable.getFile(mocker(ParseFile.class)
                    .when(it -> it.getFileInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> Task.<File>forResult(null))
                    .mock(), mock(ProgressCallback.class))
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableSaveFile() {
        rx.parse2.ParseObservable.save(mocker(ParseFile.class)
                    .when(it -> it.saveInBackground())
                    .thenReturn(it -> Task.<Void>forResult(null))
                    .mock())
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testParseObservableSaveFileProgress() {
        rx.parse2.ParseObservable.save(mocker(ParseFile.class)
                    .when(it -> it.saveInBackground(any(ProgressCallback.class)))
                    .thenReturn(it -> Task.<Void>forResult(null))
                    .mock(), mock(ProgressCallback.class))
            .test()
            .assertNoErrors()
            .assertComplete();
    }

    @Test
    public void testConstructor() {
        assertThat(new ParseObservable()).isNotNull();
    }

    public static <T> Predicate<T> check(Consumer<T> consumer) {
        return t -> {
            consumer.accept(t);
            return true;
        };
    }
}
