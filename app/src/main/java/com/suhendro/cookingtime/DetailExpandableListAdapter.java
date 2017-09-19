package com.suhendro.cookingtime;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.suhendro.cookingtime.model.CookingStep;
import com.suhendro.cookingtime.model.Ingredient;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by Suhendro on 9/18/2017.
 */

public class DetailExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final String[] mTitles;
    private Ingredient[] ingredients;
    private CookingStep[] cookingInstructions;

    public DetailExpandableListAdapter(Context context, String[] titles) {
        mContext = context;
        mTitles = titles;
    }

    @Override
    public int getGroupCount() {
        // there are only group of ingredients and instruction
        return mTitles.length;
    }

    @Override
    public int getChildrenCount(int group) {
        if(group == 0) {
            return ingredients == null ? 0 : ingredients.length;
        } else if(group == 1) {
            return cookingInstructions == null ? 0 : cookingInstructions.length;
        }
        return 0;
    }

    @Override
    public Object getGroup(int group) {
        return mTitles[group];
    }

    @Override
    public Object getChild(int group, int itemAt) {
        if(group == 0)
            return ingredients[itemAt];
        else if(group == 1)
            return cookingInstructions[itemAt];

        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int group, int itemAt) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView groupTitle = (TextView) convertView.findViewById(R.id.lst_group);
        groupTitle.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.lst_item);
        String text = null;
        if(groupPosition == 0) {
            return convertView;
        } else {
            CookingStep cookingStep = (CookingStep) getChild(groupPosition, childPosition);
            text = cookingStep.getShortDescription();
        }
        item.setText(text);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public void setCookingInstructions(CookingStep[] cookingInstructions) {
        this.cookingInstructions = cookingInstructions;
    }
}
