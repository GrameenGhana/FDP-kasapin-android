package org.grameen.fdp.kasapin.ui.gpsPicker;

/**
 * Created by AangJnr on 9/21/16.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.grameen.fdp.kasapin.R;

import java.util.List;

public class PointsListAdapter extends RecyclerView.Adapter<PointsListAdapter.ViewHolder> {

    OnItemClickListener mItemClickListener;
    OnLongClickListener longClickListener;
    private List<LatLng> plots;

    /**
     * Constructor
     *
     * @param plots .
     **/
    public PointsListAdapter(Context context, List<LatLng> plots) {
        this.plots = plots;
    }

    /**
     * @param position position of the itemView
     * @return position
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return plots.size();
    }

    @NonNull
    @Override
    public PointsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.point_item_view, viewGroup, false);
        return new PointsListAdapter.ViewHolder(v);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        LatLng plot = plots.get(position);
        viewHolder.index.setText(String.format("%d.", position + 1));
        viewHolder.coordinates.setText(String.format("%s, %s", plot.latitude, plot.longitude));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setOnItemClickListener(final PointsListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void OnLongClickListener(final PointsListAdapter.OnLongClickListener mLongClickListener) {
        this.longClickListener = mLongClickListener;
    }

    public void removePoint(int position) {
        plots.remove(position);
        notifyDataSetChanged();
    }

    public void addPoint(LatLng latLng) {
        plots.add(latLng);
        notifyDataSetChanged();
    }

    public List<LatLng> getPoints() {
        return plots;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnLongClickListener {
        void onLongClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView coordinates;
        TextView delete;

        ViewHolder(final View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            coordinates = itemView.findViewById(R.id.coordinates);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(delete, getAdapterPosition());
            });
        }
    }
}
