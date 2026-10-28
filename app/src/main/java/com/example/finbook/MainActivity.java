package com.example.finbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 数据库帮助类实例
    private BillDatabaseHelper dbHelper;
    // 正在编辑的账单（null 表示新增模式，非 null 表示修改模式）
    private Bill editingBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 设置布局

        // 1. 实例化数据库帮助类
        dbHelper = new BillDatabaseHelper(this);

        // 2. 绑定控件
        EditText etAmount = findViewById(R.id.et_amount);
        EditText etRemark = findViewById(R.id.et_remark);
        RadioGroup rgType = findViewById(R.id.rg_type);
        Spinner spCategory = findViewById(R.id.sp_category);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnViewList = findViewById(R.id.btn_view_list);
        Button btnClear = findViewById(R.id.btn_clear);

        // 3. 收支类型对应的分类列表
        final String[] expenseCategories = {"餐饮", "交通", "购物", "娱乐", "其他"};
        final String[] incomeCategories = {"工资", "兼职", "理财收益", "其他"};

        // 默认选中“支出”，先显示支出分类
        setCategoryAdapter(spCategory, expenseCategories);

        // 监听收支类型切换，动态更新 Spinner 的分类列表
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String[] newCategories = (checkedId == R.id.rb_income) ? incomeCategories : expenseCategories;
                setCategoryAdapter(spCategory, newCategories);
            }
        });

        // 4. 检查是否是修改模式（从列表页长按“修改”跳转过来）
        editingBill = (Bill) getIntent().getSerializableExtra("edit_bill");
        if (editingBill != null) {
            // 修改模式：回填数据，并把按钮文字改成“更新账单”
            btnSave.setText("更新账单");
            etAmount.setText(String.valueOf(editingBill.getAmount()));
            etRemark.setText(editingBill.getRemark());

            // 根据收支类型选中对应的 RadioButton（会触发上面的监听，切换分类列表）
            if ("收入".equals(editingBill.getType())) {
                rgType.check(R.id.rb_income);
            } else {
                rgType.check(R.id.rb_expense);
            }

            // 选中对应的分类
            String[] categories = "收入".equals(editingBill.getType()) ? incomeCategories : expenseCategories;
            selectCategory(spCategory, categories, editingBill.getCategory());
        }

        // 5. 保存按钮点击事件：新增或更新数据
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
                String remark = etRemark.getText().toString().trim();

                // 使用 SimpleDateFormat 获取当前系统时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String time = sdf.format(new Date());

                if (editingBill != null) {
                    // 修改模式：更新数据库并关闭本页，回到列表页
                    editingBill.setType(type);
                    editingBill.setCategory(category);
                    editingBill.setAmount(amount);
                    editingBill.setRemark(remark);
                    editingBill.setTime(time);
                    dbHelper.updateBill(editingBill);
                    Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // 新增模式：插入数据库
                    Bill newBill = new Bill(type, category, amount, remark, time);
                    dbHelper.insertBill(newBill);
                    Toast.makeText(MainActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                    etAmount.setText("");
                    etRemark.setText("");
                }
            }
        });

        // 6. 查看列表按钮的点击事件：跳转到列表页
        btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        // 7. 清空数据按钮的点击事件：弹窗确认后清空数据库
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("清空数据")
                        .setMessage("确定要清空所有账单数据吗？此操作不可逆。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.clearAllBills();
                                Toast.makeText(MainActivity.this, "已清空全部数据", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 取消：不做任何操作
                            }
                        })
                        .show();
            }
        });
    }

    /** 给 Spinner 设置分类列表 */
    private void setCategoryAdapter(Spinner spCategory, String[] categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    /** 在分类列表中选中指定的分类 */
    private void selectCategory(Spinner spCategory, String[] categories, String category) {
        if (category == null) {
            return;
        }
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                spCategory.setSelection(i);
                return;
            }
        }
    }
}
