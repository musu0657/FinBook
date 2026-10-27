package com.example.finbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 1. 接收 MainActivity 传过来的账单总数
        int totalCount = getIntent().getIntExtra("total_count", 0);

        // 2. 绑定汇总 TextView，并把数字显示出来（证明 Intent 传值成功）
        TextView tvSummary = findViewById(R.id.tv_summary); // 确保 XML 里有这个 ID
        if (tvSummary != null) {
            tvSummary.setText("当前共 " + totalCount + " 笔账单");
        }

        // 3. 绑定 ListView，并使用我们写好的 BillAdapter 展示数据
        ListView lvBills = findViewById(R.id.lv_bills); // 确保 XML 里有这个 ID
        if (lvBills != null) {
            // 直接把 MainActivity 里的 billList 拿过来用
            BillAdapter adapter = new BillAdapter(this, MainActivity.billList);
            lvBills.setAdapter(adapter);
        }

        // 给 ListView 设置点击事件，点击跳转到详情页
        lvBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取当前点击的那条账单数据
                Bill clickedBill = MainActivity.billList.get(position);

                // 创建 Intent 跳转到详情页
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);

                // 【核心传值】：把整个 Bill 对象传过去（Bill 已经实现了 Serializable）
                intent.putExtra("bill_data", clickedBill);

                startActivity(intent);
            }
        });
    }
}
