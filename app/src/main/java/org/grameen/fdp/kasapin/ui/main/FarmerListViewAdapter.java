package org.grameen.fdp.kasapin.ui.main;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ImageUtil;
import org.grameen.fdp.kasapin.utilities.ScreenUtils;

import java.util.List;
import java.util.Random;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.CURRENT_PAGE;


/**
 * Created by AangJnr on 14, December, 2018 @ 12:22 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmerListViewAdapter extends ArrayAdapter<RealFarmer> {

    private List<RealFarmer> farmers;
    private Context context;
    private LayoutInflater layoutInflater;
    private int lastPosition = -1;
    private OnItemClickListener mItemClickListener;
    private OnLongClickListener longClickListener;




    public FarmerListViewAdapter(Context c, List<RealFarmer> _farmers){
        super(c, R.layout.farmer_grid_item_view, _farmers);

        this.context = c;
        this.farmers = _farmers;

        layoutInflater = LayoutInflater.from(context);
    }


    private static class ViewHolder{

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


    @NonNull
    @Override
    public View getView(int position, @Nullable  View convertView, @NonNull ViewGroup parent) {

        RealFarmer realFarmer = farmers.get(position);
        View view;

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.farmer_grid_item_view, parent, false);


            viewHolder.mainLayout =  convertView.findViewById(R.id.mainLayout);
            viewHolder.rl1 = convertView.findViewById(R.id.rl1);

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.code = convertView.findViewById(R.id.code);
            viewHolder.initials = convertView.findViewById(R.id.initials);
            viewHolder.photo = convertView.findViewById(R.id.photo);
            viewHolder.imageView = convertView.findViewById(R.id.image_view1);

            viewHolder.syncStatus = convertView.findViewById(R.id.sync_status);
            viewHolder.fdpStatus = convertView.findViewById(R.id.fdp_status);


            view = convertView;
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            view = convertView;

        }


        Animation animation;


        if(CURRENT_PAGE == 0) {

            if (ScreenUtils.isTablet((AppCompatActivity) context))
                animation = AnimationUtils.loadAnimation(context,
                        (position % AppConstants.TABLET_COLUMN_COUNT - 1 < AppConstants.TABLET_COLUMN_COUNT) ? R.anim.up_from_bottom : R.anim.down_from_top);
            else
                animation = AnimationUtils.loadAnimation(context,
                        (position % AppConstants.PHONE_COLUMN_COUNT - 1 < AppConstants.PHONE_COLUMN_COUNT) ? R.anim.up_from_bottom : R.anim.down_from_top);

            view.startAnimation(animation);
        }
        lastPosition = position;


        setData(realFarmer, viewHolder);

        return convertView;
    }


    private void setData(RealFarmer farmer, ViewHolder viewHolder){

        if (farmer.getImageUrl() != null && !farmer.getImageUrl().equals("")){
            viewHolder.photo.setImageBitmap(ImageUtil.base64ToScaledBitmap(farmer.getImageUrl()));
            //Picasso.with(context).load(farmer.getImageUrl()).resize(200, 200).into(viewHolder.photo);
           // viewHolder.setIsRecyclable(false);
        } else {
            //viewHolder.setIsRecyclable(true);

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


             if(farmer.getSyncStatus() == 1) {
                viewHolder.syncStatus.setImageResource(R.drawable.ic_sync_black_18dp);
                viewHolder.syncStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            }else if(farmer.getSyncStatus() == 0) {
                viewHolder.syncStatus.setImageResource(R.drawable.ic_sync_problem_black_18dp);
                viewHolder.syncStatus.setColorFilter(ContextCompat.getColor(context, R.color.cpb_red));
        }


        if (farmer.getHasSubmitted() != null) {
            if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.FARMER_SUBMITTED_YES)) {
                viewHolder.fdpStatus.setImageResource(R.drawable.ic_check_circle_black_18dp);
                viewHolder.fdpStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }

       // viewHolder.mainLayout.setOnClickListener(this);
        //viewHolder.mainLayout.setOnLongClickListener(this);
    }


/*

    @Override
    public void onClick(View view) {



        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(view);
        }

    }

    @Override
    public boolean onLongClick(View view) {

        if(longClickListener != null)
        longClickListener.onLongClick(view);




        return false;
    }
*/



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
}
