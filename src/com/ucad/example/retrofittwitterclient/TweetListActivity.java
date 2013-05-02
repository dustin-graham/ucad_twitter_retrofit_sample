package com.ucad.example.retrofittwitterclient;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Callback;
import retrofit.http.GET;
import retrofit.http.Name;
import retrofit.http.RetrofitError;
import retrofit.http.client.Response;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

public class TweetListActivity extends ListActivity {

    private List<String> tweetTextList = new ArrayList<String>();
    private ArrayAdapter<String> mTweetAdapter;
    private static final String TWITTER_USER = "twitterapi";

    private static final int ASYNC_TASK_STYLE = 0;
    private static final int CALLBACK_STYLE = 1;
    private int mRetrofitUsageStyle = CALLBACK_STYLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mTweetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tweetTextList);
	setListAdapter(mTweetAdapter);

	if (mRetrofitUsageStyle == ASYNC_TASK_STYLE) {
	    TweetTask tweetTask = new TweetTask();
	    tweetTask.execute(TWITTER_USER);
	} else {
	    TwitterClient tweetClient = ServiceClient.getInstance().getClient(this, TwitterClient.class);
	    tweetClient.getUserTweetsAsync(TWITTER_USER, "1", new Callback<List<Tweet>>() {

		@Override
		public void success(List<Tweet> tweetList, Response response) {
		    tweetTextList.clear();
		    for (Tweet tweet : tweetList) {
			tweetTextList.add("Created On: " + tweet.createdAt + " text: " + tweet.text);
		    }
		    mTweetAdapter.notifyDataSetChanged();
		}

		@Override
		public void failure(RetrofitError error) {
		    Toast.makeText(TweetListActivity.this, "Failed to retrieve the user's tweets.",
			    Toast.LENGTH_LONG).show();
		}
	    });
	}
    }
    
    private List<Tweet> getUserTweets(ServiceClient serviceClient, String userName) {
	TwitterClient client = serviceClient.getClient(this, TwitterClient.class);
	List<Tweet> tweets = client.getUserTweets(userName, "1");
	return tweets;
    }

    private class TweetTask extends AsyncTask<String, Void, List<Tweet>> {

	@Override
	protected List<Tweet> doInBackground(String... params) {
	    if (params.length > 0) {
		String userName = params[0];
		List<Tweet> tweets = getUserTweets(ServiceClient.getInstance(), userName);
		return tweets;
	    }
	    return null;
	}

	@Override
	protected void onPostExecute(List<Tweet> result) {
	    super.onPostExecute(result);
	    tweetTextList.clear();
	    for (Tweet t : result) {
		tweetTextList.add("Created On: " + t.createdAt + " text: " + t.text);
	    }
	    mTweetAdapter.notifyDataSetChanged();
	}
    }

    class Tweet {
	@SerializedName("created_at")
	String createdAt;
	String text;
	User user;
    }

    class User {
	int id;
    }

    interface TwitterClient {
	@GET("/statuses/user_timeline.json")
	List<Tweet> getUserTweets(@Name("screen_name") String screenName, @Name("trim_user") String trimUser);

	@GET("/statuses/user_timeline.json")
	void getUserTweetsAsync(@Name("screen_name") String screenName, @Name("trim_user") String trimUser,
		Callback<List<Tweet>> callback);
    }

}
