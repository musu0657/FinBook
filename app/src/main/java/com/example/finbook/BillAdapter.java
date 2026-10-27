package com.example.finbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 账单列表自定义适配器，继承自 BaseAdapter
 */
public class BillAdapter extends BaseAdapter {

    private Context context;
    private List<Bill> billList;

    /**
     * 构造方法
     *
     * @param context  上下文
     * @param billList 账单数据列表
     */
    public BillAdapter(Context context, List<Bill> billList) {
        this.context = context;
        this.billList = billList;
    }

    /** 返回列表数据条数 */
    @Override
    public int getCount() {
        return billList == null ? 0 : billList.size();
    }

    /** 返回指定位置的数据项 */
    @Override
    public Object getItem(int position) {
        return billList.get(position);
    }

    /** 返回指定位置数据项的 id */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** 获取并复用列表项的视图 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 首次创建视图，并复用 item_bill 布局
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
            holder = new ViewHolder();
            holder.tvCategoryType = convertView.findViewById(R.id.tv_category_type);
            holder.tvAmount = convertView.findViewById(R.id.tv_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bill bill = billList.get(position);
        // 左侧显示「分类 / 类型」，右侧显示金额
        holder.tvCategoryType.setText(bill.getCategory() + " / " + bill.getType());
        holder.tvAmount.setText(String.valueOf(bill.getAmount()));
        return convertView;
    }

    /** ViewHolder 用于缓存视图，提高列表滚动性能 */
    static class ViewHolder {
        TextView tvCategoryType;
        TextView tvAmount;
    }
}
