package com.example.finbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 1. 存放所有账单的集合
    public static List<Bill> billList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 设置布局

        // 2. 绑定控件
        EditText etAmount = findViewById(R.id.et_amount);
        RadioGroup rgType = findViewById(R.id.rg_type);
        Spinner spCategory = findViewById(R.id.sp_category);
        CheckBox cbImportant = findViewById(R.id.cb_important);
        ImageButton btnSave = findViewById(R.id.btn_save);
        Button btnViewList = findViewById(R.id.btn_view_list);

        // 3. 初始化 Spinner
        String[] categories = {"餐饮", "交通", "购物", "工资", "娱乐", "其他"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        // 4. 保存按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etAmount.getText().toString().trim();
                if (amountStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                String type = "支出";
                if (rgType.getCheckedRadioButtonId() == R.id.rb_income) {
                    type = "收入";
                }
                String category = spCategory.getSelectedItem().toString();
                String remark = cbImportant.isChecked() ? "重点" : "普通";

                billList.add(new Bill(type, category, amount, remark));
                Toast.makeText(MainActivity.this, "保存成功！共 " + billList.size() + " 笔", Toast.LENGTH_SHORT).show();
                etAmount.setText("");
            }
        });

        // 5. 查看列表按钮的点击事件：跳转并传值
        btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建 Intent，从 MainActivity 跳转到 ListActivity
                Intent intent = new Intent(MainActivity.this, ListActivity.class);

                // 把当前账单的总数量传给下一个页面
                intent.putExtra("total_count", billList.size());

                // 执行跳转
                startActivity(intent);
            }
        });
    }
}