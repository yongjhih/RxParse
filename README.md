# RxParse

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RxParse-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1670)
[![JitPack](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=JitPack)](https://jitpack.io/#yongjhih/RxParse)
[![Download](https://api.bintray.com/packages/yongjhih/maven/RxParse/images/download.svg)](https://bintray.com/yongjhih/maven/RxParse/_latestVersion)
[![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse/c3256ac553/javadoc/)
[![Build Status](https://travis-ci.org/yongjhih/RxParse.svg)](https://travis-ci.org/yongjhih/RxParse)
[![Gitter Chat](https://img.shields.io/gitter/room/yongjhih/RxParse.svg)](https://gitter.im/yongjhih/RxParse)
[![Coverage Status](https://coveralls.io/repos/github/yongjhih/RxParse/badge.svg)](https://coveralls.io/github/yongjhih/RxParse)
[![Bountysource](https://bountysource.com/badge/team?team_id=43965&style=bounties_posted)](https://bountysource.com/teams/8tory/bounties)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c583ae8fff9f4855954133c9146a11e4)](https://codacy.com/app/yongjhih/RxParse)
<!--[![javadoc.io](https://javadocio-badges.herokuapp.com/com.infstory/rxparse/badge.svg)](http://www.javadoc.io/doc/com.infstory/rxparse/)-->
<!--[![Coveralls](https://img.shields.io/coveralls/yongjhih/RxParse.svg)](https://coveralls.io/github/yongjhih/RxParse)-->

[![rxparse.png](art/rxparse.png)](art/rxparse.png)

## Usage

javadoc:

* rxparse: [![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse/c3256ac553/javadoc/)
* rxparse-facebook-v3: [![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse-facebook-v3/c3256ac553/javadoc/)
* rxparse-facebook-v4: [![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse-facebook-v4/c3256ac553/javadoc/)

<!--* rxparse: [![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse/2.0.0/javadoc/)-->
<!--* rxparse-facebook-v3: [![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse-facebook-v3/2.0.0/javadoc/)-->
<!--* rxparse-facebook-v4: [![javadoc](https://img.shields.io/github/tag/yongjhih/RxParse.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/RxParse/rxparse-facebook-v4/2.0.0/javadoc/)-->

<!--rxparse: [![javadoc.io](https://javadocio-badges.herokuapp.com/com.infstory/rxparse/badge.svg)](http://www.javadoc.io/doc/com.infstory/rxparse/)-->
<!--rxparse-facebook-v3 (ParseFacebookUtils v3): [![javadoc.io](https://javadocio-badges.herokuapp.com/com.infstory/rxparse-facebook-v3/badge.svg)](http://www.javadoc.io/doc/com.infstory/rxparse-facebook-v3/)-->
<!--rxparse-facebook-v4 (ParseFacebookUtils v4): [![javadoc.io](https://javadocio-badges.herokuapp.com/com.infstory/rxparse-facebook-v4/badge.svg)](http://www.javadoc.io/doc/com.infstory/rxparse-facebook-v4/)-->

### find

`<T extends ParseObject> Observable<T> ParseObservable.find(ParseQuery<T>);`

Before:

```java
ParseUser.getQuery().findInBackground(new FindCallback() {
    @Override
    public done(ParseUser user, ParseException e) {
        if (e == null) System.out.println(user);
    }
});
```

After:

```java
Observable<ParseUser> users = ParseObservable.find(ParseUser.getQuery());
users.subscribe(user -> System.out.println(user.getObjectId()));
```

```java
Observable<ParseUser> users = ParseObservable.find(ParseUser.getQuery().setLimit(1000));
```

### count


Before:

```java
ParseUser.getQuery().countInBackground(new CountCallback() {
    @Override
    public done(int count, ParseException e) {
        if (e == null) System.out.println(count);
    }
});
```

 After:

```java
Observable<Integer> count = ParseObservable.count(ParseUser.getQuery());
count.subscirbe(c -> System.out.println(c));
```

### Sign in with facebook

After:

```java
ParseFacebookObservable.logIn(Arrays.asList("public_profile", "email"), activity).subscribe(user -> {
  System.out.println("user: " + user);
});
```

### Get my commented posts

Before:

```java
ParseComment.getQuery().whereEqualTo("from", ParseUser.getCurrentUser()).findInBackground(new FindCallback<ParseComment> {
    @Override
    public done(List<ParseComment> comments, ParseException e) {
        if (e != null) return;

        ParsePost.getQuery().whereContainedIn("comments", comments).findInBackground(new FindCallback<ParsePost>() {
            @Override
            public done(List<ParsePost> posts, ParseException e2) {
                if (e2 != null) return;

                // ...
            }
        });
    }
});
```

After:

```java
ParseObservable.find(ParseComment.getQuery().whereEqualTo("from", ParseUser.getCurrentUser()))
    .toList()
    .flatMap(comments -> ParseObservable.find(ParsePost.getQuery().whereContainedIn("comments", comments)))
    .subscribe(posts -> {});
```

## Parse cloud code: ParseObservable.callFunction()

```java
Map<String, Object> params = new HashMap<>();
params.put("accessToken", googleToken());
ParseObservable.callFunction("signInWithGoogle", params).subscribe(parseToken -> {});
```

## Installation

via jcenter

```gradle
repositories {
    jcenter()

}

dependencies {
    compile 'com.infstory:rxparse:2.0.3'
    //compile 'com.infstory:rxparse-facebook-v3:2.0.3' // if needed
    //compile 'com.infstory:rxparse-facebook-v4:2.0.3' // if needed
}
```

Or via jitpack.io

```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.yongjhih.RxParse:rxparse:2.0.3'
    //compile 'com.github.yongjhih.RxParse:rxparse-facebook-v3:2.0.3' // if needed
    //compile 'com.github.yongjhih.RxParse:rxparse-facebook-v4:2.0.3' // if needed

    // SNAPSHOT
    //compile 'com.github.yongjhih.RxParse:rxparse:-SNAPSHOT'
    //compile 'com.github.yongjhih.RxParse:rxparse-facebook-v3:-SNAPSHOT' // if needed
    //compile 'com.github.yongjhih.RxParse:rxparse-facebook-v4:-SNAPSHOT' // if needed
}
```

## Test

```bash
./gradlew clean :rxparse:assembleDebug :rxparse:testDebug --tests='*.ParseObservableTest'
```

## Deploy

```bash
./gradlew :rxparse:build :rxparse:bintrayUpload
./gradlew :rxparse-facebook-v3:build :rxparse-facebook-v3:bintrayUpload
./gradlew :rxparse-facebook-v4:build :rxparse-facebook-v4:bintrayUpload
```

## LICENSE

Copyright 2015 8tory, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
