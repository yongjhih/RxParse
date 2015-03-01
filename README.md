# RxParse

## Usage

```java
Observable<ParseUser> users = ParseObservable.from(ParseUser.class).find();
users.subscribe(user -> System.out.println(user.getObjectId()));
```

```java
Observable<ParseUser> users = ParseObservable.from(ParseUser.class).find(ParseUser.getQuery().setLimit(1000));
```

```java
Observable<Integer> count = ParseObservable.from(ParseUser.class).count();
count.subscirbe(c -> System.out.println(c));
```
