package com.nougust3.diary.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import com.fiberlink.maas360.android.richtexteditor.RichEditText;
import com.fiberlink.maas360.android.richtexteditor.RichTextActions;
import com.fiberlink.maas360.android.richtexteditor.RichWebView;

import com.nougust3.diary.R;
import com.nougust3.diary.View.EditorView;
import com.nougust3.diary.ui.dialogs.NewNotebookFragment;
import com.nougust3.diary.ui.dialogs.EditorDialogsListener;
import com.nougust3.diary.ui.dialogs.RemoveNoteFragment;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.nougust3.diary.Presenter.EditorPresenter;

public class EditorActivity extends MvpAppCompatActivity implements EditorView, EditorDialogsListener {

    @InjectPresenter
    EditorPresenter editorPresenter;

    private FloatingActionButton fab;

    private EditText titleView;
    private RichEditText contentView;
    private Spinner spinner;

    private MenuItem doneItem;
    private MenuItem undoItem;
    private MenuItem redoItem;
    private MenuItem removeItem;

    private ProgressDialog progressDialog;
    private NewNotebookFragment newNotebookDialog;
    private RemoveNoteFragment removeNoteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        spinner = (Spinner) findViewById(R.id.spinner);
        titleView = (EditText) findViewById(R.id.titleView);
        contentView = (RichEditText) findViewById(R.id.contentView);
        contentView.setRichTextActionsView((RichTextActions) findViewById(R.id.rich_text_actions));

        newNotebookDialog = new NewNotebookFragment();
        removeNoteFragment = new RemoveNoteFragment();

        contentView.getmEditor().setOnScrollChangedCallback(new RichWebView.OnScrollChangedCallback() {
            @Override
            public void onScrollChange(WebView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                editorPresenter.onScrollNote(scrollY, oldScrollY);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorPresenter.onEditNote();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == spinner.getCount() - 1) {
                    //newNotebookDialog.show(getSupportFragmentManager(), "newNotebookDialog");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        setSupportActionBar(toolbar);
    }

    @Override
    public void showToolbar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    @Override
    public void hideToolbar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void showFAB() {
        fab.show();
    }

    @Override
    public void hideFAB() {
        fab.hide();
    }

    @Override
    public void disableViews() {
        titleView.setEnabled(false);
        spinner.setEnabled(false);
        contentView.setFocusable(false);
    }

    @Override
    public void enableViews() {
        titleView.setEnabled(true);
        spinner.setEnabled(true);
        contentView.setFocusable(true);
    }

    @Override
    public void createProgressDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.hide();
    }

    @Override
    public void setTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void setContent(String content) {
        contentView.setHtml(content);
    }

    @Override
    public void setNotebook(String notebook) {
        for (int i = 0; i < spinner.getCount(); i ++) {
            if(spinner.getItemAtPosition(i).toString().equals(notebook)) {
                spinner.setSelection(i);
            }
        }
    }

    @Override
    public void checkIntents() {
        if(getIntent().getType() != null &&
                Intent.ACTION_SEND.equals(getIntent().getAction()) &&
                "text/plain".equals(getIntent().getType())) {
            editorPresenter.onParseHtml(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
        else {
            long creation = getIntent().getLongExtra("creation5", 0);

            if(creation != 0) {
                editorPresenter.onLoadNote(creation);
            }
            else {
                editorPresenter.onCreateNote();
                editorPresenter.onSetNotebook(getIntent().getLongExtra("notebook", 0));
            }
        }
    }

    @Override
    public void showRemoveDialog() {
        removeNoteFragment.show(getSupportFragmentManager(), "Remove note fragment");
    }

    @Override
    public void onRemove() {
        editorPresenter.onRemoveConfirm();
    }

    @Override
    public void showNewNotebookDialog() {
        newNotebookDialog.show(getSupportFragmentManager(), "New notebook dialog");
    }

    @Override
    public void onNewNotebook() {
        editorPresenter.onNewNotebook();
    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);

        doneItem = menu.findItem(R.id.app_bar_done);
        undoItem = menu.findItem(R.id.app_bar_undo);
        redoItem = menu.findItem(R.id.app_bar_redo);
        removeItem = menu.findItem(R.id.app_bar_remove);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.equals(doneItem)) {
            editorPresenter.onSaveNote(
                    titleView.getText().toString(),
                    spinner.getSelectedItem().toString(),
                    contentView.getHtml()
            );
        }
        else if(item.equals(removeItem)) {
            editorPresenter.onRemoveNote();
        }
        else if(item.equals(undoItem)) {
            contentView.getmEditor().undo();
        }
        else if(item.equals(redoItem)) {
            contentView.getmEditor().redo();
        }

        return true;
    }

    @Override
    public void populateSpinner(ArrayList<String> notebooks) {
        notebooks.add("New notebook");
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.editor_spinner_item, notebooks));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public Context getContext() {
        return getApplicationContext();
    }
}
