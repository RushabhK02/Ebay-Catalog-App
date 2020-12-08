package ebay_app.ebay_product_search.data;

import android.support.v7.app.AppCompatActivity;

import edu.gyaneshm.ebay_product_search.R;

public class AppConfig {
    public static String getApiEndPoint(AppCompatActivity activity) {
        return activity.getResources().getString(R.string.server_root);
    }

    public static String getFacebookAppId() {
        return "";
    }

    public static String getFacebookRedirectUri() {
        return "";
    }
}
