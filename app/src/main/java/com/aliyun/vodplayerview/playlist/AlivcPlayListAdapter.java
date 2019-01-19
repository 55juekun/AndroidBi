package com.aliyun.vodplayerview.playlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.vodplayerview.playlist.AlivcVideoInfo.Video;
import com.aliyun.vodplayerview.utils.Formatter;
import com.bi.R;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mulberry
 *         create on 2018/5/17.
 */

public class AlivcPlayListAdapter extends RecyclerView.Adapter<AlivcPlayListAdapter.ViewHolder>{
    ArrayList<Video> videoLists;
    HashMap<Integer,Boolean> isChecked=new HashMap<>();
    WeakReference<Context> context;
    boolean showCheck=false;
    int CHECKALL=1;
    int CHECKNONE=-1;
    int CHECKNORMAL=0;//既不是全选也不是全不选
    public int check=CHECKNORMAL;//通过check来判断是否全选/全不选

    public AlivcPlayListAdapter(Context context,ArrayList<Video> videoLists) {
        this.context = new WeakReference<Context>(context);
        this.videoLists = videoLists;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public CheckBox cb_item;
        ImageView coverImage;
        TextView title;
        TextView tvVideoDuration;
        LinearLayout alivcVideoInfoItemLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            alivcVideoInfoItemLayout = (LinearLayout)itemView.findViewById(R.id.alivc_video_info_item_layout);
            coverImage = (ImageView)itemView.findViewById(R.id.iv_video_cover);
            title = (TextView)itemView.findViewById(R.id.tv_video_title);
            tvVideoDuration= (TextView)itemView.findViewById(R.id.tv_video_duration);
            cb_item=itemView.findViewById(R.id.cb_item);
        }
    }

    public void setIsChecked(int position,boolean check){
            isChecked.put(position,check);
    }
    public boolean getChecked(int position){
        if (isChecked.containsKey(position)){
            return isChecked.get(position);
        }else
            return false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.alivc_play_list_item,parent,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (videoLists.size() > 0) {
            Video video = videoLists.get(position);
            if (video != null) {
                if (showCheck){
                    if (getChecked(position)){
                        holder.cb_item.setChecked(true);
                    }else {
                        holder.cb_item.setChecked(false);
                    }
                    holder.cb_item.setVisibility(View.VISIBLE);
                }else {
                    setIsChecked(position,false);
                    holder.cb_item.setVisibility(View.GONE);
                }
                if (check==CHECKALL){
                    isChecked.put(position,true);
                    holder.cb_item.setChecked(true);
                }else if (check==CHECKNONE){
                    holder.cb_item.setChecked(false);
                    isChecked.put(position,false);
                }
                holder.title.setText(video.getTitle());
                if (video.getTitle().contains(".jpg")){//显示图片
                    if(video.getUrl().contains("http")){
                        Glide.with(this.context.get())//用于设置封面url的
                                .load(video.getCoverURL())
                                .centerCrop()
                                .crossFade()
                                .into(holder.coverImage);
                    }else {
                        Bitmap bitmap = BitmapFactory.decodeFile(video.getUrl());
                        holder.coverImage.setImageBitmap(bitmap);
                    }
                    holder.tvVideoDuration.setText("");
                }else if (video.getTitle().contains(".mp4")){//显示录制视频
                    double dTime = Double.parseDouble(video.getDuration());
                    holder.tvVideoDuration.setText(Formatter.double2Date(dTime));
                    if (video.getUrl().contains("http")){
                        Glide.with(this.context.get())
                            .load(video.getCoverURL())
                            .centerCrop()
                            .crossFade()
                            .into(holder.coverImage);
                    }else {
                        MediaMetadataRetriever media =new MediaMetadataRetriever();
                        media.setDataSource(video.getUrl());
                        Bitmap bitmap = media.getFrameAtTime();
                        holder.coverImage.setImageBitmap(bitmap);
                    }
                }
            }
        }
        holder.alivcVideoInfoItemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoListItemClick != null) {
                    onVideoListItemClick.onItemClick(v,position);
                }
            }
        });

        holder.alivcVideoInfoItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCheck=true;
                check=CHECKNORMAL;
                notifyDataSetChanged();
                holder.alivcVideoInfoItemLayout.setLongClickable(false);
                if (onVideoListItemLongClick != null) {
                    holder.cb_item.setChecked(true);
                    onVideoListItemLongClick.onItemLongClick(v,position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoLists.size();
    }

    private OnVideoListItemClick onVideoListItemClick;
    private OnVideoListItemLongClick onVideoListItemLongClick;

    public void setOnVideoListItemClick(
        OnVideoListItemClick onVideoListItemClick) {
        this.onVideoListItemClick = onVideoListItemClick;
    }

    public void setOnVideoListItemLongClick(
            OnVideoListItemLongClick onVideoListItemLongClick) {
        this.onVideoListItemLongClick = onVideoListItemLongClick;
    }


    public interface OnVideoListItemClick{
        void onItemClick(View v,int position);
    }

    public interface OnVideoListItemLongClick{
        void onItemLongClick(View view,int position);
    }

    public void removeCheck(){
        if (showCheck){
            showCheck=false;
            notifyDataSetChanged();
        }
    }
    public void setCHECKALL(){
        check=CHECKALL;
        notifyDataSetChanged();
    }
    public void setCHECKNONE(){
        check=CHECKNONE;
        notifyDataSetChanged();
    }
    public void setCHECKNORMAL(){
        check=CHECKNORMAL;
        notifyDataSetChanged();
    }
}
