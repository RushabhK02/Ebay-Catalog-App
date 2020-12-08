package ebay_app.ebay_product_search.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import edu.gyaneshm.ebay_product_search.R;
import ebay_app.ebay_product_search.listeners.ItemClickListener;
import ebay_app.ebay_product_search.models.SearchResultModel;
import ebay_app.ebay_product_search.shared.Utils;

public class SearchItemRecyclerViewAdapter extends RecyclerView.Adapter<SearchItemRecyclerViewAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<SearchResultModel> mSearchResults;
    private ItemClickListener mItemClickListener;

    public SearchItemRecyclerViewAdapter(Context context, ArrayList<SearchResultModel> searchResults, ItemClickListener itemClickListener) {
        mContext = context;
        mSearchResults = searchResults;
        mItemClickListener = itemClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.search_item_card_view, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        SearchResultModel searchResult = mSearchResults.get(position);

        String title = searchResult.getTitle();
        if (title == null) {
            holder.mTitleTextView.setText(mContext.getString(R.string.n_a));
        } else {
            holder.mTitleTextView.setText(title);
        }

        Double price = searchResult.getPrice();
        if (price == null) {
            holder.mPriceTextView.setText(mContext.getString(R.string.n_a));
        } else {
            holder.mPriceTextView.setText(mContext.getString(R.string.price, Utils.formatPriceToString(price)));
        }

        String condition = searchResult.getCondition();
        if (condition == null) {
            holder.mConditionTextView.setText(mContext.getString(R.string.n_a));
        } else {
            holder.mConditionTextView.setText(condition);
        }

        Double shipping = Double.parseDouble(searchResult.getShippingInfo());
        if (shipping == 0) {
            String text = "<b>FREE</b> Shipping";
            holder.mShippingTextView.setText(Html.fromHtml(text));
        } else {
            String text = "Ships for <b>$"+ shipping.toString()+"</b>";
            holder.mShippingTextView.setText(Html.fromHtml(text));
        }

        String itemUrl = searchResult.getImage();
        itemUrl = itemUrl.equalsIgnoreCase("https://thumbs1.ebaystatic.com/pict/04040_0.jpg")?"":itemUrl;
        if (itemUrl == null || itemUrl=="") {
            Glide.with(mContext).clear(holder.mItemImageView);
            holder.mItemImageView.setImageDrawable(mContext.getDrawable(R.drawable.ebay_default));
        } else {
            Glide.with(mContext)
                    .load(itemUrl)
                    .into(holder.mItemImageView);
        }

        String topRated = searchResult.getTopRated();
        if(topRated.equalsIgnoreCase("true")){
            holder.mTopRatedTextView.setVisibility(View.VISIBLE);
        } else holder.mTopRatedTextView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mSearchResults.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView;
        TextView mPriceTextView;
        TextView mShippingTextView;
        TextView mConditionTextView;
        TextView mTopRatedTextView;
        ImageView mItemImageView;

        ItemViewHolder(View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.card_item_product_title);
            mPriceTextView = itemView.findViewById(R.id.card_item_product_price);
            mShippingTextView = itemView.findViewById(R.id.card_item_product_shipping);
            mTopRatedTextView = itemView.findViewById(R.id.card_item_product_top);
            mConditionTextView = itemView.findViewById(R.id.card_item_product_condition);
            mItemImageView = itemView.findViewById(R.id.card_item_product_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClicked(getAdapterPosition());
                }
            });

        }
    }
}
