# RxParse

## Usage

```java
Observable<ParseUser> users = ParseObservable.from(ParseUser.class).find();
users.subscribe(user -> System.out.println(user.getObjectId()));
```

```java
Observable<ParseUser> posts = ParseObservable.from(Post.class).find();
posts.subscribe(p -> System.out.println(p.getObjectId()));
```
