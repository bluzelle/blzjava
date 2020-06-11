import com.bluzelle.Bluzelle;
import com.bluzelle.GasInfo;
import com.bluzelle.LeaseInfo;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class Gui {
    private static final String defaultMnemonic = "around buzz diagram captain obtain detail salon mango muffin" +
            " brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car";
    private static final String defaultEndpoint = "http://dev.testnet.public.bluzelle.com:1317";
    private final JTextField gasPriceField = new JTextField("10");
    private final JTextField maxGasField = new JTextField("0");
    private final JTextField maxFeeFiled = new JTextField("0");
    private final JTextField daysField = new JTextField("0");
    private final JTextField hoursField = new JTextField("0");
    private final JTextField minutesFiled = new JTextField("10");
    private final JTextField secondsFiled = new JTextField("0");
    private Bluzelle bluzelle;

    private Gui() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Gui().createAndShow());
    }

    private void createAndShow() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());

        leftPanel.add(new JLabel("Gas info"), getHeaderConstraints());
        leftPanel.add(new JLabel("gas price"), getLabelConstraints());
        leftPanel.add(gasPriceField, getInputConstraints());
        leftPanel.add(new JLabel("max gas"), getLabelConstraints());
        leftPanel.add(maxGasField, getInputConstraints());
        leftPanel.add(new JLabel("max fee"), getLabelConstraints());
        leftPanel.add(maxFeeFiled, getInputConstraints());

        leftPanel.add(new JLabel("Lease info"), getHeaderConstraints());
        leftPanel.add(new JLabel("days"), getLabelConstraints());
        leftPanel.add(daysField, getInputConstraints());
        leftPanel.add(new JLabel("hours"), getLabelConstraints());
        leftPanel.add(hoursField, getInputConstraints());
        leftPanel.add(new JLabel("minutes"), getLabelConstraints());
        leftPanel.add(minutesFiled, getInputConstraints());
        leftPanel.add(new JLabel("seconds"), getLabelConstraints());
        leftPanel.add(secondsFiled, getInputConstraints());

        leftPanel.add(new JLabel("Connect"), getHeaderConstraints());
        leftPanel.add(new JLabel("mnemonic"), getLabelConstraints());
        final JTextArea mnemonicField = new JTextArea(defaultMnemonic);
        mnemonicField.setLineWrap(true);
        mnemonicField.setWrapStyleWord(true);
        leftPanel.add(mnemonicField, getInputConstraints());
        leftPanel.add(new JLabel("endpoint"), getLabelConstraints());
        final JTextArea endpointField = new JTextArea(defaultEndpoint);
        endpointField.setLineWrap(true);
        endpointField.setWrapStyleWord(true);
        leftPanel.add(endpointField, getInputConstraints());
        leftPanel.add(new JLabel("uuid"), getLabelConstraints());
        final JTextArea uuidFiled = new JTextArea();
        uuidFiled.setLineWrap(true);
        uuidFiled.setWrapStyleWord(true);
        leftPanel.add(uuidFiled, getInputConstraints());
        leftPanel.add(new JLabel("chain id"), getLabelConstraints());
        final JTextArea chainIdField = new JTextArea();
        chainIdField.setLineWrap(true);
        chainIdField.setWrapStyleWord(true);
        leftPanel.add(chainIdField, getInputConstraints());
        final JButton connectButton = new JButton("connect");
        leftPanel.add(connectButton, getButtonConstraints());
        final JLabel connectLabel = new JLabel("not connected");
        leftPanel.add(connectLabel, getLabelConstraints());
        connectButton.addActionListener(
                actionEvent -> connect(mnemonicField, endpointField, uuidFiled, chainIdField, connectLabel)
        );

        rightPanel.add(new JLabel("Create"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField createKeyField = new JTextField();
        rightPanel.add(createKeyField, getInputConstraints());
        rightPanel.add(new JLabel("value"), getLabelConstraints());
        final JTextField createValueFiled = new JTextField();
        rightPanel.add(createValueFiled, getInputConstraints());
        final JButton createButton = new JButton("create");
        rightPanel.add(createButton, getButtonConstraints());
        final JLabel createLabel = new JLabel();
        rightPanel.add(createLabel, getLabelConstraints());
        createButton.addActionListener(actionEvent -> create(createKeyField, createValueFiled, createLabel));

        rightPanel.add(new JLabel("Read"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField readKeyField = new JTextField();
        rightPanel.add(readKeyField, getInputConstraints());
        final JPanel readButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        final JButton readButton = new JButton("read");
        readButtons.add(readButton);
        readButtons.add(Box.createRigidArea(new Dimension(16, 0)));
        final JButton txReadButton = new JButton("tx read");
        readButtons.add(txReadButton);
        rightPanel.add(readButtons, getButtonConstraints());
        final JLabel readLabel = new JLabel();
        rightPanel.add(readLabel, getLabelConstraints());
        readButton.addActionListener(actionEvent -> read(readKeyField, false, readLabel));
        txReadButton.addActionListener(actionEvent -> read(readKeyField, true, readLabel));

        rightPanel.add(new JLabel("Has"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField hasKeyField = new JTextField();
        rightPanel.add(hasKeyField, getInputConstraints());
        final JPanel hasButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        final JButton hasButton = new JButton("has");
        hasButtons.add(hasButton);
        hasButtons.add(Box.createRigidArea(new Dimension(16, 0)));
        final JButton txHasButton = new JButton("tx has");
        hasButtons.add(txHasButton);
        rightPanel.add(hasButtons, getButtonConstraints());
        final JLabel hasLabel = new JLabel();
        rightPanel.add(hasLabel, getLabelConstraints());
        hasButton.addActionListener(actionEvent -> has(hasKeyField, false, hasLabel));
        txHasButton.addActionListener(actionEvent -> has(hasKeyField, true, hasLabel));

        rightPanel.add(new JLabel("All keys"), getHeaderConstraints());
        final JPanel keysButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        final JButton keysButton = new JButton("keys");
        keysButtons.add(keysButton);
        keysButtons.add(Box.createRigidArea(new Dimension(16, 0)));
        final JButton txKeysButton = new JButton("tx keys");
        keysButtons.add(txKeysButton);
        rightPanel.add(keysButtons, getButtonConstraints());
        final JLabel keysLabel = new JLabel();
        rightPanel.add(keysLabel, getLabelConstraints());
        keysButton.addActionListener(actionEvent -> keys(false, keysLabel));
        txKeysButton.addActionListener(actionEvent -> keys(true, keysLabel));

        rightPanel.add(new JLabel("Get lease"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField getLeaseKeyField = new JTextField();
        rightPanel.add(getLeaseKeyField, getInputConstraints());
        final JPanel getLeaseButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        final JButton getLeaseButton = new JButton("get");
        getLeaseButtons.add(getLeaseButton);
        getLeaseButtons.add(Box.createRigidArea(new Dimension(16, 0)));
        final JButton txGetLeaseButton = new JButton("tx get");
        getLeaseButtons.add(txGetLeaseButton);
        rightPanel.add(getLeaseButtons, getButtonConstraints());
        final JLabel getLeaseLabel = new JLabel();
        rightPanel.add(getLeaseLabel, getLabelConstraints());
        getLeaseButton.addActionListener(actionEvent -> getLease(getLeaseKeyField, false, getLeaseLabel));
        txGetLeaseButton.addActionListener(actionEvent -> getLease(getLeaseKeyField, true, getLeaseLabel));

        rightPanel.add(new JLabel("Update"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField updateKeyField = new JTextField();
        rightPanel.add(updateKeyField, getInputConstraints());
        rightPanel.add(new JLabel("value"), getLabelConstraints());
        final JTextField updateValueField = new JTextField();
        rightPanel.add(updateValueField, getInputConstraints());
        final JButton updateButton = new JButton("update");
        rightPanel.add(updateButton, getButtonConstraints());
        final JLabel updateLabel = new JLabel();
        rightPanel.add(updateLabel, getLabelConstraints());
        updateButton.addActionListener(actionEvent -> update(updateKeyField, updateValueField, updateLabel));

        rightPanel.add(new JLabel("Rename"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField renameKeyField = new JTextField();
        rightPanel.add(renameKeyField, getInputConstraints());
        rightPanel.add(new JLabel("new key"), getLabelConstraints());
        final JTextField renameNewKeyFiled = new JTextField();
        rightPanel.add(renameNewKeyFiled, getInputConstraints());
        final JButton renameButton = new JButton("rename");
        rightPanel.add(renameButton, getButtonConstraints());
        final JLabel renameLabel = new JLabel();
        rightPanel.add(renameLabel, getLabelConstraints());
        renameButton.addActionListener(actionEvent -> rename(renameKeyField, renameNewKeyFiled, renameLabel));

        rightPanel.add(new JLabel("Renew lease"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField renewLeaseKeyField = new JTextField();
        rightPanel.add(renewLeaseKeyField, getInputConstraints());
        final JButton renewLeaseButton = new JButton("renew");
        rightPanel.add(renewLeaseButton, getButtonConstraints());
        final JLabel renewLeaseLabel = new JLabel();
        rightPanel.add(renewLeaseLabel, getLabelConstraints());
        renewLeaseButton.addActionListener(actionEvent -> renewLease(renewLeaseKeyField, renewLeaseLabel));

        rightPanel.add(new JLabel("Delete"), getHeaderConstraints());
        rightPanel.add(new JLabel("key"), getLabelConstraints());
        final JTextField deleteKeyField = new JTextField();
        rightPanel.add(deleteKeyField, getInputConstraints());
        final JButton deleteButton = new JButton("delete");
        rightPanel.add(deleteButton, getButtonConstraints());
        final JLabel deleteLabel = new JLabel();
        rightPanel.add(deleteLabel, getLabelConstraints());
        deleteButton.addActionListener(actionEvent -> delete(deleteKeyField, deleteLabel));

        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setPreferredSize(new Dimension(300, 700));
        JScrollPane rightScrollPane = new JScrollPane(rightPanel);
        rightScrollPane.setPreferredSize(new Dimension(700, 700));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightScrollPane);
        splitPane.setOneTouchExpandable(true);

        JFrame frame = new JFrame("Bluzelle");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private GridBagConstraints getHeaderConstraints() {
        return new GridBagConstraints(
                0, // gridx
                GridBagConstraints.RELATIVE, // gridy
                1, // gridwidth
                1, // gridheight
                1, // weightx
                0, // weighty
                GridBagConstraints.CENTER, // anchor
                GridBagConstraints.NONE, // fill
                new Insets(24, 0, 0, 0), // insets(top, left, bottom, right)
                0, // ipadx
                0 // ipady
        );
    }

    private GridBagConstraints getLabelConstraints() {
        return new GridBagConstraints(
                0, // gridx
                GridBagConstraints.RELATIVE, // gridy
                1, // gridwidth
                1, // gridheight
                1, // weightx
                0, // weighty
                GridBagConstraints.WEST, // anchor
                GridBagConstraints.HORIZONTAL, // fill
                new Insets(0, 16, 2, 16), // insets(top, left, bottom, right)
                0, // ipadx
                0 // ipady
        );
    }

    private GridBagConstraints getInputConstraints() {
        return new GridBagConstraints(
                0, // gridx
                GridBagConstraints.RELATIVE, // gridy
                1, // gridwidth
                1, // gridheight
                1, // weightx
                0, // weighty
                GridBagConstraints.CENTER, // anchor
                GridBagConstraints.HORIZONTAL, // fill
                new Insets(0, 8, 0, 8), // insets(top, left, bottom, right)
                0, // ipadx
                0 // ipady
        );
    }

    private GridBagConstraints getButtonConstraints() {
        return new GridBagConstraints(
                0, // gridx
                GridBagConstraints.RELATIVE, // gridy
                1, // gridwidth
                1, // gridheight
                1, // weightx
                0, // weighty
                GridBagConstraints.WEST, // anchor
                GridBagConstraints.NONE, // fill
                new Insets(8, 8, 2, 8), // insets(top, left, bottom, right)
                0, // ipadx
                0 // ipady
        );
    }

    private GasInfo getGasInfo() {
        int gasPrice;
        try {
            gasPrice = Integer.parseInt(gasPriceField.getText());
            if (gasPrice < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            gasPrice = 0;
        }
        gasPriceField.setText(String.valueOf(gasPrice));

        int maxGas;
        try {
            maxGas = Integer.parseInt(maxGasField.getText());
            if (maxGas < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            maxGas = 0;
        }
        maxGasField.setText(String.valueOf(maxGas));

        int maxFee;
        try {
            maxFee = Integer.parseInt(maxFeeFiled.getText());
            if (maxFee < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            maxFee = 0;
        }
        maxFeeFiled.setText(String.valueOf(maxFee));

        return new GasInfo(gasPrice, maxGas, maxFee);
    }

    private LeaseInfo getLeaseInfo() {
        int days;
        try {
            days = Integer.parseInt(daysField.getText());
        } catch (NumberFormatException e) {
            days = 0;
        }
        daysField.setText(String.valueOf(days));

        int hours;
        try {
            hours = Integer.parseInt(hoursField.getText());
        } catch (NumberFormatException e) {
            hours = 0;
        }
        hoursField.setText(String.valueOf(hours));

        int minutes;
        try {
            minutes = Integer.parseInt(minutesFiled.getText());
        } catch (NumberFormatException e) {
            minutes = 0;
        }
        minutesFiled.setText(String.valueOf(minutes));

        int seconds;
        try {
            seconds = Integer.parseInt(secondsFiled.getText());
        } catch (NumberFormatException e) {
            seconds = 0;
        }
        secondsFiled.setText(String.valueOf(seconds));

        return new LeaseInfo(days, hours, minutes, seconds);
    }

    private void connect(
            JTextArea mnemonicField,
            JTextArea endpointField,
            JTextArea uuidFiled,
            JTextArea chainIdFiled,
            JLabel label
    ) {
        String mnemonic = mnemonicField.getText();
        if (mnemonic.isEmpty()) {
            mnemonicField.setText(defaultMnemonic);
            return;
        }
        String endpoint = endpointField.getText();
        if (endpoint.isEmpty()) {
            endpointField.setText(defaultEndpoint);
            return;
        }
        String uuid = uuidFiled.getText();
        String chainId = chainIdFiled.getText();
        label.setText("connecting...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    bluzelle = Bluzelle.connect(mnemonic, endpoint, uuid, chainId);
                    return "connected";
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void create(JTextField keyField, JTextField valueField, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        String value = valueField.getText();
        GasInfo gasInfo = getGasInfo();
        LeaseInfo leaseInfo = getLeaseInfo();
        label.setText("creating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    bluzelle.create(key, value, gasInfo, leaseInfo);
                    return "created";
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void read(JTextField keyField, boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = tx ? getGasInfo() : null;
        label.setText("reading...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    if (tx) {
                        return bluzelle.txRead(key, gasInfo);
                    } else {
                        return bluzelle.read(key, false);
                    }
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void has(JTextField keyField, boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = tx ? getGasInfo() : null;
        label.setText("reading...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    if (tx) {
                        return String.valueOf(bluzelle.txHas(key, gasInfo));
                    } else {
                        return String.valueOf(bluzelle.has(key));
                    }
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void keys(boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        GasInfo gasInfo = tx ? getGasInfo() : null;
        label.setText("reading...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    if (tx) {
                        return bluzelle.txKeys(gasInfo).toString();
                    } else {
                        return bluzelle.keys().toString();
                    }
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void getLease(JTextField keyField, boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = tx ? getGasInfo() : null;
        label.setText("reading...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    if (tx) {
                        return String.valueOf(bluzelle.txGetLease(key, gasInfo));
                    } else {
                        return String.valueOf(bluzelle.getLease(key));
                    }
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void update(JTextField keyField, JTextField valueField, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        String value = valueField.getText();
        GasInfo gasInfo = getGasInfo();
        LeaseInfo leaseInfo = getLeaseInfo();
        label.setText("updating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    bluzelle.update(key, value, gasInfo, leaseInfo);
                    return "updated";
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void rename(JTextField keyField, JTextField newKeyFiled, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        String newKey = newKeyFiled.getText();
        GasInfo gasInfo = getGasInfo();
        label.setText("updating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    bluzelle.rename(key, newKey, gasInfo);
                    return "updated";
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void renewLease(JTextField keyField, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = getGasInfo();
        LeaseInfo leaseInfo = getLeaseInfo();
        label.setText("updating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    bluzelle.renewLease(key, gasInfo, leaseInfo);
                    return "updated";
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }

    private void delete(JTextField keyField, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = getGasInfo();
        label.setText("deleting...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    bluzelle.delete(key, gasInfo);
                    return "deleted";
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message != null) {
                        return message;
                    }
                    return e.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    label.setText(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText(e.toString());
                }
            }
        }.execute();
    }
}