package example;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.bluzelle.LeaseInfo;

public class DialogLease extends DialogFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    static DialogLease newInstance() {
        DialogLease dialog = new DialogLease();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_lease, container, false);
        view.findViewById(R.id.set).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.days)).setText(String.valueOf(Data.leaseInfo.days));
        ((TextView) view.findViewById(R.id.hours)).setText(String.valueOf(Data.leaseInfo.hours));
        ((TextView) view.findViewById(R.id.minutes)).setText(String.valueOf(Data.leaseInfo.minutes));
        ((TextView) view.findViewById(R.id.seconds)).setText(String.valueOf(Data.leaseInfo.seconds));
        ((EditText) view.findViewById(R.id.seconds)).setOnEditorActionListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.set) {
            return;
        }
        setLease();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (textView.getId() != R.id.seconds) {
            return true;
        }
        setLease();
        return false;
    }

    private void setLease() {
        Data.leaseInfo = new LeaseInfo(
                get(R.id.days),
                get(R.id.hours),
                get(R.id.minutes),
                get(R.id.seconds)
        );
        dismiss();
    }

    private int get(int id) {
        String input = ((EditText) getView().findViewById(id)).getText().toString();
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return 0;
        }
    }
}