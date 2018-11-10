package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> questionsList;
    private HashMap<String, List<String>> questionsAnswersMap;
    private ExpandableListView expandableListView;
    public ArrayList<String> groupItem, tempChild;


    public ExpandableListAdapter(Context context,
                                 List<String> questionsList,
                                 HashMap<String, List<String>> questionsAnswersMap,
                                 ExpandableListView expandableListView) {
        this.context = context;
        this.questionsList = questionsList;
        this.questionsAnswersMap = questionsAnswersMap;
        this.expandableListView = expandableListView;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.questionsAnswersMap.get(this.questionsList.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);
        tempChild = (ArrayList<String>) questionsAnswersMap.get(questionsList.get(groupPosition));

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_odgovori_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.rezultatiChildTV);

        //View expandedChildLine = convertView.findViewById(R.id.lineChildExpanded);

        txtListChild.setText(tempChild.get(childPosition));

        //expandedChildLine.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) questionsAnswersMap.get(questionsList.get(groupPosition))).size();
        //return this.questionsAnswersMap.get(this.questionsList.get(groupPosition))
                //.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.questionsList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.questionsList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_pitanje_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.rezultatiParentTV);
        lblListHeader.setText(headerTitle);
        TextView expandTV = (TextView)convertView.findViewById(R.id.expand_iconTV);
        View nonExpandedLine = convertView.findViewById(R.id.lineNotExpanded);

        if(isExpanded){

            expandTV.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            expandTV.setText("-");
            lblListHeader.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            nonExpandedLine.setVisibility(View.GONE);

        }else{
            expandTV.setTextColor(ContextCompat.getColor(context, R.color.black_overlay));
            expandTV.setText("+");
            lblListHeader.setTextColor(ContextCompat.getColor(context, R.color.black_overlay));
            nonExpandedLine.setVisibility(View.VISIBLE);
        }
        // listView.setDividerHeight(20);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

