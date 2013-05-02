package com.ucad.example.retrofittwitterclient;

import java.util.HashMap;
import java.util.Map;

import retrofit.http.RestAdapter;
import retrofit.http.Server;
import android.content.Context;

public class ServiceClient {
    private static ServiceClient instance;
    public static final String BASE_URL_PROD = "http://api.twitter.com/1";
    public static final String BASE_URL_TEST = "http://api.twitter.com/1";
    public static final String BASE_URL_DEV = "http://api.twitter.com/1";

    private RestAdapter mRestAdapter;
    private Map<String, Object> mClients = new HashMap<String, Object>();

    private String mBaseUrl = BASE_URL_PROD;

    private ServiceClient() {
    }

    public static ServiceClient getInstance() {
	if (null == instance) {
	    instance = new ServiceClient();
	}
	return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getClient(Context context, Class<T> clazz) {
	if (mRestAdapter == null) {
	    mRestAdapter = new RestAdapter.Builder().setServer(new Server(getBaseUrl(context))).build();
	}
	T client = null;
	if ((client = (T) mClients.get(clazz.getCanonicalName())) != null) {
	    return client;
	}
	client = mRestAdapter.create(clazz);
	mClients.put(clazz.getCanonicalName(), client);
	return client;
    }

    public void setRestAdapter(RestAdapter restAdapter) {
	mRestAdapter = restAdapter;
    }

    public String getBaseUrl(Context context) {
	// TODO: switch base url by some sort of settings logic
	return mBaseUrl;
    }
}
