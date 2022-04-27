package org.grameen.fdp.kasapin.ui.main;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import java.io.File;
import java.util.List;
import java.util.Random;

public class FarmerListViewAdapter extends ArrayAdapter<Farmer> {
    int[] mColors;
    Picasso picasso;
    private List<Farmer> farmers;
    private Context context;
    private LayoutInflater layoutInflater;
    private int lastPosition = -1;
    private OnItemClickListener mItemClickListener;
    private OnLongClickListener longClickListener;


    public FarmerListViewAdapter(Context c, List<Farmer> _farmers) {
        super(c, R.layout.farmer_grid_item_view, _farmers);
        this.context = c;
        this.farmers = _farmers;
        mColors = context.getResources().getIntArray(R.array.recommendations_colors);
        layoutInflater = LayoutInflater.from(context);
        picasso = Picasso.get();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Farmer realFarmer = farmers.get(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.farmer_grid_item_view, parent, false);
            viewHolder.mainLayout = convertView.findViewById(R.id.mainLayout);
            viewHolder.rl1 = convertView.findViewById(R.id.rl1);

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.code = convertView.findViewById(R.id.code);
            viewHolder.initials = convertView.findViewById(R.id.initials);
            viewHolder.photo = convertView.findViewById(R.id.photo);
            viewHolder.imageView = convertView.findViewById(R.id.image_view1);
            viewHolder.syncStatus = convertView.findViewById(R.id.sync_status);
            viewHolder.fdpStatus = convertView.findViewById(R.id.fdp_status);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        setData(realFarmer, viewHolder);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return farmers.get(position).getCode().hashCode();
    }

    private void setData(Farmer farmer, ViewHolder viewHolder) {
        if (farmer.getImageLocalUrl() != null) {
            picasso.load(new File(farmer.getImageLocalUrl()))
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.photo);
        } else {
            viewHolder.photo.setImageBitmap(null);

            try {
                String[] valueArray = farmer.getFarmerName().split(" ");
                String value = valueArray[0].substring(0, 1) + valueArray[1].substring(0, 1);
                viewHolder.initials.setText(value);
            } catch (Exception ignore) {
                if (!farmer.getFarmerName().trim().isEmpty())
                    viewHolder.initials.setText(farmer.getFarmerName().substring(0, 1));
            }

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

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void OnLongClickListener(final OnLongClickListener mLongClickListener) {
        this.longClickListener = mLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }


    public interface OnLongClickListener {
        void onLongClick(View view);
    }

    private static class ViewHolder {
        RelativeLayout mainLayout;
        RelativeLayout rl1;
        TextView name;
        TextView code;
        TextView initials;
        ImageView photo;
        ImageView imageView;
        ImageView syncStatus;
        ImageView fdpStatus;
    }
}
