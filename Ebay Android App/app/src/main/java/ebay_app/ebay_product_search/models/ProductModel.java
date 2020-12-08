package ebay_app.ebay_product_search.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ebay_app.ebay_product_search.shared.Utils;

public class ProductModel {
    private String title;
    private String productId;
    private String productUrl;
    private ArrayList<String> productImages = new ArrayList<>();
    private String subtitle;
    private Double price;
    private HashMap<String, String> seller;
    private HashMap<String, String> returnInfo;
    private HashMap<String, String> itemSpecifics = new HashMap<>();

    public ProductModel(JSONObject data) {
        try {
            title = Utils.optString(data, "title");
            productId = Utils.optString(data, "productId");
            productUrl = Utils.optString(data, "productUrl");
            JSONArray images = Utils.optJSONArray(data, "productImages");
            for (int i = 0; i < images.length(); i++) {
                productImages.add(images.getString(i));
            }
            JSONObject sellerInfo = data.getJSONObject("seller");
            seller = Utils.toMap(sellerInfo);

            JSONObject returnData = data.getJSONObject("returnPolicy");
            returnInfo = Utils.toMap(returnData);

            subtitle = Utils.optString(data, "subtitle");
            price = Utils.optDouble(data, "price");
            JSONArray specifics = Utils.optJSONArray(data, "ItemSpecifics");
            for (int i = 0; i < specifics.length(); i++) {
                JSONObject item = specifics.getJSONObject(i);
                itemSpecifics.put(Utils.optString(item, "name"), Utils.optString(item, "value"));
            }
        } catch (Exception ex) {
        }
    }

    public String getTitle() {
        return title;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Double getPrice() {
        return price;
    }

    public HashMap<String, String> getItemSpecifics() {
        return itemSpecifics;
    }

    public HashMap<String, String> getSeller() {
        return seller;
    }

    public HashMap<String, String> getReturnInfo() {
        return returnInfo;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("title: ");
        str.append(title);
        str.append(", ");
        str.append("productId: ");
        str.append(productId);
        str.append(", ");
        str.append("productUrl: ");
        str.append(productUrl);
        str.append(", ");
        str.append("subtitle: ");
        str.append(subtitle);
        str.append(", ");
        str.append("price: ");
        str.append(price);
        str.append(", ");
        str.append("productImages: ");
        str.append(productImages.toString());
        str.append(", ");
        str.append("itemSpecifics: ");
        str.append(itemSpecifics.toString());
        return str.toString();
    }
}
