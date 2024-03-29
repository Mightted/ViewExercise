package com.hxs.viewexercise.expandlist;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hxs.viewexercise.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CnPeng on 2017/1/21.
 * <p>
 * 自定义ExpandableListAdapter展示ExpandableListView的内容
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private String[]   classes;
    private String[] stuents;
    private Context context;
    View.OnClickListener ivGoToChildClickListener;

    public MyExpandableListAdapter(String[] classes, String[] stuents, Context context,
                                   View.OnClickListener ivGoToChildClickListener) {
        this.classes = classes;
        this.stuents = stuents;
        this.context = context;
        this.ivGoToChildClickListener = ivGoToChildClickListener;
    }

    @Override
    public int getGroupCount() {    //组的数量
        return classes.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {    //某组中子项数量
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {     //某组
        return classes[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {  //某子项
        return stuents[groupPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHold groupHold;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_elv_group, null);
            groupHold = new GroupHold();
            groupHold.tvGroupName = convertView.findViewById(R.id.groupTitle);
//            groupHold.ivGoToChildLv = (ImageView) convertView.findViewById(R.id.iv_goToChildLV);

            convertView.setTag(groupHold);

        } else {
            groupHold = (GroupHold) convertView.getTag();

        }

        String groupName = classes[groupPosition];
        groupHold.tvGroupName.setText(groupName);

        //取消默认的groupIndicator后根据方法中传入的isExpand判断组是否展开并动态自定义指示器
//        if (isExpanded) {   //如果组展开
//            groupHold.ivGoToChildLv.setImageResource(R.drawable.delete);
//        } else {
//            groupHold.ivGoToChildLv.setImageResource(R.drawable.send_btn_add);
//        }

        //setTag() 方法接收的类型是object ，所以可将position和converView先封装在Map中。Bundle中无法封装view,所以不用bundle
        Map<String, Object> tagMap = new HashMap<>();
        tagMap.put("groupPosition", groupPosition);
        tagMap.put("isExpanded", isExpanded);
        groupHold.tvGroupName.setTag(tagMap);

        //图标的点击事件
        groupHold.tvGroupName.setOnClickListener(ivGoToChildClickListener);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final ChildHold childHold;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_elv_child, null);
            childHold = new ChildHold();
            childHold.contentText = convertView.findViewById(R.id.contentText);
//            childHold.tvChildName = (TextView) convertView.findViewById(R.id.tv_elv_childName);
//            childHold.cbElvChild = (CheckBox) convertView.findViewById(R.id.cb_elvChild);
            convertView.setTag(childHold);
        } else {
            childHold = (ChildHold) convertView.getTag();
        }

        String childName = stuents[groupPosition];
        childHold.contentText.setText(childName);


        childHold.contentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String content = childHold.contentText.getText().toString().trim();
//                content += "";
                childHold.contentText.append("2354534\r\n");
//                txt_mmm.append(showString);
                int scrollAmount = childHold.contentText.getLayout().getLineTop(childHold.contentText.getLineCount())
                        - childHold.contentText.getHeight();
                if (scrollAmount > 0)
                    childHold.contentText.scrollTo(0, scrollAmount);
                else
                    childHold.contentText.scrollTo(0, 0);
            }
        });

//        childHold.cbElvChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    Toast.makeText(context, "条目选中:" + classes[groupPosition] + "的" +
//                            stuents[groupPosition][childPosition] + "被选中了", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "条目选中:" + classes[groupPosition] + "的" +
//                            stuents[groupPosition][childPosition] + "被取消选中了", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;    //默认返回false,改成true表示组中的子条目可以被点击选中
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    class GroupHold {
        TextView tvGroupName;
//        ImageView ivGoToChildLv;
    }

    class ChildHold {
        TextView contentText;
//        TextView tvChildName;
//        CheckBox cbElvChild;
    }
}
