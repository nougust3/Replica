package com.nougust3.replica.View.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fiberlink.maas360.android.richtexteditor.RichEditText;
import com.fiberlink.maas360.android.richtexteditor.RichTextActions;
import com.nougust3.replica.Keep;
import com.nougust3.replica.Model.Content;
import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Note;
import com.nougust3.replica.R;
import com.nougust3.replica.Utils.ContentUtils;
import com.nougust3.replica.Utils.DateUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParserActivity extends BaseActivity {

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);



        Button saveBtn = (Button) findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    private void parseHtml(String url) {
        Keep.getApi().getData(url).enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, Response<Content> response) {
                Content content = response.body();
                note = new Note();

                note.setTitle(content.getTitle());
                note.setContent(content.getContent());

                show(note);
                //saveNote();
            }
            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                t.printStackTrace();
                showToast("Fail");
            }
        });
    }

    private void show(Note note) {
        TextView titleView = (TextView) findViewById(R.id.titleView);
        TextView dateView = (TextView) findViewById(R.id.dateView);
        TextView notebookView = (TextView) findViewById(R.id.notebookView);
        TextView contentView = (TextView) findViewById(R.id.contentView);

        RichEditText content = (RichEditText) findViewById(R.id.content);
        content.removeChangeListener();

        RichTextActions richTextActions = (RichTextActions) findViewById(R.id.rich_text_actions);
        content.setRichTextActionsView(richTextActions);

        titleView.setText(note.getTitle());
        dateView.setText(DateUtils.format(note.getCreation()));
        notebookView.setText("Inbox");
        contentView.setText(ContentUtils.htmlToText(note.getContent()));

        content.setHtml(note.getContent());
    }

    private void saveNote() {
        DBHelper db = new DBHelper(getContext());
        db.updateNote(note);
        finish();
    }
}
