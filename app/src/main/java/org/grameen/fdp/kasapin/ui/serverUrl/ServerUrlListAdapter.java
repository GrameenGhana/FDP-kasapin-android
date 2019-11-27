package org.grameen.fdp.kasapin.ui.serverUrl;

/**
 * Created by AangJnr on 9/21/16.
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;

import java.util.List;


/**
 * Created by AangJnr on 6/27/16.
 */


public class ServerUrlListAdapter extends RecyclerView.Adapter<ServerUrlListAdapter.ViewHolder> {

    private OnItemClickListener mItemClickListener;
    private OnDeleteClickListener mDeleteClickListener;
    private OnLongClickListener longClickListener;
    private List<ServerUrl> urls;
    private Context context;
    private String currentUrl = null;

    /**
     * Constructor
     *
     * @param serverUrls
     **/

    ServerUrlListAdapter(Context context, List<ServerUrl> serverUrls, String _currentUrl) {
        this.urls = serverUrls;
        this.context = context;
        this.currentUrl = _currentUrl;

    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return urls.size();

    }

    @NonNull
    @Override
    public ServerUrlListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.server_url_item_view, viewGroup, false);

        return new ServerUrlListAdapter.ViewHolder(v);


    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ServerUrl url = urls.get(position);


        viewHolder.radioButton.setChecked(url.getUrl().equals(currentUrl));
        if(!TextUtils.isEmpty(url.getName()))
        viewHolder.name.setText(url.getName());
        else
            viewHolder.name.setVisibility(View.GONE);

        viewHolder.url.setText(url.getUrl());


    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setOnItemClickListener(final ServerUrlListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setOnDeleteClickListener(final ServerUrlListAdapter.OnDeleteClickListener mItemClickListener) {
        this.mDeleteClickListener = mItemClickListener;
    }

    public void OnLongClickListener(final ServerUrlListAdapter.OnLongClickListener mLongClickListener) {
        this.longClickListener = mLongClickListener;
    }

    public void remove(int position) {
        urls.remove(position);
        notifyDataSetChanged();

    }

    public void add(ServerUrl url) {
        urls.add(url);
        notifyDataSetChanged();
    }

    public void setCurrentUrl(String url) {
       currentUrl = url;
       notifyDataSetChanged();
    }

    public List<ServerUrl> getUrlss() {
        return urls;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(View view, int position);
    }



    public interface OnLongClickListener {
        void onLongClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        RadioButton radioButton;
        TextView name;
        TextView url;
        TextView deleteTextView;


        ViewHolder(final View itemView) {
            super(itemView);

            radioButton = itemView.findViewById(R.id.radio);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);


            deleteTextView = itemView.findViewById(R.id.delete);


            deleteTextView.setOnClickListener(v -> {
                if (mDeleteClickListener != null)
                    mDeleteClickListener.onDeleteClick(deleteTextView, getAdapterPosition());
            });


            itemView.setOnClickListener(v -> {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(deleteTextView, getAdapterPosition());
            });
        }


    }


}
