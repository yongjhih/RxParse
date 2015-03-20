# RxParse

## Usage

### find

Before:

```java
ParseUser.getQuery().findInBackground(new FindCallback() {
    @Override
    public done(ParseUser user, ParseException e) {
        if (e == null) System.out.println(user));
    }
});
```

After:

```java
Observable<ParseUser> users = ParseObservable.from(ParseUser.class).find();
users.subscribe(user -> System.out.println(user.getObjectId()));
```

```java
Observable<ParseUser> users = ParseObservable.from(ParseUser.class).find(ParseUser.getQuery().setLimit(1000));
```

### count

Before:

```java
ParseUser.getQuery().countInBackground(new CountCallback() {
    @Override
    public done(int count, ParseException e) {
        if (e == null) System.out.println(count));
    }
});
```

After:

```java
Observable<Integer> count = ParseObservable.from(ParseUser.class).count();
count.subscirbe(c -> System.out.println(c));
```

### loginWithFacebook

```java
Observable<ParseUser> loginUser = ParseObservable.loginWithFacebook(activity);
```

### fetchIfNeeded

Before:

```java
ParseUser.getQuery().fetchIfNeededInBackground(new GetCallback() {
    @Override
    public done(ParseUser user, ParseException e) {
        if (e == null) System.out.println(user));
    }
});
```

After:

```java
ParseObservable.fetchIfNeeded(user)
    .subscribe(user -> System.out.println(user));
```

### pin

Before:

```java
user.pinInBackground(new SaveCallback() {
    @Override
    public done(ParseException e) {
        // ...
    }
});
```

After:

```java
ParseObservable.pin(user)
    .subscribe(user -> System.out.println(user));
```

### pin List

Before:

```java
ParseObject.pinAllInBackground(users, new SaveCallback() {
    @Override
    public done(ParseException e) {
        // ...
    }
});
```

After:

```java
ParseObservable.pin(users)
    .subscribe(user -> System.out.println(user));
```

### save

Before:

```java
user.saveInBackground(new SaveCallback() {
    @Override
    public done(ParseException e) {
        // ...
    }
});
```

After:

```java
ParseObservable.save(user)
    .subscribe(user -> System.out.println(user));
```
