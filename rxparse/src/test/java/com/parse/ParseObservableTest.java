/*
 * Copyright (c) 2015-present, 8tory. Inc.
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import bolts.Task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static mocker.Mocker.mocker;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import rx.parse.BuildConfig;

// Avoid cannot be accessed from outside package
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ParseObservableTest {

    @Before
    public void setUp() {
        ParseTestUtils.setTestParseUser();
        Parse.enableLocalDatastore(RuntimeEnvironment.application.getApplicationContext());
        ParseObject.registerSubclass(ParseUser.class);
    }

    @After
    public void tearDown() {
        ParseObject.unregisterSubclass(ParseUser.class);
        ParseCorePlugins.getInstance().reset();
        Parse.disableLocalDatastore();
    }

    @Test
    public void testParseObservableAllNextAfterCompleted() {
        ParseQueryController queryController = mock(ParseQueryController.class);
        ParseCorePlugins.getInstance().registerQueryController(queryController);

        List<ParseUser> users = Arrays.asList(
                mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "1_" + user.hashCode()).mock(),
                mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "2_" + user.hashCode()).mock(),
                mocker(ParseUser.class).when(user -> user.getObjectId()).thenReturn(user -> "3_" + user.hashCode()).mock());

        Task<List<ParseUser>> task = Task.forResult(users);
        when(queryController.findAsync(
                    any(ParseQuery.State.class),
                    any(ParseUser.class),
                    any(Task.class))
            ).thenReturn(task);
        when(queryController.countAsync(
                    any(ParseQuery.State.class),
                    any(ParseUser.class),
                    any(Task.class))).thenReturn(Task.<Integer>forResult(users.size()));

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.setUser(new ParseUser());

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.all(query))
            .withoutErrors()
            .expectedValues(users)
            .completes();

        try {
            ParseTaskUtils.wait(task);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Test
    public void testParseObservableFindNextAfterCompleted() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));
        ParseQueryController queryController = mock(ParseQueryController.class);
        ParseCorePlugins.getInstance().registerQueryController(queryController);

        Task<List<ParseUser>> task = Task.forResult(users);
        when(queryController.findAsync(
                    any(ParseQuery.State.class),
                    any(ParseUser.class),
                    any(Task.class))
            ).thenReturn(task);
            //).thenThrow(IllegalStateException.class);

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.setUser(new ParseUser());

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.find(query))
            .withoutErrors()
            .expectedValues(users)
            .completes();

        try {
            ParseTaskUtils.wait(task);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Test
    public void testBlockingFind() throws ParseException {
        ParseQueryController queryController = mock(ParseQueryController.class);
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        Task<List<ParseUser>> task = Task.forResult(Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class)));
        when(queryController.findAsync(any(ParseQuery.State.class), any(ParseUser.class), any(Task.class))).thenReturn(task);

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

        assertThat(query.find()).isEqualTo(rx.parse.ParseObservable.find(query).toList().toBlocking().single());
    }

    @Test
    public void testParseObservablePinList() {
    }

    @Test
    public void testParseObservableSave() {
        ParseObjectController controller = mock(ParseObjectController.class);
        ParseCorePlugins.getInstance().registerObjectController(controller);

        when(controller.saveAsync(
                    any(ParseObject.State.class),
                    any(ParseOperationSet.class),
                    any(String.class),
                    any(ParseDecoder.class))
            ).thenReturn(Task.<ParseObject.State>forResult(mock(ParseObject.State.class)));

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.save(mock(ParseUser.class)))
            .withoutErrors()
            .completes();
     }

    @Test
    public void testParseObservableSaveAll() {
        List<ParseUser> users = Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class));
        ParseObjectController controller = mock(ParseObjectController.class);
        ParseCorePlugins.getInstance().registerObjectController(controller);

        when(controller.saveAllAsync(
                    any(List.class),
                    any(List.class),
                    any(String.class),
                    any(List.class)))
            //.thenReturn(Task.<List<ParseObject.State>>forResult(Arrays.asList(mock(ParseObject.State.class), mock(ParseObject.State.class), mock(ParseObject.State.class))));
            .thenReturn(Arrays.asList(mock(ParseObject.State.class), mock(ParseObject.State.class), mock(ParseObject.State.class)));

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.save(users))
            .withoutErrors()
            .completes();
     }

    @Test
    public void testParseObservableUnsubscribe() {
        ParsePushChannelsController controller = mock(ParsePushChannelsController.class);
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);

        when(controller.subscribeInBackground(any(String.class)))
            .thenReturn(Task.forResult(null));

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.subscribe("hello"))
            .withoutErrors()
            .completes();
    }

    @Test
    public void testParseObservableSubscribe() {
        ParsePushChannelsController controller = mock(ParsePushChannelsController.class);
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);

        when(controller.unsubscribeInBackground(any(String.class)))
            .thenReturn(Task.forResult(null));

        rx.assertions.RxAssertions.assertThat(rx.parse.ParseObservable.unsubscribe("hello"))
            .withoutErrors()
            .completes();
    }

}
