package org.grameen.fdp.kasapin.ui.main;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.ImageUtil;

import java.util.List;
import java.util.Random;

public class FarmerListRecyclerViewAdapter extends RecyclerView.Adapter<FarmerListRecyclerViewAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    OnLongClickListener longClickListener;
    private List<Farmer> farmers;
    private Context context;

    /**
     * Constructor
     *
     * @param farmers .
     **/
    FarmerListRecyclerViewAdapter(Context context, List<Farmer> farmers) {
        this.farmers = farmers;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return farmers.size();
    }

    @NonNull
    @Override
    public FarmerListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.farmer_item_view, viewGroup, false);
        return new FarmerListRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Farmer farmer = farmers.get(position);
        if (farmer.getImageUrl() != null && !farmer.getImageUrl().equals("")) {
            viewHolder.photo.setImageBitmap(ImageUtil.base64ToScaledBitmap(farmer.getImageUrl()));
            viewHolder.setIsRecyclable(false);
        } else {
            viewHolder.setIsRecyclable(true);
            try {
                String[] valueArray = farmer.getFarmerName().split(" ");
                String value = valueArray[0].substring(0, 1) + valueArray[1].substring(0, 1);
                viewHolder.initials.setText(value);
            } catch (Exception e) {
                if (!farmer.getFarmerName().trim().isEmpty())
                    viewHolder.initials.setText(farmer.getFarmerName().substring(0, 1));
            }

            int[] mColors = context.getResources().getIntArray(R.array.recommendations_colors);
            int randomColor = mColors[new Random().nextInt(mColors.length)];
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(0);
            drawable.setColor(randomColor);
            viewHolder.rl1.setBackground(drawable);
            viewHolder.imageView.setVisibility(View.GONE);
        }

        viewHolder.name.setText(farmer.getFarmerName());
        viewHolder.code.setText(farmer.getCode());

        if (farmer.getSyncStatus() == 1) {
            viewHolder.syncStatus.setImageResource(R.drawable.ic_sync_black_18dp);
            viewHolder.syncStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
        } else if (farmer.getSyncStatus() == 0) {
            viewHolder.syncStatus.setImageResource(R.drawable.ic_sync_problem_black_18dp);
            viewHolder.syncStatus.setColorFilter(ContextCompat.getColor(context, R.color.cpb_red));
        }

        if (farmer.getHasSubmitted() != null) {
            if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.FARMER_SUBMITTED_YES)) {
                viewHolder.fdpStatus.setImageResource(R.drawable.ic_check_circle_black_18dp);
                viewHolder.fdpStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(final FarmerListRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void OnLongClickListener(final FarmerListRecyclerViewAdapter.OnLongClickListener mLongClickListener) {
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
        RelativeLayout rl1;
        TextView name;
        TextView code;
        TextView initials;
        ImageView photo;
        ImageView imageView;
        ImageView syncStatus;
        ImageView fdpStatus;

        ViewHolder(final View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            rl1 = itemView.findViewById(R.id.rl1);
            name = itemView.findViewById(R.id.name);
            code = itemView.findViewById(R.id.code);
            initials = itemView.findViewById(R.id.initials);
            photo = itemView.findViewById(R.id.photo);
            imageView = itemView.findViewById(R.id.image_view1);
            syncStatus = itemView.findViewById(R.id.sync_status);
            fdpStatus = itemView.findViewById(R.id.fdp_status);

            mainLayout.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            mainLayout.setOnLongClickListener(view -> {
                longClickListener.onLongClick(view, getAdapterPosition());
                return true;
            });
        }
    }
}
