package adarsh.helloworld.tictactoe;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static adarsh.helloworld.tictactoe.PaginationScrollListener.PAGE_START;

public class PopupActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "PopupActivity";
    private final int totalPage = 10;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    int itemCount = 0;
    private UserRecyclerAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
        ButterKnife.bind(this);

        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new UserRecyclerAdapter(new ArrayList<User>());
        mRecyclerView.setAdapter(adapter);
        doApiCall();
        /**
         * add scroll listener while user reach in bottom load more will call
         */
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    /**
     * do api call here to fetch data from server
     * In example i'm adding data manually
     */
    private void doApiCall() {
        final ArrayList<User> items = new ArrayList<>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    itemCount++;
                    User userItem = new User();
                    userItem.setFullName(getString(R.string.enter_fullname) + itemCount);
                    userItem.setEmail(getString(R.string.enter_email));
                    items.add(userItem);
                }
                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);
                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 1500);
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        doApiCall();
    }
}
