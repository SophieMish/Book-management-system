import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteDBDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField loginField;
    private JTextField passwordField;
    private JTextField dbField;

    public DeleteDBDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        Connection connection;
        try {
            connection = DBAPI.connect(loginField.getText(), passwordField.getText(), "");
        } catch (SQLException | IOException ex) {
            String msg = String.format("Failed to login: %s", ex.toString());
            JOptionPane.showMessageDialog(null, msg);
            throw new RuntimeException(ex);
        }

        try {
            DBAPI.dropDatabase(connection, dbField.getText());
            dispose();
        } catch (SQLException ex) {
            String msg = String.format("Failed to create user: %s", ex.toString());
            JOptionPane.showMessageDialog(null, msg);
        }

        try {
            connection.close();
        } catch (SQLException e) {
            return;
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DeleteDBDialog dialog = new DeleteDBDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
