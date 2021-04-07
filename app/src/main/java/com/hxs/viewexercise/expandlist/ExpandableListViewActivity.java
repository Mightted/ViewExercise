package com.hxs.viewexercise.expandlist;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hxs.viewexercise.R;

import java.util.Map;

/**
 * Created by CnPeng on 2017/1/21.
 * <p>
 * 使用BaseExpandableLsitAdapter 展示ExpandableLsitView 的具体内容
 */

public class ExpandableListViewActivity extends AppCompatActivity {

    private View.OnClickListener ivGoToChildClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded);

        init();
    }

    private void init() {
        final ExpandableListView elv01 = (ExpandableListView) findViewById(R.id.elv_01);

        //模拟数据（数组，集合都可以，这里使用数组）
        final String[] classes = new String[]{"进气及燃油系统", "点火系统", "车速及怠速控制系统", "电脑控制系统", "废气控制系统", "故障码检测"};
        final String[] items = new String[]{"11111\r\n222222\n33333\n44444\n55555\n66666\n77777\n88888\n99999\naaaaa\nbbbbb\nccccc\nddddd\neeeee\nfffff\n", "111111", "111111", "111111", "111111", "111111"};
//        final String[][] students = new String[][]{{"张三1", "李四1", "王五1", "赵六1", "钱七1", "高八1"}, {"张三1", "李四1", "王五1",
//                "赵六1", "钱七1", "高八1"}, {"张三1", "李四1", "王五1", "赵六1", "钱七1", "高八1"}, {"张三1", "李四1", "王五1", "赵六1", "钱七1",
//                "高八1"},{}};

        //自定义 展开/收起  图标的点击事件。position和 isExpand 都是通过tag 传递的
        ivGoToChildClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取被点击图标所在的group的索引
                Map<String, Object> map = (Map<String, Object>) v.getTag();
                int groupPosition = (int) map.get("groupPosition");
//                boolean isExpand = (boolean) map.get("isExpanded");   //这种是通过tag传值
                boolean isExpand = elv01.isGroupExpanded(groupPosition);    //判断分组是否展开

                if (isExpand) {
                    elv01.collapseGroup(groupPosition);
                } else {
                    elv01.expandGroup(groupPosition);
                }
            }
        };

        //创建并设置适配器
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(classes, items, this,
                ivGoToChildClickListener);
        elv01.setAdapter(adapter);

        //默认展开第一个分组
        elv01.expandGroup(0);

        //展开某个分组时，并关闭其他分组。注意这里设置的是 ExpandListener
        elv01.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //遍历 group 的数组（或集合），判断当前被点击的位置与哪个组索引一致，不一致就合并起来。
//                for (int i = 0; i < classes.length; i++) {
//                    if (i != groupPosition) {
//                        elv01.collapseGroup(i); //收起某个指定的组
//                    }
//                }
            }
        });

        //点击某个分组时，跳转到指定Activity
        elv01.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(ExpandableListViewActivity.this, "组被点击了，跳转到具体的Activity", Toast.LENGTH_SHORT).show();
                return true;    //拦截点击事件，不再处理展开或者收起
            }
        });

        //某个分组中的子View被点击时的事件
//        elv01.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
//                                        long id) {
//
//                Toast.makeText(ExpandableListViewActivity.this, "组中的条目被点击：" + classes[groupPosition] + "的" +
//                        students[groupPosition][childPosition] + "放学后到校长办公室", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

    }
}
