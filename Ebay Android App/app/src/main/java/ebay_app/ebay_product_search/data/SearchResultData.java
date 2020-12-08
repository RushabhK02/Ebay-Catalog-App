package ebay_app.ebay_product_search.data;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ebay_app.ebay_product_search.EbayProductSearchApplication;
import edu.gyaneshm.ebay_product_search.R;
import ebay_app.ebay_product_search.models.SearchFormModel;
import ebay_app.ebay_product_search.models.SearchResultModel;
import ebay_app.ebay_product_search.listeners.DataFetchingListener;
import ebay_app.ebay_product_search.shared.Utils;

public class SearchResultData {
    private static SearchResultData mSearchResultData = null;

    private SearchFormModel searchFormData;

    private boolean dataFetched = false;
    private String errorMessage = null;
    private ArrayList<SearchResultModel> searchResults = new ArrayList<>();

    private ArrayList<DataFetchingListener> mCallbacks = new ArrayList<>();

    private RequestQueue mRequestQueue = null;

    private SearchResultData() {
    }

    public static SearchResultData getInstance() {
        if (mSearchResultData == null) {
            mSearchResultData = new SearchResultData();
        }
        return mSearchResultData;
    }

    public void setSearchFormData(SearchFormModel searchFormData) {
        this.searchFormData = searchFormData;
        errorMessage = null;
        dataFetched = false;
        searchResults.clear();
    }

    public void fetchData(AppCompatActivity activity) {
        String apiEndPoint = AppConfig.getApiEndPoint(activity) + "/items?";

        String finalUrl = apiEndPoint+searchFormData.toQueryParams();
        Log.i("Fetch URL",  finalUrl);
        dataFetched = false;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                finalUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        errorMessage = null;
                        if (response.length() == 0) {
                            errorMessage = "No Records.";
                        } else {
                            try {
                                JSONArray items = response.getJSONArray("products");
                                for (int i = 0; i < items.length(); i++) {

                                        JSONObject result = items.getJSONObject(i);
                                        SearchResultModel item = new SearchResultModel(result);
                                        searchResults.add(item);
                                }
                            } catch (Exception e) {
                                errorMessage = "No Records.";
                            }
                        }
                        dataFetched = true;
                        sendNotification();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            errorMessage = Utils.getString(R.string.not_connected_to_internet);
                        } else if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
                            errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        } else {
                            errorMessage = Utils.getString(R.string.search_results_no_records);
                        }

                        dataFetched = true;
                        sendNotification();
                    }
                }
        );
        jsonArrayRequest.setTag("results");

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(EbayProductSearchApplication.getInstance().getApplicationContext());
        }
        mRequestQueue.add(jsonArrayRequest);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ArrayList<SearchResultModel> getSearchResults() {
        return searchResults;
    }

    public SearchFormModel getSearchFormData() {
        return searchFormData;
    }

    public boolean isDataFetched() {
        return dataFetched;
    }

    public void registerCallback(DataFetchingListener callback) {
        mCallbacks.add(callback);
    }

    public void unregisterCallback(DataFetchingListener callback) {
        mCallbacks.remove(callback);
    }

    private void sendNotification() {
        for (int i = 0; i < mCallbacks.size(); i++) {
            mCallbacks.get(i).dataSuccessFullyFetched();
        }
    }

    public void cancelRequest() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll("results");
        }
    }
}
