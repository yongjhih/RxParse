package rx.parse.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v4.app.NavUtils;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.StaggeredGridLayoutManager;
//import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.parse.*;

import rx.Observable;
import rx.android.app.*;
import rx.functions.*;

import rx.parse.*;

public class MainFragment extends Fragment {

    @InjectView(R.id.text_view)
    TextView textView;

    Handler handler;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        handler = new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        ParseObservable.count(ParseUser.getQuery())
            .subscribe(i -> {
                android.util.Log.d("RxParse", "" + i);
            });

        ParseObservable.find(ParseUser.getQuery())
            .doOnNext(user -> android.util.Log.d("RxParse", user.getObjectId()))
            .subscribe(user -> {
                android.util.Log.d("RxParse", user.getObjectId());
            });

        AppObservable.bindFragment(this, ParseObservable.find(ParseUser.getQuery()))
            .flatMap(user -> ParseObservable.get(ParseUser.class, user.getObjectId()))
            .subscribe(user -> {
                handler.post(() -> {
                    android.util.Log.d("RxParse", "textView: " + user.getObjectId());
                    textView.setText(user.getObjectId());
                });
            });
        */

        AppObservable.bindFragment(this, ParseObservable.find(ParseUser.getQuery()))
            .flatMap(new Func1<ParseUser, Observable<ParseUser>>() {
                    @Override public Observable<ParseUser> call(ParseUser user) {
                        return ParseObservable.get(ParseUser.class, user.getObjectId());
                    }
                })
            .subscribe(new Action1<ParseUser>() {
                @Override public void call(final ParseUser user) {
                    handler.post(new Runnable() {
                        @Override public void run() {
                            android.util.Log.d("RxParse", "textView: " + user.getObjectId());
                            textView.setText(user.getObjectId());
                        }
                    });
                }
            });
    }
}
