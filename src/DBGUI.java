import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBGUI {
    private JTextField titleField;
    private JTextField authorField;
    private JSpinner yearField;
    private JButton addBookButton;
    private JButton findBookByYearButton;
    private JButton deleteBookByYearButton;
    private JTable tableView;
    private JButton createDBButton;
    private JButton clearTableButton;
    private JLabel titleLabel;
    private JTextField loginField;
    private JTextField passworField;
    private JCheckBox isAdminCheckBox;
    private JButton loginButton;
    private JButton createAndLoginButton;
    private JPanel panelMain;
    private JButton getAllButton;
    private JSpinner idField;
    private JLabel idLabel;
    private JButton updateByIDButton;
    private JButton deleteDBButton;
    private JTextField dbField;

    public static DefaultTableModel tableModel =  new DefaultTableModel();

    public  static  Connection connection = null;

    public DBGUI() {
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection == null) {
                    JOptionPane.showMessageDialog(null, "Need login first");
                    return;
                }

                try {
                    String title = titleField.getText();
                    String author = authorField.getText();
                    int year = (Integer) yearField.getValue();
                    DBAPI.addBook(connection, title, author, year);
                } catch (SQLException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    throw new RuntimeException(ex);
                }
            }
        });

        createAndLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Create user and login");
                CreateUserDialog dialog = new CreateUserDialog();
                dialog.setModal(true);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        createDBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateDBDialog dialog = new CreateDBDialog();
                dialog.setModal(true);
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        getAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Get all books");

                if (connection == null) {
                    JOptionPane.showMessageDialog(null, "Need login first");
                    return;
                }

                try  {
                    DBAPI.getAllBooks(connection, tableModel);
                } catch (SQLException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    throw new RuntimeException(ex);
                }
            }
        });

        updateByIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection == null) {
                    JOptionPane.showMessageDialog(null, "Need login first");
                    return;
                }

                try {
                    String title = titleField.getText();
                    String author = authorField.getText();
                    int year = (Integer) yearField.getValue();
                    int id = (Integer) idField.getValue();
                    DBAPI.updateBook(connection, title, author, year, id);

                } catch (SQLException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    throw new RuntimeException(ex);
                }
            }
        });

        deleteBookByYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection == null) {
                    JOptionPane.showMessageDialog(null, "Need login first");
                    return;
                }

                try {
                    int year = (Integer) yearField.getValue();
                    DBAPI.removeByYear(connection, year);
                } catch (SQLException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    throw new RuntimeException(ex);
                }
            }
        });
        findBookByYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection == null) {
                    JOptionPane.showMessageDialog(null, "Need login first");
                    return;
                }

                try {
                    int year = (Integer) yearField.getValue();
                    DBAPI.findBookByYear(connection, tableModel, year);

                } catch (SQLException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    throw new RuntimeException(ex);
                }
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        return;
                    }
                }
                try  {
                    connection = DBAPI.connect(loginField.getText(), passworField.getText(), dbField.getText());
                    JOptionPane.showMessageDialog(null, "Succesfull login");
                } catch (SQLException | IOException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    setConnectionInfo();
                }
            }
        });
        clearTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection == null) {
                    JOptionPane.showMessageDialog(null, "Need login first");
                    return;
                }

                try {
                    DBAPI.clearTable(connection);
                    JOptionPane.showMessageDialog(null, "Succesfulley clearead table");
                } catch (SQLException ex) {
                    String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
                    JOptionPane.showMessageDialog(null, msg);
                    throw new RuntimeException(ex);
                }
            }
        });
        deleteDBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch ( Exception ex ) {
                    }
                }
                DeleteDBDialog dialog = new DeleteDBDialog();
                dialog.setModal(true);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
    }

    private static void createTableView() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Year");
    };

    void setConnectionInfo() {
        String[] info;

        try {
            info = DBAPI.readConnectInfo();

            if (info.length < 3) {
                return;
            }

            loginField.setText(info[0]);
            passworField.setText(info[1]);
            dbField.setText(info[2]);
        } catch (IOException ex) {
            String msg = String.format("Action failed. Please contact the admin.\nError: %s", ex.toString());
            JOptionPane.showMessageDialog(null, msg);
            throw new RuntimeException(ex);
        }

    }

    public static void main(String[] args) {
        createTableView();

        String title = String.format("Lab4: Data Management by Sophia Mishukova");
        JFrame frame = new JFrame(title);
        frame.setDefaultLookAndFeelDecorated(true);

        DBGUI app = new DBGUI();
        app.setConnectionInfo();
        app.tableView.setModel(tableModel);

        frame.setContentPane(app.panelMain);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
