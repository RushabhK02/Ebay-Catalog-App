package ebay_app.ebay_product_search.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import ebay_app.ebay_product_search.shared.Utils;

public class SearchResultModel {
    private String topRated;
    private String image;
    private String title;
    private String productId;
    private String condition;
    private Double price;
    private String shippingInfo;
    private HashMap<String, String> shipping = new HashMap<>();

    public HashMap<String, String> getShipping() {
        return shipping;
    }

    public SearchResultModel(JSONObject data) {
        try {
            image = Utils.optString(data, "image");
            title = Utils.optString(data, "title");
            productId = Utils.optString(data, "productId");
            condition = Utils.optString(data, "condition");
            shippingInfo = Utils.optString(data, "shippingInfo");
            topRated = Utils.optString(data, "topRated");
            price = Utils.optDouble(data, "price");

            JSONArray specifics = Utils.optJSONArray(data, "shipping");
            for (int i = 0; i < specifics.length(); i++) {
                JSONObject item = specifics.getJSONObject(i);
                shipping.put(Utils.optString(item, "name"), Utils.optString(item, "value"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("topRated", topRated);
            obj.put("image", image);
            obj.put("title", title);
            obj.put("productId", productId);
            obj.put("condition", condition);
            obj.put("price", price);
            obj.put("shippingInfo", shippingInfo);
        } catch (Exception ex) {
        }
        return obj;
    }

    public String getTopRated() {return topRated; }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getProductId() {
        return productId;
    }

    public String getCondition() {
        return condition;
    }

    public Double getPrice() {
        return price;
    }

    public String getShippingInfo() { return shippingInfo; }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("shippingInfo: ");
        str.append(shippingInfo);
        str.append(", ");
        str.append("image: ");
        str.append(image);
        str.append(", ");
        str.append("title: ");
        str.append(title);
        str.append(", ");
        str.append("productId: ");
        str.append(productId);
        str.append(", ");
        str.append("price: ");
        str.append(price);
        str.append(", ");
        str.append("topRated: ");
        str.append(topRated);
        str.append(", ");
        return str.toString();
    }
}
