package com.example.finbook;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 设置布局
        setContentView(R.layout.activity_detail);

        // 2. 接收传过来的 Bill 对象
        Bill bill = (Bill) getIntent().getSerializableExtra("bill_data");

        // 3. 找到布局里的 TextView
        TextView tvInfo = findViewById(R.id.tv_info);
        TextView tvTime = findViewById(R.id.tv_time);

        // 4. 如果有数据，就展示出来
        if (bill != null) {
            if (tvInfo != null) {
                String info = "类型：" + bill.getType() + "\n" +
                        "分类：" + bill.getCategory() + "\n" +
                        "金额：" + bill.getAmount() + " 元\n" +
                        "备注：" + bill.getRemark();
                tvInfo.setText(info);
            }
            // 展示完整的年月日时分
            if (tvTime != null) {
                tvTime.setText("时间：" + bill.getTime());
            }
        }
    }
}