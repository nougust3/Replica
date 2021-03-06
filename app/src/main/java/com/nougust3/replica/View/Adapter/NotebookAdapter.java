package com.nougust3.replica.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Notebook;
import com.nougust3.replica.R;
import com.nougust3.replica.View.Holder.NotebookHolder;

import java.util.ArrayList;
import java.util.List;

public class NotebookAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private ArrayList<Notebook> notebooks = new ArrayList<>();

    private SparseBooleanArray selectedItemsId;

    private Context context;

    public NotebookAdapter(Context context) {
        this.context = context;
        selectedItemsId = new SparseBooleanArray();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return notebooks.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notebook notebook = notebooks.get(position);
        NotebookHolder holder;
        View view = convertView;

        if(view == null) {
            view = inflater.inflate(R.layout.notebook_item, parent, false);
            holder = new NotebookHolder();
            holder.nameView = (TextView) view.findViewById(R.id.nameView);
            holder.descriptionView = (TextView) view.findViewById(R.id.descriptionView);
            holder.countView = (TextView) view.findViewById(R.id.countView);
            view.setTag(holder);
        }
        else {
            holder = (NotebookHolder) view.getTag();
        }

        DBHelper db = new DBHelper(context);
        int count = db.getFromNotebook(notebook.getId()).size();

        holder.nameView.setText(notebook.getName());
        holder.descriptionView.setText(notebook.getDescription());
        if(count == 0) {
            holder.countView.setText("Empty");
        }
        else {
            holder.countView.setText(count + " notes");
        }

        return view;
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsId.get(position));
    }

    public void removeSelection() {
        selectedItemsId = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemsId.size();
    }

    public SparseBooleanArray getSelectedId() {
        return selectedItemsId;
    }

    public void selectView(int position, boolean value) {
        if(value) {
            selectedItemsId.put(position, value);
        }
        else {
            selectedItemsId.delete(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public Notebook getItem(int position) {
        return notebooks.get(position);
    }

    public void setNotebooks(ArrayList<Notebook> list) {
        notebooks = list;
    }

}
