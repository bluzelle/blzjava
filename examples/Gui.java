import com.bluzelle.Bluzelle;
import com.bluzelle.GasInfo;
import com.bluzelle.LeaseInfo;

import javax.swing.*;
import java.awt.*;

public class Gui {
    private static final String defaultMnemonic = "around buzz diagram captain obtain detail salon mango muffin" +
            " brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car";
    private static final String defaultEndpoint = "http://testnet.public.bluzelle.com:1317";
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Gas info"));
        panel.add(new JLabel("gas price"));
        panel.add(gasPriceField);
        panel.add(new JLabel("max gas"));
        panel.add(maxGasField);
        panel.add(new JLabel("max fee"));
        panel.add(maxFeeFiled);

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Lease info"));
        panel.add(new JLabel("days"));
        panel.add(daysField);
        panel.add(new JLabel("hours"));
        panel.add(hoursField);
        panel.add(new JLabel("minutes"));
        panel.add(minutesFiled);
        panel.add(new JLabel("seconds"));
        panel.add(secondsFiled);

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Connect"));
        panel.add(new JLabel("mnemonic"));
        final JTextField mnemonicField = new JTextField(defaultMnemonic);
        panel.add(mnemonicField);
        panel.add(new JLabel("endpoint"));
        final JTextField endpointField = new JTextField(defaultEndpoint);
        panel.add(endpointField);
        panel.add(new JLabel("uuid"));
        final JTextField uuidFiled = new JTextField();
        panel.add(uuidFiled);
        panel.add(new JLabel("chain id"));
        final JTextField chainIdField = new JTextField();
        panel.add(chainIdField);
        final JButton connectButton = new JButton("connect");
        panel.add(connectButton);
        final JLabel connectLabel = new JLabel("not connected");
        panel.add(connectLabel);
        connectButton.addActionListener(actionEvent -> connect(mnemonicField, endpointField, uuidFiled, chainIdField, connectLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Create"));
        panel.add(new JLabel("key"));
        final JTextField createKeyField = new JTextField();
        panel.add(createKeyField);
        panel.add(new JLabel("value"));
        final JTextField createValueFiled = new JTextField();
        panel.add(createValueFiled);
        final JButton createButton = new JButton("create");
        panel.add(createButton);
        final JLabel createLabel = new JLabel();
        panel.add(createLabel);
        createButton.addActionListener(actionEvent -> create(createKeyField, createValueFiled, createLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Has"));
        panel.add(new JLabel("key"));
        final JTextField hasKeyField = new JTextField();
        panel.add(hasKeyField);
        final JButton hasButton = new JButton("has");
        panel.add(hasButton);
        final JButton txHasButton = new JButton("tx has");
        panel.add(txHasButton);
        final JLabel hasLabel = new JLabel();
        panel.add(hasLabel);
        hasButton.addActionListener(actionEvent -> has(hasKeyField, false, hasLabel));
        txHasButton.addActionListener(actionEvent -> has(hasKeyField, true, hasLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Read"));
        panel.add(new JLabel("key"));
        final JTextField readKeyField = new JTextField();
        panel.add(readKeyField);
        final JButton readButton = new JButton("read");
        panel.add(readButton);
        final JButton txReadButton = new JButton("tx read");
        panel.add(txReadButton);
        final JLabel readLabel = new JLabel();
        panel.add(readLabel);
        readButton.addActionListener(actionEvent -> read(readKeyField, false, readLabel));
        txReadButton.addActionListener(actionEvent -> read(readKeyField, true, readLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Get lease"));
        panel.add(new JLabel("key"));
        final JTextField getLeaseKeyField = new JTextField();
        panel.add(getLeaseKeyField);
        final JButton getLeaseButton = new JButton("get");
        panel.add(getLeaseButton);
        final JButton txGetLeaseButton = new JButton("tx get");
        panel.add(txGetLeaseButton);
        final JLabel getLeaseLabel = new JLabel();
        panel.add(getLeaseLabel);
        getLeaseButton.addActionListener(actionEvent -> getLease(getLeaseKeyField, false, getLeaseLabel));
        txGetLeaseButton.addActionListener(actionEvent -> getLease(getLeaseKeyField, true, getLeaseLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Rename"));
        panel.add(new JLabel("key"));
        final JTextField renameKeyField = new JTextField();
        panel.add(renameKeyField);
        panel.add(new JLabel("new key"));
        final JTextField renameNewKeyFiled = new JTextField();
        panel.add(renameNewKeyFiled);
        final JButton renameButton = new JButton("rename");
        panel.add(renameButton);
        final JLabel renameLabel = new JLabel();
        panel.add(renameLabel);
        renameButton.addActionListener(actionEvent -> rename(renameKeyField, renameNewKeyFiled, renameLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Update"));
        panel.add(new JLabel("key"));
        final JTextField updateKeyField = new JTextField();
        panel.add(updateKeyField);
        panel.add(new JLabel("value"));
        final JTextField updateValueField = new JTextField();
        panel.add(updateValueField);
        final JButton updateButton = new JButton("update");
        panel.add(updateButton);
        final JLabel updateLabel = new JLabel();
        panel.add(updateLabel);
        updateButton.addActionListener(actionEvent -> update(updateKeyField, updateValueField, updateLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Renew lease"));
        panel.add(new JLabel("key"));
        final JTextField renewLeaseKeyField = new JTextField();
        panel.add(renewLeaseKeyField);
        final JButton renewLeaseButton = new JButton("renew");
        panel.add(renewLeaseButton);
        final JLabel renewLeaseLabel = new JLabel();
        panel.add(renewLeaseLabel);
        renewLeaseButton.addActionListener(actionEvent -> renewLease(renewLeaseKeyField, renewLeaseLabel));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(new JLabel("Delete"));
        panel.add(new JLabel("key"));
        final JTextField deleteKeyField = new JTextField();
        panel.add(deleteKeyField);
        final JButton deleteButton = new JButton("delete");
        panel.add(deleteButton);
        final JLabel deleteLabel = new JLabel();
        panel.add(deleteLabel);
        deleteButton.addActionListener(actionEvent -> delete(deleteKeyField, deleteLabel));

        JFrame rootFrame = new JFrame("Bluzelle");
        rootFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        rootFrame.setContentPane(new JScrollPane(panel));
        rootFrame.setMinimumSize(new Dimension(400, 400));
        rootFrame.pack();
        rootFrame.setLocationRelativeTo(null);
        rootFrame.setVisible(true);
    }

    private GasInfo getGasInfo() {
        int gasPrice = 0;
        try {
            gasPrice = Integer.parseInt(gasPriceField.getText());
            if (gasPrice < 0) {
                gasPrice = 0;
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            gasPriceField.setText("0");
        }
        int maxGas = 0;
        try {
            maxGas = Integer.parseInt(maxGasField.getText());
            if (maxGas < 0) {
                maxGas = 0;
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            maxGasField.setText("0");
        }
        int maxFee = 0;
        try {
            maxFee = Integer.parseInt(maxFeeFiled.getText());
            if (maxFee < 0) {
                maxFee = 0;
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            maxFeeFiled.setText("0");
        }
        return new GasInfo(gasPrice, maxGas, maxFee);
    }

    private LeaseInfo getLeaseInfo() {
        int days = 0;
        try {
            days = Integer.parseInt(daysField.getText());
        } catch (NumberFormatException e) {
            daysField.setText("0");
        }
        int hours = 0;
        try {
            hours = Integer.parseInt(hoursField.getText());
        } catch (NumberFormatException e) {
            hoursField.setText("0");
        }
        int minutes = 0;
        try {
            minutes = Integer.parseInt(minutesFiled.getText());
        } catch (NumberFormatException e) {
            minutesFiled.setText("0");
        }
        int seconds = 0;
        try {
            seconds = Integer.parseInt(secondsFiled.getText());
        } catch (NumberFormatException e) {
            secondsFiled.setText("0");
        }
        return new LeaseInfo(days, hours, minutes, seconds);
    }

    private void connect(JTextField mnemonicField, JTextField endpointField, JTextField uuidFiled, JTextField chainIdFiled, JLabel connectLabel) {
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
        connectLabel.setText("connecting...");

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    bluzelle = Bluzelle.connect(
                            mnemonic,
                            endpoint,
                            uuid.isEmpty() ? null : uuid,
                            chainId.isEmpty() ? null : chainId
                    );
                    result = "connected";
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                connectLabel.setText(result);
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

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    bluzelle.create(key, value, gasInfo, leaseInfo);
                    result = "created";
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
            }
        }.execute();
    }

    private void has(JTextField keyField, boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = getGasInfo();
        label.setText("reading...");

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (tx) {
                        result = String.valueOf(bluzelle.txHas(key, gasInfo));
                    } else {
                        result = String.valueOf(bluzelle.has(key));
                    }
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
            }
        }.execute();
    }

    private void read(JTextField keyField, boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = getGasInfo();
        label.setText("reading...");

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (tx) {
                        result = bluzelle.txRead(key, gasInfo);
                    } else {
                        result = bluzelle.read(key, false);
                        if (result == null) {
                            result = "null";
                        }
                    }
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
            }
        }.execute();
    }

    private void getLease(JTextField keyField, boolean tx, JLabel label) {
        if (bluzelle == null) {
            label.setText("not connected");
            return;
        }
        String key = keyField.getText();
        GasInfo gasInfo = getGasInfo();
        label.setText("reading...");

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (tx) {
                        result = String.valueOf(bluzelle.txGetLease(key, gasInfo));
                    } else {
                        result = String.valueOf(bluzelle.getLease(key));
                    }
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
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

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    bluzelle.rename(key, newKey, gasInfo);
                    result = "updated";
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
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

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    bluzelle.update(key, value, gasInfo, leaseInfo);
                    result = "updated";
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
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

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    bluzelle.renewLease(key, gasInfo, leaseInfo);
                    result = "updated";
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
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

        new SwingWorker<Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    bluzelle.delete(key, gasInfo);
                    result = "deleted";
                } catch (Exception e) {
                    result = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText(result);
            }
        }.execute();
    }
}