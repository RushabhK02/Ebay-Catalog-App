package ebay_app.ebay_product_search.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

import ebay_app.ebay_product_search.data.ProductData;
import ebay_app.ebay_product_search.listeners.DataFetchingListener;
import ebay_app.ebay_product_search.models.ProductModel;
import ebay_app.ebay_product_search.models.SearchResultModel;
import ebay_app.ebay_product_search.shared.Utils;
import edu.gyaneshm.ebay_product_search.R;

public class SellerFragment extends Fragment {
    private LinearLayout mProgressBarContainer;
    private ProductData mProductDataInstance;
    private ProductModel productDetail;
    private SearchResultModel searchResultItem;
    private ScrollView mMainContainer;
    private TextView sellerView;
    private TextView returnView;

    private TextView mErrorTextView;
    private boolean isFetched = false;

    private DataFetchingListener dataFetchingListener = new DataFetchingListener() {
        @Override
        public void dataSuccessFullyFetched() {
            if (!isFetched) {
                initiate();
            }
        }
    };

    public SellerFragment() {
        // Required empty public constructor
    }

    public static SellerFragment newInstance(String param1, String param2) {
        SellerFragment fragment = new SellerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seller_fragment_layout, container, false);
        mMainContainer = view.findViewById(R.id.main_container);
        mProgressBarContainer = view.findViewById(R.id.progress_bar_container);
        mErrorTextView = view.findViewById(R.id.error);
        sellerView = view.findViewById(R.id.seller_info);
        returnView = view.findViewById(R.id.return_info);

        mProductDataInstance = ProductData.getInstance();
        mProductDataInstance.registerCallback(dataFetchingListener);
        initiate();
        return view;
    }

    private void initiate() {
        mProgressBarContainer.setVisibility(View.GONE);
        mMainContainer.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.GONE);

        isFetched = mProductDataInstance.isProductDetailFetched();
        if (isFetched) {
            checkError();
        } else {
            mProgressBarContainer.setVisibility(View.VISIBLE);
        }
    }

    private void checkError() {
        String errorMessage = mProductDataInstance.getProductDetailError();
        if (errorMessage == null) {
            setUpViews();
            return;
        }
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void setUpViews() {
        productDetail = mProductDataInstance.getProductDetail();
        searchResultItem = mProductDataInstance.getItem();

        setUpSellerSection();
        setUpReturnSection();
        mMainContainer.setVisibility(View.VISIBLE);
    }

    private void setUpReturnSection() {
        boolean sectionValid = false;

        StringBuilder str = new StringBuilder();
        HashMap<String, String> itemSpecifics = productDetail.getReturnInfo();

        int count = 5;
        for(HashMap.Entry<String, String> entry : itemSpecifics.entrySet()) {
//            if(count==0) break;
            String key = entry.getKey();
            String[] words = key.split("(?=\\p{Upper})");
            String line = String.join(" ", words);
            str.append("&#8226; ");
            str.append("<b>"+Utils.capitalizeFirstCharacter(line)+"</b> : ");
            String value = entry.getValue().equalsIgnoreCase("true")?"Yes":entry.getValue();
            value = value.equalsIgnoreCase("false")?"No":value;

            str.append(Utils.capitalizeFirstCharacter(value));
            str.append("<br><br>");
            sectionValid = true;
//            count--;
        }

        if(sectionValid) {
            String finalStr = str.toString().trim();
            returnView.setText(Html.fromHtml(finalStr, Html.FROM_HTML_MODE_LEGACY));
//            mSpecificationsSectionLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpSellerSection() {
        boolean sectionValid = false;

        StringBuilder str = new StringBuilder();
        HashMap<String, String> itemSpecifics = productDetail.getSeller();

        int count = 5;
        for(HashMap.Entry<String, String> entry : itemSpecifics.entrySet()) {
//            if(count==0) break;
            String key = entry.getKey();
            String[] words = key.split("(?=\\p{Upper})");
            String line = String.join(" ", words);
            str.append("&#8226; ");
            str.append("<b>"+Utils.capitalizeFirstCharacter(line)+"</b> : ");
            String value = entry.getValue().equalsIgnoreCase("true")?"Yes":entry.getValue();
            value = value.equalsIgnoreCase("false")?"No":value;
            str.append(Utils.capitalizeFirstCharacter(value));
            str.append("<br><br>");
            sectionValid = true;
//            count--;
        }

        if(sectionValid) {
            String finalStr = str.toString().trim();
            sellerView.setText(Html.fromHtml(finalStr, Html.FROM_HTML_MODE_LEGACY));
//            mSpecificationsSectionLinearLayout.setVisibility(View.VISIBLE);
        }
    }
}
