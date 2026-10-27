package com.example.finbook;

import android.os.Bundle;
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
    }
}
