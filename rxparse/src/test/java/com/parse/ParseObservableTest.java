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
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;

import static org.hamcrest.CoreMatchers.instanceOf;
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

import rx.functions.*;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.*;
import java.util.Collections;

// Avoid cannot be accessed from outside package
public class ParseObservableTest {

    @Before
    public void setUp() {
        ParseTestUtils.setTestParseUser();
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
        ParseUser user = mock(ParseUser.class);
        ParseUser user2 = mock(ParseUser.class);
        ParseUser user3 = mock(ParseUser.class);
        List<ParseUser> users = new ArrayList<>();
        when(user.getObjectId()).thenReturn("1_" + user.hashCode());
        users.add(user);
        when(user2.getObjectId()).thenReturn("2_" + user2.hashCode());
        users.add(user2);
        when(user3.getObjectId()).thenReturn("3_" + user3.hashCode());
        users.add(user3);
        ParseQueryController queryController = mock(ParseQueryController.class);
        ParseCorePlugins.getInstance().registerQueryController(queryController);

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
            .expectedValues(user, user2, user3)
            .completes();

        try {
            ParseTaskUtils.wait(task);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Test
    public void testParseObservableFindNextAfterCompleted() {
        ParseUser user = mock(ParseUser.class);
        ParseUser user2 = mock(ParseUser.class);
        ParseUser user3 = mock(ParseUser.class);
        List<ParseUser> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        users.add(user3);
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
            .expectedValues(user, user2, user3)
            .completes();

        try {
            ParseTaskUtils.wait(task);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Test
    public void testBlockingFind() {
        ParseQueryController queryController = mock(ParseQueryController.class);
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        Task<List<ParseUser>> task = Task.forResult(Arrays.asList(mock(ParseUser.class), mock(ParseUser.class), mock(ParseUser.class)));
        when(queryController.findAsync(any(ParseQuery.State.class), any(ParseUser.class), any(Task.class))).thenReturn(task);

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

        try {
            assertEquals(query.find(), rx.parse.ParseObservable.find(query).toList().toBlocking().single());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
