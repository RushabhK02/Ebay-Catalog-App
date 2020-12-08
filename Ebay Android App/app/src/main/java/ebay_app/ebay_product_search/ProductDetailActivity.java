package ebay_app.ebay_product_search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ebay_app.ebay_product_search.adapters.ViewPageAdapter;
import ebay_app.ebay_product_search.data.AppConfig;
import ebay_app.ebay_product_search.data.ProductData;
import ebay_app.ebay_product_search.fragments.DetailFragment;
import ebay_app.ebay_product_search.fragments.SellerFragment;
import ebay_app.ebay_product_search.fragments.ShippingFragment;
import ebay_app.ebay_product_search.models.ProductModel;
import ebay_app.ebay_product_search.models.SearchResultModel;
import ebay_app.ebay_product_search.shared.Utils;
import edu.gyaneshm.ebay_product_search.R;

public class ProductDetailActivity extends AppCompatActivity {
    private ActionBar mActionBar;
    private TabLayout mTabLayout;
    private ViewPageAdapter mViewPageAdapter;
    private ProductData mProductDataInstance;
    private SearchResultModel item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity_layout);

        mProductDataInstance = ProductData.getInstance();
        item = mProductDataInstance.getItem();

        mActionBar = getSupportActionBar();
        setUpActionBar();

        ViewPager viewPager = findViewById(R.id.product_container);
        setUpViewPager();
        viewPager.setAdapter(mViewPageAdapter);
        viewPager.setOffscreenPageLimit(2);

        mTabLayout = findViewById(R.id.product_tabs);
        mTabLayout.setupWithViewPager(viewPager);
        setTabIcons();
        initiateDataFetching();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProductDataInstance.cancelRequest();
    }

    private void setUpActionBar() {
        mActionBar.setTitle(item.getTitle());
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_data_menu, menu);
        MenuItem redirectItem = menu.getItem(0);
        redirectItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_link:
                shareFacebookPost();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setUpViewPager() {
        mViewPageAdapter = new ViewPageAdapter(this, getSupportFragmentManager());
        mViewPageAdapter.addFragment(new DetailFragment(), R.string.product_tab_title_product);
        mViewPageAdapter.addFragment(new SellerFragment(), R.string.product_tab_title_seller);
        mViewPageAdapter.addFragment(new ShippingFragment(), R.string.product_tab_title_shipping);
    }

    private void initiateDataFetching() {
        if (!mProductDataInstance.isProductDetailFetched()) {
            mProductDataInstance.fetchProductDetail(this);
        }
    }

    private void shareFacebookPost() {
        if (!mProductDataInstance.isProductDetailFetched()) {
            Utils.showToast(R.string.error_facebook_share_data_not_fetched);
            return;
        }
        if (mProductDataInstance.getProductDetailError() != null) {
            Utils.showToast(R.string.error_facebook_share_data_not_found);
            return;
        }
        ProductModel product = mProductDataInstance.getProductDetail();
        String productUrl = product.getProductUrl();
        if (productUrl == null) {
            productUrl = "https://www.ebay.com/";
        }

        Uri.Builder builder = Uri.parse(productUrl).buildUpon();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, builder.build());
        startActivity(browserIntent);
    }

    private void setTabIcons() {
        int[] icons = new int[]{
                R.drawable.information_variant_selector,
                R.drawable.ic_seller,
                R.drawable.truck_delivery_selector
        };

        for (int i = 0; i < icons.length; i++) {
            mTabLayout.getTabAt(i).setIcon(icons[i]);
        }
    }
}
