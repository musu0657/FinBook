package com.example.finbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 1. 实例化数据库帮助类
        BillDatabaseHelper dbHelper = new BillDatabaseHelper(this);

        // 2. 从数据库读取所有账单数据
        List<Bill> billList = dbHelper.getAllBills();

        // 3. 绑定汇总 TextView，统计笔数和收支总额
        TextView tvSummary = findViewById(R.id.tv_summary);
        if (tvSummary != null) {
            double totalExpense = 0;
            double totalIncome = 0;
            for (Bill bill : billList) {
                if ("收入".equals(bill.getType())) {
                    totalIncome += bill.getAmount();
                } else {
                    totalExpense += bill.getAmount();
                }
            }
            tvSummary.setText("共 " + billList.size() + " 笔，总支出 "
                    + String.format(Locale.CHINA, "%.2f", totalExpense)
                    + " 元，总收入 "
                    + String.format(Locale.CHINA, "%.2f", totalIncome) + " 元");
        }

        // 4. 绑定 ListView，使用 BillAdapter 展示数据，并设置条目点击事件
        ListView lvBills = findViewById(R.id.lv_bills);
        if (lvBills != null) {
            BillAdapter adapter = new BillAdapter(this, billList);
            lvBills.setAdapter(adapter);

            lvBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 从查询出来的列表中获取当前点击的账单数据
                    Bill clickedBill = billList.get(position);

                    // 创建 Intent 跳转到详情页
                    Intent intent = new Intent(ListActivity.this, DetailActivity.class);

                    // 把整个 Bill 对象传过去（Bill 已实现 Serializable）
                    intent.putExtra("bill_data", clickedBill);

                    startActivity(intent);
                }
            });
        }
    }
}
