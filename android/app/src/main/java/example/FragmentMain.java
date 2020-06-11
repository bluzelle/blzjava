package example;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentMain extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.findViewById(R.id.create).setOnClickListener(this);
        view.findViewById(R.id.read).setOnClickListener(this);
        view.findViewById(R.id.update).setOnClickListener(this);
        view.findViewById(R.id.delete).setOnClickListener(this);
        ((EditText) view.findViewById(R.id.createValue)).setOnEditorActionListener(this);
        ((EditText) view.findViewById(R.id.readKey)).setOnEditorActionListener(this);
        ((EditText) view.findViewById(R.id.updateValue)).setOnEditorActionListener(this);
        ((EditText) view.findViewById(R.id.deleteKey)).setOnEditorActionListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create:
                create();
                break;
            case R.id.read:
                read();
                break;
            case R.id.update:
                update();
                break;
            case R.id.delete:
                delete();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        switch (textView.getId()) {
            case R.id.createValue:
                return create();
            case R.id.readKey:
                return read();
            case R.id.updateValue:
                return update();
            case R.id.deleteKey:
                return delete();
        }
        return true;
    }

    private boolean create() {
        String key = ((EditText) getView().findViewById(R.id.createKey)).getText().toString();
        if (key.isEmpty()) {
            ((EditText) getView().findViewById(R.id.createKey)).setHint(R.string.enterKey);
            return true;
        }
        String value = ((EditText) getView().findViewById(R.id.createValue)).getText().toString();
        if (value.isEmpty()) {
            ((EditText) getView().findViewById(R.id.createValue)).setHint(getString(R.string.enterValue));
            return true;
        }
        ((TextView) getView().findViewById(R.id.createResult)).setText(R.string.creating);

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... s) {
                try {
                    Data.bluzelle.create(s[0], s[1], Data.gasInfo, Data.leaseInfo);
                    return getString(R.string.created);
                } catch (Exception exception) {
                    return exception.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((TextView) getView().findViewById(R.id.createResult)).setText(result);
            }
        }.execute(key, value);

        return false;
    }

    private boolean read() {
        String key = ((EditText) getView().findViewById(R.id.readKey)).getText().toString();
        if (key.isEmpty()) {
            ((EditText) getView().findViewById(R.id.readKey)).setHint(R.string.enterKey);
            return true;
        }
        ((TextView) getView().findViewById(R.id.readResult)).setText(R.string.reading);

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... s) {
                try {
                    return Data.bluzelle.read(s[0], false);
                } catch (Exception exception) {
                    return exception.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((TextView) getView().findViewById(R.id.readResult)).setText(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, key);

        return false;
    }

    private boolean update() {
        String key = ((EditText) getView().findViewById(R.id.updateKey)).getText().toString();
        if (key.isEmpty()) {
            ((EditText) getView().findViewById(R.id.updateKey)).setHint(R.string.enterKey);
            return true;
        }
        String value = ((EditText) getView().findViewById(R.id.updateValue)).getText().toString();
        if (value.isEmpty()) {
            ((EditText) getView().findViewById(R.id.updateValue)).setHint(getString(R.string.enterValue));
            return true;
        }
        ((TextView) getView().findViewById(R.id.updateResult)).setText(R.string.updating);

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... s) {
                try {
                    Data.bluzelle.update(s[0], s[1], Data.gasInfo, Data.leaseInfo);
                    return getString(R.string.updated);
                } catch (Exception exception) {
                    return exception.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((TextView) getView().findViewById(R.id.updateResult)).setText(result);
            }
        }.execute(key, value);

        return false;
    }

    private boolean delete() {
        String key = ((EditText) getView().findViewById(R.id.deleteKey)).getText().toString();
        if (key.isEmpty()) {
            ((EditText) getView().findViewById(R.id.deleteKey)).setHint(R.string.enterKey);
            return true;
        }
        ((TextView) getView().findViewById(R.id.deleteResult)).setText(R.string.deleting);

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... s) {
                try {
                    Data.bluzelle.delete(s[0], Data.gasInfo);
                    return getString(R.string.deleted);
                } catch (Exception exception) {
                    return exception.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((TextView) getView().findViewById(R.id.deleteResult)).setText(result);
            }
        }.execute(key);

        return false;
    }
}
