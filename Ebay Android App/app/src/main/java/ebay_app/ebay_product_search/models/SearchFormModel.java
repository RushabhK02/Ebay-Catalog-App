package ebay_app.ebay_product_search.models;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SearchFormModel {
    private String keyword;
    private String category;
    private HashSet<String> condition;
    private HashMap<String, String> priceRange;

    public String toQueryParams() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("keyword="+keyword);
        stringBuilder.append("&sort="+category);
        if(!priceRange.isEmpty()){
            for (Map.Entry<String,String> rule : priceRange.entrySet()) {
                if(!rule.getValue().equalsIgnoreCase(""))
                    stringBuilder.append("&"+rule.getKey()+"="+rule.getValue());
            }
        }
        if(!condition.isEmpty()) {
            for(String value: condition){
                stringBuilder.append("&condition="+value);
            }
        }
        return stringBuilder.toString();
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCondition(HashMap<String, Boolean> condition) {
        HashSet<String> conditionKeys = new HashSet<>();
        for (Map.Entry<String, Boolean> row: condition.entrySet()){
            if(row.getValue()){
                conditionKeys.add(row.getKey());
            }
        }
        this.condition = conditionKeys;
    }

    public void setPriceRange(HashMap<String, String> priceRange) {
        this.priceRange = priceRange;
    }

    public String getKeyword() {
        return keyword;
    }
}
