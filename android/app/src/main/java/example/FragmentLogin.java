package example;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.bluzelle.Bluzelle;

public class FragmentLogin extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.findViewById(R.id.login).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login) {
            login();
        }
    }

    private void login() {
        String mnemonic = ((EditText) getView().findViewById(R.id.mnemonic)).getText().toString();
        if (mnemonic.isEmpty()) {
            ((EditText) getView().findViewById(R.id.mnemonic)).setText(R.string.defaultMnemonic);
            return;
        }
        String endpoint = ((EditText) getView().findViewById(R.id.endpoint)).getText().toString();
        if (endpoint.isEmpty()) {
            ((EditText) getView().findViewById(R.id.endpoint)).setText(R.string.defaultEndpoint);
            return;
        }
        String uuid = ((EditText) getView().findViewById(R.id.uuid)).getText().toString();
        String chainId = ((EditText) getView().findViewById(R.id.chainId)).getText().toString();
        getView().findViewById(R.id.connect).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.login).setVisibility(View.GONE);

        new AsyncTask<String, Void, Exception>() {

            @Override
            protected Exception doInBackground(String... s) {
                try {
                    Data.bluzelle = Bluzelle.connect(s[0], s[1], s[2], s[3]);
                    return null;
                } catch (Exception exception) {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Exception result) {
                super.onPostExecute(result);
                getView().findViewById(R.id.connect).setVisibility(View.GONE);
                getView().findViewById(R.id.login).setVisibility(View.VISIBLE);
                if (result == null) {
                    ((ActivityMain) getActivity()).loginFragment = false;
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, new FragmentMain())
                            .commit();
                    getActivity().invalidateOptionsMenu();
                } else {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(mnemonic, endpoint, uuid, chainId);
    }
}
