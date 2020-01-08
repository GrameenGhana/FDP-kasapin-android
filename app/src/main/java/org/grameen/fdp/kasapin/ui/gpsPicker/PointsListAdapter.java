package org.grameen.fdp.kasapin.ui.gpsPicker;

/**
 * Created by AangJnr on 9/21/16.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.grameen.fdp.kasapin.R;

import java.util.List;


/**
 * Created by AangJnr on 6/27/16.
 */


public class PointsListAdapter extends RecyclerView.Adapter<PointsListAdapter.ViewHolder> {

    OnItemClickListener mItemClickListener;
    OnLongClickListener longClickListener;
    private List<LatLng> plots;
    private Context context;


    /**
     * Constructor
     *
     * @param plots
     **/

    public PointsListAdapter(Context context, List<LatLng> plots) {
        this.plots = plots;
        this.context = context;

    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return plots.size();

    }

    @Override
    public PointsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.point_item_view, viewGroup, false);

        return new PointsListAdapter.ViewHolder(v);


    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        LatLng plot = plots.get(position);
        viewHolder.index.setText(position + 1 + ".");
        viewHolder.coords.setText(plot.latitude + ", " + plot.longitude);

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
        TextView coords;
        TextView delete;


        ViewHolder(final View itemView) {
            super(itemView);

            index = itemView.findViewById(R.id.index);
            coords = itemView.findViewById(R.id.coordinates);

            delete = itemView.findViewById(R.id.delete);


            delete.setOnClickListener(v -> {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(delete, getAdapterPosition());
            });
        }


    }


}
