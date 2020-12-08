package ebay_app.ebay_product_search.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.HashMap;

import edu.gyaneshm.ebay_product_search.R;
import ebay_app.ebay_product_search.SearchResultActivity;
import ebay_app.ebay_product_search.data.SearchResultData;
import ebay_app.ebay_product_search.models.SearchFormModel;
import ebay_app.ebay_product_search.shared.Utils;

public class SearchFragment extends Fragment {
    private EditText mKeywordEditText;
    private TextView mKeywordErrorTextView;
    private Spinner mSortSpinner;

    private CheckBox mConditionNewCheckbox;
    private CheckBox mConditionUsedCheckbox;
    private CheckBox mConditionUnspecifiedCheckbox;

    private EditText minPrice;
    private EditText maxPrice;
    private TextView mPriceError;

    private Button mSearchButton;
    private Button mClearButton;

    private RequestQueue mRequestQueue = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_layout, container, false);

        mKeywordEditText = view.findViewById(R.id.search_keyword);
        mKeywordErrorTextView = view.findViewById(R.id.search_keyword_error);

        mSortSpinner = view.findViewById(R.id.search_sort);
        initializeSortSpinner();

        mConditionNewCheckbox = view.findViewById(R.id.search_condition_new);
        mConditionUsedCheckbox = view.findViewById(R.id.search_condition_used);
        mConditionUnspecifiedCheckbox = view.findViewById(R.id.search_condition_unspecified);

        minPrice = view.findViewById(R.id.search_minprice);
        maxPrice = view.findViewById(R.id.search_maxprice);
        mPriceError = view.findViewById(R.id.search_price_error);

        mSearchButton = view.findViewById(R.id.search_button);
        setUpSearchButton();
        mClearButton = view.findViewById(R.id.clear_button);
        setUpClearButton();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll("zipcode");
            mRequestQueue.cancelAll("current_zipcode");
        }
    }

    private void initializeSortSpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.search_sort_categories,
                android.R.layout.simple_spinner_dropdown_item
        );
        mSortSpinner.setAdapter(categoryAdapter);
        mSortSpinner.setSelection(0);
    }

    private void setUpSearchButton() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                String keyword = mKeywordEditText.getText().toString();
                if (
                        keyword.length() == 0 ||
                                keyword.matches("^ +$")
                ) {
                    valid = false;
                    mKeywordErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    mKeywordErrorTextView.setVisibility(View.GONE);
                }

                boolean priceError = false;
                String minprice = minPrice.getText().toString();
                if(!minprice.equalsIgnoreCase("") && !priceError){
                    Double value = Double.parseDouble(minprice);
                    if(value<0) {
                        valid = false;
                        priceError = true;
                        mPriceError.setVisibility(View.VISIBLE);
                    } else {
                        mPriceError.setVisibility(View.GONE);
                    }
                }

                String maxprice = maxPrice.getText().toString();
                if(!maxprice.equalsIgnoreCase("") && !priceError){
                    Double value = Double.parseDouble(maxprice);
                    if(value<0) {
                        valid = false;
                        mPriceError.setVisibility(View.VISIBLE);
                        priceError = true;
                    } else {
                        mPriceError.setVisibility(View.GONE);
                    }
                }

                if(!priceError && !maxprice.equalsIgnoreCase("") && !minprice.equalsIgnoreCase("")){
                    Double value = Double.parseDouble(maxprice);
                    Double value2 = Double.parseDouble(minprice);
                    if(value<value2) {
                        valid = false;
                        mPriceError.setVisibility(View.VISIBLE);
                        priceError = true;
                    } else {
                        mPriceError.setVisibility(View.GONE);
                    }
                }

                if (!valid) {
                    Utils.showToast(R.string.please_fix_all_fields_with_error);
                    return;
                }
                else {
                    launchSearchResultsActivity();
                }
            }
        });

    }

    private void setUpClearButton() {
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeywordEditText.setText("");
                mKeywordErrorTextView.setVisibility(View.GONE);

                mSortSpinner.setSelection(0);

                mConditionNewCheckbox.setChecked(false);
                mConditionUsedCheckbox.setChecked(false);
                mConditionUnspecifiedCheckbox.setChecked(false);

                minPrice.setText("");
                maxPrice.setText("");
                mPriceError.setVisibility(View.GONE);
            }
        });
    }

    private void launchSearchResultsActivity() {
        SearchFormModel searchFormData = new SearchFormModel();

        searchFormData.setKeyword(mKeywordEditText.getText().toString());

        int categoryPosition = mSortSpinner.getSelectedItemPosition();
        searchFormData.setCategory(getResources().getStringArray(R.array.search_sort_categories_id)[categoryPosition]);

        HashMap<String, Boolean> condition = new HashMap<>();
        condition.put("New", mConditionNewCheckbox.isChecked());
        condition.put("Used", mConditionUsedCheckbox.isChecked());
        condition.put("Unspecified", mConditionUnspecifiedCheckbox.isChecked());
        searchFormData.setCondition(condition);

        HashMap<String, String> priceRange = new HashMap<>();
        priceRange.put("minprice", minPrice.getText().toString());
        priceRange.put("maxprice", maxPrice.getText().toString());
        searchFormData.setPriceRange(priceRange);

        SearchResultData searchResultData = SearchResultData.getInstance();
        searchResultData.setSearchFormData(searchFormData);

        Intent intent = new Intent(getContext(), SearchResultActivity.class);
        startActivity(intent);
    }
}
