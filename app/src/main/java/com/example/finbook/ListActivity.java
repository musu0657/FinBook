package com.example.finbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private BillDatabaseHelper dbHelper;
    // 从数据库查出的全部原始数据
    private List<Bill> allBills;
    // 过滤后展示给适配器的数据
    private List<Bill> filteredBills;
    private BillAdapter adapter;
    private TextView tvExpense;
    private TextView tvIncome;
    // 当前搜索关键字
    private String currentSearchKeyword = "";
    // 当前收支筛选类型：全部 / 收入 / 支出
    private String currentFilterType = "全部";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 1. 实例化数据库帮助类
        dbHelper = new BillDatabaseHelper(this);

        // 2. 绑定控件
        tvExpense = findViewById(R.id.tv_expense);
        tvIncome = findViewById(R.id.tv_income);
        EditText etSearch = findViewById(R.id.et_search);
        RadioGroup rgFilter = findViewById(R.id.rg_filter);
        ListView lvBills = findViewById(R.id.lv_bills);

        // 3. 初始化列表和适配器（数据在 onResume 中加载）
        allBills = new ArrayList<>();
        filteredBills = new ArrayList<>();
        adapter = new BillAdapter(this, filteredBills);
        if (lvBills != null) {
            lvBills.setAdapter(adapter);

            // 单击：跳转到详情页
            lvBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bill clickedBill = filteredBills.get(position);
                    Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                    intent.putExtra("bill_data", clickedBill);
                    startActivity(intent);
                }
            });

            // 长按：弹出修改/删除菜单
            lvBills.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Bill bill = filteredBills.get(position);
                    showActionDialog(bill);
                    return true; // 消费掉长按事件，避免再触发单击
                }
            });
        }

        // 4. 搜索框监听：更新关键字并刷新
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchKeyword = s.toString().trim();
                    updateListView();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        // 5. 条件筛选监听：更新筛选类型并刷新
        if (rgFilter != null) {
            rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rb_filter_income) {
                        currentFilterType = "收入";
                    } else if (checkedId == R.id.rb_filter_expense) {
                        currentFilterType = "支出";
                    } else {
                        currentFilterType = "全部";
                    }
                    updateListView();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到本页都重新加载数据，保证修改后能看到最新结果
        refreshList();
    }

    /** 重新从数据库读取原始数据，并刷新列表 */
    private void refreshList() {
        allBills.clear();
        allBills.addAll(dbHelper.getAllBills());
        updateListView();
    }

    /** 根据「搜索关键字 + 收支类型」双重过滤，刷新列表和汇总 */
    private void updateListView() {
        filteredBills.clear();
        for (Bill bill : allBills) {
            if (matchesFilter(bill)) {
                filteredBills.add(bill);
            }
        }
        adapter.notifyDataSetChanged();
        updateSummary();
    }

    /** 判断一条账单是否满足当前的搜索和筛选条件 */
    private boolean matchesFilter(Bill bill) {
        // 收支类型筛选
        if (!"全部".equals(currentFilterType) && !currentFilterType.equals(bill.getType())) {
            return false;
        }
        // 关键字筛选（匹配备注或分类）
        if (currentSearchKeyword != null && !currentSearchKeyword.isEmpty()) {
            String category = bill.getCategory() == null ? "" : bill.getCategory();
            String remark = bill.getRemark() == null ? "" : bill.getRemark();
            if (!category.contains(currentSearchKeyword) && !remark.contains(currentSearchKeyword)) {
                return false;
            }
        }
        return true;
    }

    /** 更新顶部汇总（基于过滤后的列表） */
    private void updateSummary() {
        double totalExpense = 0;
        double totalIncome = 0;
        for (Bill bill : filteredBills) {
            if ("收入".equals(bill.getType())) {
                totalIncome += bill.getAmount();
            } else {
                totalExpense += bill.getAmount();
            }
        }
        if (tvExpense != null) {
            tvExpense.setText(String.format(Locale.CHINA, "支出 ¥%.2f", totalExpense));
        }
        if (tvIncome != null) {
            tvIncome.setText(String.format(Locale.CHINA, "收入 ¥%.2f", totalIncome));
        }
    }

    /** 长按弹出的操作菜单 */
    private void showActionDialog(final Bill bill) {
        final String[] options = {"修改账单", "删除账单", "取消"};
        new AlertDialog.Builder(this)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // 修改：跳转回 MainActivity，并带上要编辑的账单
                            Intent intent = new Intent(ListActivity.this, MainActivity.class);
                            intent.putExtra("edit_bill", bill);
                            startActivity(intent);
                        } else if (which == 1) {
                            // 删除：二次确认
                            confirmDelete(bill);
                        }
                        // which == 2 为“取消”，不做任何操作
                    }
                })
                .show();
    }

    /** 删除前的二次确认 */
    private void confirmDelete(final Bill bill) {
        new AlertDialog.Builder(this)
                .setTitle("删除账单")
                .setMessage("确定要删除这条账单吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteBill(bill.getId());
                        Toast.makeText(ListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
