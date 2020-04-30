package alirezajavadi.todotoday.adapter;

import android.content.Context;
import android.text.NoCopySpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import alirezajavadi.todotoday.R;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> titleList;
    private HashMap<String, List<String>> contentTextHashMap;

    public ExpandableListViewAdapter(Context context, List<String> titleList, HashMap<String, List<String>> contentTextHashMap) {
        this.context = context;
        this.titleList = titleList;
        this.contentTextHashMap = contentTextHashMap;
    }

    @Override
    public int getGroupCount() {
        return titleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return contentTextHashMap.get(titleList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.contentTextHashMap.get(this.titleList.get(groupPosition)).get(childPosition);
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String title = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.view_title_expandable_list, null);
        }
        ((TextView) convertView.findViewById(R.id.txv_titleExpandable_expandableList)).setText(title);
        if (isExpanded)
            ((ImageView) convertView.findViewById(R.id.img_indicator_expandable)).setImageResource(R.drawable.ic_arrow_bottom);
        else
            ((ImageView) convertView.findViewById(R.id.img_indicator_expandable)).setImageResource(R.drawable.ic_arrow_left);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String contentText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.view_item_expandable_list, null);
        }
        ((TextView) convertView.findViewById(R.id.txv_itemExpandable_expandableList)).setText(contentText);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
