package com.example.finbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * 账单列表自定义适配器，继承自 BaseAdapter
 */
public class BillAdapter extends BaseAdapter {

    /** 收入金额颜色：微信绿 */
    private static final int COLOR_INCOME = Color.parseColor("#07C160");
    /** 支出金额颜色：深灰 */
    private static final int COLOR_EXPENSE = Color.parseColor("#333333");

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
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvRemark = convertView.findViewById(R.id.tv_remark);
            holder.tvAmount = convertView.findViewById(R.id.tv_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bill bill = billList.get(position);
        // 左侧显示「分类 / 类型」，下方显示日期和备注，右侧显示金额
        holder.tvCategoryType.setText(bill.getCategory() + " / " + bill.getType());

        // 根据收支类型设置金额文字（带正负号，保留两位小数）和颜色
        if ("收入".equals(bill.getType())) {
            holder.tvAmount.setText("+" + String.format(Locale.CHINA, "%.2f", bill.getAmount()));
            holder.tvAmount.setTextColor(COLOR_INCOME);
        } else {
            holder.tvAmount.setText("-" + String.format(Locale.CHINA, "%.2f", bill.getAmount()));
            holder.tvAmount.setTextColor(COLOR_EXPENSE);
        }

        // 只取时间字符串中的日期部分展示
        String time = bill.getTime();
        String date = "";
        if (time != null && time.length() >= 10) {
            date = time.substring(0, 10);
        }
        holder.tvTime.setText(date);

        // 备注：非空才显示，否则隐藏（因为列表项会复用，必须显式设置可见性）
        String remark = bill.getRemark();
        if (remark != null && !remark.trim().isEmpty()) {
            holder.tvRemark.setText(remark);
            holder.tvRemark.setVisibility(View.VISIBLE);
        } else {
            holder.tvRemark.setText("");
            holder.tvRemark.setVisibility(View.GONE);
        }
        return convertView;
    }

    /** ViewHolder 用于缓存视图，提高列表滚动性能 */
    static class ViewHolder {
        TextView tvCategoryType;
        TextView tvTime;
        TextView tvRemark;
        TextView tvAmount;
    }
}
