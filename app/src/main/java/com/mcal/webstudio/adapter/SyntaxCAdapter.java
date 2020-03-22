package com.mcal.webstudio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.webstudio.R;
import com.mcal.webstudio.language.LanguageC;
import com.mcal.webstudio.model.CModel;

import java.util.ArrayList;
import java.util.List;

public class SyntaxCAdapter extends ArrayAdapter {

    private List<String> dataList;
    private Context mContext;
    private int itemLayout;

    private SyntaxCAdapter.ListFilter listFilter = new SyntaxCAdapter.ListFilter();
    private List<String> dataListAllItems;


    public SyntaxCAdapter(Context context, int resource, List<String> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }
        AppCompatTextView strName = view.findViewById(R.id.text_id);
        AppCompatTextView strType = view.findViewById(R.id.type_id);
        AppCompatImageView imgType = view.findViewById(R.id.img_id);
        final AppCompatImageView imgInfo = view.findViewById(R.id.info_id);

        try {
            if (LanguageC.c().contains(getItem(position))) {
                imgType.setImageResource(R.drawable.ic_js_48);
                strType.setText(CModel.getAttrC(getItem(position)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strName.setText(getItem(position));

        try {
            ((AppCompatActivity) mContext).runOnUiThread(() -> {
                imgInfo.setVisibility(View.GONE);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<String> matchValues = new ArrayList<>();

                for (String dataItem : dataListAllItems) {
                    if (dataItem.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<String>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}