package com.bluzelle;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class DialogGas extends DialogFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    static DialogGas newInstance() {
        DialogGas dialog = new DialogGas();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_gas, container, false);
        view.findViewById(R.id.set).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.gasPrice)).setText(String.valueOf(Data.gasInfo.gasPrice));
        ((TextView) view.findViewById(R.id.maxGas)).setText(String.valueOf(Data.gasInfo.maxGas));
        ((TextView) view.findViewById(R.id.maxFee)).setText(String.valueOf(Data.gasInfo.maxFee));
        ((EditText) view.findViewById(R.id.maxFee)).setOnEditorActionListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.set) {
            return;
        }
        setGas();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (textView.getId() != R.id.maxFee) {
            return true;
        }
        setGas();
        return false;
    }

    private void setGas() {
        Data.gasInfo = new GasInfo(
                get(R.id.gasPrice),
                get(R.id.maxGas),
                get(R.id.maxFee)
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