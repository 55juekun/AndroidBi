package com.aliyun.vodplayerview.Util;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.vodplayerview.activity.AliyunPlayerSkinActivity;
import com.bi.R;

import java.util.ArrayList;

public class TreeAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Bitmap mIconCollapse;
    private Bitmap mIconExpand;

    private Context mContext;
    private ArrayList<TreeElement> root;
    public TreeAdapter(Context context,
                           ArrayList root) {

        this.mContext=context;
        mInflater = LayoutInflater.from(context);
        this.root = root;
        mIconCollapse = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.collapse);
        mIconExpand = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.expand);
    }
    public int getCount() {
        return root.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
//        convertView = mInflater.inflate(R.layout.activity_main, null);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.tree_item,parent,false);
        holder = new ViewHolder();
        holder.text = (TextView) convertView.findViewById(R.id.text);
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
        convertView.setTag(holder);
            /*
             * } else { holder = (ViewHolder) convertView.getTag(); }
             */

        final TreeElement obj = root.get(position);

        int level = obj.getLevel();
        holder.icon.setPadding(25 * (level + 1),
                holder.icon.getPaddingTop(), 0,
                holder.icon.getPaddingBottom());
        holder.text.setText(obj.getOutlineTitle());
        if (obj.isMhasChild() && (obj.isExpanded() == false)) {
            holder.icon.setImageBitmap(mIconCollapse);
        } else if (obj.isMhasChild() && (obj.isExpanded() == true)) {
            holder.icon.setImageBitmap(mIconExpand);
        } else if (!obj.isMhasChild()) {
            holder.icon.setImageBitmap(mIconCollapse);
            holder.icon.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id, DrawerLayout drawerLayout) {
        if (!root.get(position).isMhasChild()) {
            drawerLayout.closeDrawer(Gravity.START);
            TreeElement element = root.get(position);
            Intent intent=new Intent(mContext,AliyunPlayerSkinActivity.class);
            intent.putExtra("id",element.getId());
            mContext.startActivity(intent);
            return;
        }

        if (root.get(position).isExpanded()) {
            root.get(position).setExpanded(false);
            TreeElement element = root.get(position);
            ArrayList<TreeElement> temp = new ArrayList<TreeElement>();

            for (int i = position + 1; i < root.size(); i++) {
                if (element.getLevel() >= root.get(i).getLevel()) {
                    break;
                }
                temp.add(root.get(i));
            }

            root.removeAll(temp);

            notifyDataSetChanged();

        } else {
            TreeElement obj = root.get(position);
            obj.setExpanded(true);
            int level = obj.getLevel();
            int nextLevel = level + 1;

            for (TreeElement element : obj.getChildList()) {
                element.setLevel(nextLevel);
                element.setExpanded(false);
                root.add(position + 1, element);

            }
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
