package org.grameen.fdp.kasapin.ui.farmerProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Plot;

import java.util.List;

public class PlotsListAdapter extends RecyclerView.Adapter<PlotsListAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    OnLongClickListener longClickListener;
    private List<Plot> plots;

    /**
     * Constructor
     *
     * @param _plots .
     **/
    public PlotsListAdapter(Context context, List<Plot> _plots) {
        this.plots = _plots;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return plots.size();
    }

    @NonNull
    @Override
    public PlotsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.plot_item_view, viewGroup, false);
        return new PlotsListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Plot plot = plots.get(position);
        viewHolder.name.setText(plot.getName());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setOnItemClickListener(final PlotsListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void OnLongClickListener(final PlotsListAdapter.OnLongClickListener mLongClickListener) {
        this.longClickListener = mLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnLongClickListener {
        void onLongClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView name;

        ViewHolder(final View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            name = itemView.findViewById(R.id.name);
            mainLayout.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            mainLayout.setOnLongClickListener(view -> {
                if (longClickListener != null)
                    longClickListener.onLongClick(view, getAdapterPosition());
                return true;
            });
        }
    }
}
